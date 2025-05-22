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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdapterItem.ViewHolder<*, out T?> {
        return delegate.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(
        holder: AdapterItem.ViewHolder<*, out T?>,
        position: Int,
    ) {
        val item = data[position]
        delegate.onBindViewHolder(holder.requiredData(), item)
    }

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

    override fun getItemViewType(position: Int): Int {
        return delegate.getItemViewType(data[position])
    }

    private fun AdapterItem.ViewHolder<*, out T?>.requiredData(): AdapterItem.ViewHolder<*, out T> {
        @Suppress("UNCHECKED_CAST") return this as AdapterItem.ViewHolder<*, out T>
    }
}


