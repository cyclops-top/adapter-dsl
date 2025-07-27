package top.cyclops.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView


fun <T : Any> createAdapter(block: AdapterScope<T>.() -> Unit): Adapter<T> {
    val builder = AdapterDelegate.Builder<T>()
    AdapterScope(builder).apply(block)
    return Adapter(builder.build())
}


class Adapter<T : Any> internal constructor(private val delegate: AdapterDelegate<T>) :
    RecyclerView.Adapter<AdapterItem.ViewHolder<*, out T?>>() {
    /**
     * 存储适配器的数据列表。
     *
     * 使用 [ListDataDelegate] 管理数据更新，支持数据差异比较和异步更新。
     */
    var data by ListDataDelegate(delegate.diffCallback)

    /**
     * 绑定适配器的跨度查找器到 [GridLayoutManager]。
     *
     * 该方法用于设置 [GridLayoutManager] 的 [SpanSizeLookup]，以支持不同类型列表项的跨度设置。
     *
     * @param layoutManager 要绑定的 [GridLayoutManager]。
     */
    fun bindSpanLookup(layoutManager: GridLayoutManager) {
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return delegate.lookup.getSpanSize(
                    layoutManager.spanCount, getItemViewType(position)
                )
            }
        }
    }

    /**
     * 创建新的 ViewHolder。
     *
     * 该方法用于创建适配器的 ViewHolder 实例。
     *
     * @param parent ViewHolder 的父视图组。
     * @param viewType 视图类型，用于区分不同类型的列表项。
     * @return 返回创建的 ViewHolder 实例。
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdapterItem.ViewHolder<*, out T?> {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    /**
     * 绑定数据到 ViewHolder。
     *
     * 该方法用于将数据项绑定到指定位置的 ViewHolder。
     *
     * @param holder 要绑定数据的 ViewHolder。
     * @param position 数据项在列表中的位置。
     */
    override fun onBindViewHolder(
        holder: AdapterItem.ViewHolder<*, out T?>,
        position: Int,
    ) {
        val item = data[position]
        delegate.onBindViewHolder(holder.requiredData(), item)
    }


    /**
     * 绑定数据到 ViewHolder，支持部分更新。
     *
     * 该方法用于将数据项绑定到指定位置的 ViewHolder，并支持通过 payloads 进行部分更新。
     *
     * @param holder 要绑定数据的 ViewHolder。
     * @param position 数据项在列表中的位置。
     * @param payloads 用于部分更新的数据负载。
     */
    override fun onBindViewHolder(
        holder: AdapterItem.ViewHolder<*, out T?>,
        position: Int,
        payloads: List<Any?>,
    ) {
        val item = data[position]
        delegate.onBindViewHolder(holder.requiredData(), item, payloads)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * 获取指定位置的数据项的视图类型。
     *
     * 该方法用于返回数据项在指定位置的视图类型，以便适配器根据视图类型创建合适的 ViewHolder。
     *
     * @param position 数据项在列表中的位置。
     * @return 返回数据项的视图类型。
     */
    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(data[position])
    }

    private fun AdapterItem.ViewHolder<*, out T?>.requiredData(): AdapterItem.ViewHolder<*, out T> {
        @Suppress("UNCHECKED_CAST") return this as AdapterItem.ViewHolder<*, out T>
    }
}


