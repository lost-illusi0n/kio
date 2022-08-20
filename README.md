# kio
A Kotlin Multiplatform IO library that supports the JS (node only), JVM, and Native targets.

## Dependency
Currently not published to any remote repository (maven central is planned). Kio must be published locally to use (or included as a composite project through Gradle).
```kotlin
implementation("dev.sitar:kio:{version}")
```

## Quickstart
Kio allows you to interface with asynchronous and synchronous data.

### Synchronous
For synchronous data, Kio provides the following interfaces:
- `AbsoluteReader`
- `AbsoluteWriter`
- `SequentialReader`
- `SequentialWriter`

A `Buffer` implements all of these interfaces and is very useful. It represents a sequence of bytes that can be indexed similar to a `ByteArray` however has the ability to grow along with the data you put into it. A `Buffer` keeps track of its current "read" and "write" heads, which lets you do sequential write/read operations.

### Asynchronous
For asynchronous data, Kio provides the following interfaces:
- `AsyncReader`
- `AsyncWriter`

These may be useful for streaming data (reading a large file), and you want to process it as you read the data. They provide similar functionality to the `SequentialReader/Writer`, and can often be used to replace Java Input/OutputStreams (see `InputStream#toAsyncReader` and `OutputStream#toAsyngWriter`).

## License
This project is published under the [MIT license](https://github.com/lost-illusi0n/kio/blob/master/LICENSE).