# AdapterDsl

[![](https://img.shields.io/badge/adapter--dsl-1.0.0-blueviolet?logo=android)](https://github.com/cyclops-top/adapter-dsl)

[中文](README_CN.md) [API](https://adapter.cyclops.top/)

## Introduction

AdapterDsl is an Android library that simplifies the process of creating and managing `RecyclerView`
adapters using a Domain-Specific Language (DSL). It provides a concise and intuitive way to define
multiple item types, handle data binding, and manage pagination.

## Features

### 1. DSL Configuration

AdapterDsl uses a DSL to configure `RecyclerView` adapters, making the code more readable and
maintainable. You can define item types, view bindings, and data payloads in a single, coherent
block.

```kotlin
fun createMyAdapter() = createAdapter<Item> {
    item(Item.Title::class, ItemTitleBinding::inflate) {
        key { it.id }
        span = AdapterItem.Span.Full
        payload(Item.Title::text) {
            binding.title.text = data.text
        }
    }
    item(Item.Content::class, ItemContentBinding::inflate) {
        key { it.id }
        payload(Item.Content::text) {
            binding.title.text = data.text
        }
    }
}
```

### 2. Multiple Item Types

Easily support multiple item types in a single `RecyclerView` adapter. Each item type can have its
own view binding, data binding logic, and span configuration.

### 3. Data Payload Handling

AdapterDsl supports data payloads, which allow for partial updates of views when only a portion of
the data has changed. This can significantly improve performance by reducing unnecessary view
redraws.

### 4. Pagination Support

The library provides built-in support for pagination using `PagingDataAdapter`. You can create a
paging adapter with the same DSL configuration as a regular adapter.

```kotlin
fun createMyPagingAdapter() = createPagingAdapter<Item> {
    item(Item.Title::class, ItemTitleBinding::inflate) {
        key { it.id }
        span = AdapterItem.Span.Full
        payload(Item.Title::text) {
            binding.title.text = data.text
        }
    }
    item(Item.Content::class, ItemContentBinding::inflate) {
        key { it.id }
        payload(Item.Content::text) {
            binding.title.text = data.text
        }
    }
}
```

### 5. Span Configuration

Configure the span size of each item type, making it easy to create grid layouts with different span
counts for different item types.

```kotlin
item(Item.Title::class, ItemTitleBinding::inflate) {
    key { it.id }
    span = AdapterItem.Span.Full
    // ...
}
```

## Installation

AdapterDsl is available on Maven. To use it in your project, add the following dependency to your
`build.gradle` file:

```groovy
implementation 'top.cyclops:adapter-dsl:x.y.z'
```

Replace `x.y.z` with the latest version of AdapterDsl.

## Usage

### 1. Define Data Models

First, define your data models as sealed interfaces or classes.

```kotlin
sealed interface Item {
    val id: Int

    data class Title(override val id: Int, val text: String) : Item
    data class Content(override val id: Int, val text: String) : Item
}
```

### 2. Create an Adapter

Use the `createAdapter` or `createPagingAdapter` function to create an adapter with the DSL
configuration.

```kotlin
val adapter = createMyAdapter()
```

### 3. Bind the Adapter to a RecyclerView

Set the adapter to a `RecyclerView` and configure the layout manager.

```kotlin
val layoutManager = GridLayoutManager(this, 8)
binding.list.layoutManager = layoutManager
adapter.bindSpanLookup(layoutManager)
binding.list.adapter = adapter
```

### 4. Submit Data

If you are using a paging adapter, submit the `PagingData` to the adapter.

```kotlin
lifecycleScope.launch {
    pager.flow.collectLatest {
        adapter.submitData(it)
    }
}
```

## Contribution

Contributions are welcome! If you find a bug or have a feature request, please open an issue on the
GitHub repository. If you want to contribute code, please fork the repository and submit a pull
request.

## License

AdapterDsl is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.