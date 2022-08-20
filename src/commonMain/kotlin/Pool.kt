package dev.sitar.kio

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

public abstract class Pool<T>(
    public val capacity: Int,
    public val chainedCapacity: Int
) {
    public abstract val T.key: Int

    private val objects: MutableMap<Int, MutableList<T>> = mutableMapOf()
    private val lock = SynchronizedObject()

    public abstract fun generate(key: Int): T
    public abstract fun cleanse(t: T)

    public open fun acquire(key: Int): T = synchronized(lock) {
        val objs = objects[key] ?: objects.asSequence().find { it.key > key }?.value

        if (!objs.isNullOrEmpty()) {
            return objs.removeLast()
        }

        return generate(key)
    }

    public open fun recycle(t: T): Unit = synchronized(lock) {
        objects
            .takeIf { it.size < capacity }
            ?.getOrPut(t.key) { mutableListOf() }
            ?.takeIf { it.size < chainedCapacity }
            ?.add(t.also(::cleanse))
    }
}

public inline fun <T, R> Pool<T>.use(key: Int, block: (T) -> R): R {
    val t = acquire(key)

    try {
        return block(t)
    } finally {
        recycle(t)
    }
}

public abstract class EmptyPool<T> : Pool<T>(0, 0) {
    override val T.key: Int
        get() = 0

    override fun cleanse(t: T) {}

    override fun acquire(key: Int): T {
        return generate(key)
    }

    override fun recycle(t: T) {}
}

public fun <T> emptyPool(generate: (Int) -> T): EmptyPool<T> {
    return object : EmptyPool<T>() {
        override fun generate(key: Int): T {
            return generate(key)
        }
    }
}