# AdapterDsl 项目介绍

[![](https://img.shields.io/badge/adapter--dsl-1.0.0-blueviolet?logo=android)](https://github.com/cyclops-top/hilt-plus)

[En](README.md) [API](https://adapter.cyclops.top/)

# AdapterDsl

## 介绍

AdapterDsl 是一个 Android 库，它使用领域特定语言（DSL）简化了创建和管理 `RecyclerView`
适配器的过程。它提供了一种简洁直观的方式来定义多种 item 类型、处理数据绑定和管理分页。

## 特性

### 1. DSL 配置

AdapterDsl 使用 DSL 来配置 `RecyclerView` 适配器，使代码更具可读性和可维护性。你可以在一个连贯的代码块中定义
item 类型、视图绑定和数据有效负载。

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

### 2. 多 item 类型支持

可以轻松地在单个 `RecyclerView` 适配器中支持多种 item 类型。每种 item 类型都可以有自己的视图绑定、数据绑定逻辑和跨距配置。

### 3. 数据有效负载处理

AdapterDsl 支持数据有效负载，当只有部分数据发生更改时，允许对视图进行部分更新。这可以通过减少不必要的视图重绘来显著提高性能。

### 4. 分页支持

该库使用 `PagingDataAdapter` 提供了内置的分页支持。你可以使用与常规适配器相同的 DSL 配置来创建分页适配器。

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

### 5. 跨距配置

配置每种 item 类型的跨距大小，从而轻松创建具有不同跨距计数的网格布局。

```kotlin
item(Item.Title::class, ItemTitleBinding::inflate) {
    key { it.id }
    span = AdapterItem.Span.Full
    // ...
}
```

## 安装

AdapterDsl 可在 Maven 上获取。要在你的项目中使用它，请在 `build.gradle` 文件中添加以下依赖项：

```groovy
implementation 'top.cyclops:adapter-dsl:x.y.z'
```

将 `x.y.z` 替换为 AdapterDsl 的最新版本。

## 使用方法

### 1. 定义数据模型

首先，将你的数据模型定义为密封接口或类。

```kotlin
sealed interface Item {
    val id: Int

    data class Title(override val id: Int, val text: String) : Item
    data class Content(override val id: Int, val text: String) : Item
}
```

### 2. 创建适配器

使用 `createAdapter` 或 `createPagingAdapter` 函数通过 DSL 配置创建适配器。

```kotlin
val adapter = createMyAdapter()
```

### 3. 将适配器绑定到 RecyclerView

将适配器设置给 `RecyclerView` 并配置布局管理器。

```kotlin
val layoutManager = GridLayoutManager(this, 8)
binding.list.layoutManager = layoutManager
adapter.bindSpanLookup(layoutManager)
binding.list.adapter = adapter
```

### 4. 提交数据

如果你使用的是分页适配器，请将 `PagingData` 提交给适配器。

```kotlin
lifecycleScope.launch {
    pager.flow.collectLatest {
        adapter.submitData(it)
    }
}
```

## 贡献

欢迎贡献代码！如果你发现了一个 bug 或有功能请求，请在 GitHub 仓库上开一个 issue。如果你想贡献代码，请先
fork 仓库，然后提交一个 pull request。

## 许可证

AdapterDsl 采用 Apache License 2.0 许可。有关详细信息，请参阅 [LICENSE](LICENSE) 文件。 