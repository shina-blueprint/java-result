# java-result [![Java CI with Gradle](https://github.com/shina-blueprint/java-result/actions/workflows/gradle.yml/badge.svg)](https://github.com/shina-blueprint/java-result/actions/workflows/gradle.yml)

**Rust-like Result type written in Java**

## Overview

This project provides Rust-like Result type for Java application.

Result type is a type-safe means of handling success and error in computations.

## Features

- **Ok and Err Variants**: Represents success and error with two variants.
- **Functional Operations**: Abundant methods for effectively combining operations, such as `map`, `andThen`, etc.
- **Type-safe Value Extraction**: Use `ok` and `err` methods to safely retrieve values or errors.
- **Simple API**: Provides an intuitive and concise API, making error handling more explicit and manageable.
- **Algebraic Data Types**: Realizes algebraic data types, allowing for a more expressive and type-safe design.
- **Java 21 Pattern Matching**: Seamlessly integrates with Java 21 pattern matching for enhanced readability.

## Usage

```
// Creating Result
Result<Integer> okResult = new Ok<>(200);
Result<Integer> errResult = new Err<>(new RuntimeException("Service Unavailable"));

// Operations in the case of Ok
int value = okResult.unwrap();

// Operations in the case of Err
Exception error = errResult.unwrapErr();

// Pattern Matching in Java 21
String result = switch (okResult) {
    case Ok<Integer> ok -> "Success: " + ok.unwrap();
    case Err<Integer> err -> "Error: " + err.unwrapErr().getMessage();
};
```
