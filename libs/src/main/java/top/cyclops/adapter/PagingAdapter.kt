package top.cyclops.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup

/**
 * 创建一个 `PagingAdapter` 实例的工厂函数。
 *
 * 该函数使用 DSL 风格的配置块来设置 `AdapterScope`，允许用户方便地定义列表项和占位符。
 *
 * @param T 列表项数据的类型。
 * @param block 用于配置 `AdapterScope` 的 DSL 块。
 * @return 一个新的 `PagingAdapter` 实例。
 */
fun <T : Any> createPagingAdapter(block: AdapterScope<T>.() -> Unit): PagingAdapter<T> {
    val builder = AdapterDelegate.Builder<T>()
    AdapterScope(builder).apply(block)
    return PagingAdapter(builder.build())
}

/**
 * `PagingAdapter` 类是一个继承自 `PagingDataAdapter` 的自定义适配器，用于处理分页数据。
 * 它使用 `AdapterDelegate` 来委托列表项的创建和绑定操作，提高代码的可维护性和可扩展性。
 *
 * @param T 列表项数据的类型。
 * @param delegate `AdapterDelegate` 实例，用于管理列表项的配置和操作。
 */
class PagingAdapter<T : Any> internal constructor(private val delegate: AdapterDelegate<T>) :
    PagingDataAdapter<T, AdapterItem.ViewHolder<*, out T?>>(
        delegate.diffCallback
    ) {
    /**
     * 将 `GridLayoutManager` 的 `SpanSizeLookup` 绑定到 `AdapterDelegate` 的 `SpanLookup`。
     * 这允许根据列表项的配置动态设置每个列表项的跨度大小。
     *
     * @param layoutManager 要绑定的 `GridLayoutManager` 实例。
     */
    fun bindSpanLookup(layoutManager: GridLayoutManager) {
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            /**
             * 获取指定位置列表项的跨度大小。
             *
             * @param position 列表项的位置。
             * @return 列表项的跨度大小。
             */
            override fun getSpanSize(position: Int): Int {
                return delegate.lookup.getSpanSize(
                    layoutManager.spanCount,
                    getItemViewType(position)
                )
            }
        }
    }

    /**
     * 创建列表项的 `ViewHolder`。
     *
     * @param parent 列表项的父视图组。
     * @param viewType 列表项的视图类型。
     * @return 一个新的 `AdapterItem.ViewHolder` 实例。
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdapterItem.ViewHolder<*, out T?> {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    /**
     * 绑定列表项的数据到 `ViewHolder`。
     *
     * @param holder 要绑定数据的 `AdapterItem.ViewHolder` 实例。
     * @param position 列表项的位置。
     */
    override fun onBindViewHolder(
        holder: AdapterItem.ViewHolder<*, out T?>,
        position: Int,
    ) {
        val item = getItem(position)
        delegate.onBindViewHolder(holder.requiredData(), item)
    }

    /**
     * 绑定列表项的数据到 `ViewHolder`，支持部分更新。
     *
     * @param holder 要绑定数据的 `AdapterItem.ViewHolder` 实例。
     * @param position 列表项的位置。
     * @param payloads 部分更新的负载数据列表。
     */
    override fun onBindViewHolder(
        holder: AdapterItem.ViewHolder<*, out T?>,
        position: Int,
        payloads: List<Any?>,
    ) {
        val item = getItem(position)
        delegate.onBindViewHolder(holder.requiredData(), item, payloads)
    }

    /**
     * 获取指定位置列表项的视图类型。
     *
     * @param position 列表项的位置。
     * @return 列表项的视图类型。
     */
    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(peek(position))
    }

    /**
     * 将 `AdapterItem.ViewHolder<*, out T?>` 转换为 `AdapterItem.ViewHolder<*, out T>`。
     *
     * @return 转换后的 `AdapterItem.ViewHolder` 实例。
     */
    private fun AdapterItem.ViewHolder<*, out T?>.requiredData(): AdapterItem.ViewHolder<*, out T> {
        @Suppress("UNCHECKED_CAST") return this as AdapterItem.ViewHolder<*, out T>
    }
}