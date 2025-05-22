package top.cyclops.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlin.reflect.KClass

internal class AdapterDelegate<T : Any>(
    val diffCallback: DiffUtil.ItemCallback<T>,
    val items: List<AdapterItem.Delegate<*, out T>>,
    val placeHolder: AdapterItem.PlaceHolder<*, T>?,
) {
    private lateinit var layoutInflater: LayoutInflater
    val lookup = SpanLookup()
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterItem.ViewHolder<*, out T?> {
        if (!this::layoutInflater.isInitialized) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        return if (viewType == placeHolder?.typeId) {
            placeHolder.createViewHolder(layoutInflater, parent)
        } else {
            items[viewType].factory.createViewHolder(layoutInflater, parent)
        }
    }


    fun onBindViewHolder(holder: AdapterItem.ViewHolder<*, out T>, data: T?) {
        holder.bindUnSafe(data, AdapterPayloadChange.All)
    }

    fun onBindViewHolder(
        holder: AdapterItem.ViewHolder<*, out T>,
        data: T?,
        payloads: List<Any?>,
    ) {
        if (data == null) {
            return
        }
        if (payloads.isEmpty()) {
            holder.bindUnSafe(data, AdapterPayloadChange.All)
        } else {
            val change = payloads.first() as? AdapterPayloadChange
            if (change != null) {
                holder.bindUnSafe(data, change)
            }
        }
    }


    fun getItemViewType(data: T?): Int {
        return if (data != null) {
            items.indexOfFirst {
                it.type == data::class
            }
        } else {
            placeHolder?.typeId ?: error("please set placeholder")
        }
    }

    @Suppress("USELESS_CAST")
    class DiffCallback<T : Any>(val items: Map<KClass<out T>, AdapterItem.DiffCallback<out T>>) :
        DiffUtil.ItemCallback<T>() {
        private fun getItemDiffCallback(clazz: KClass<out T>): AdapterItem.DiffCallback<out T> {
            return items[clazz]!!
        }

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            val type = oldItem::class
            if (newItem::class != type) {
                return false
            }
            val diff = getItemDiffCallback(type)

            return diff.areItemsTheSame(oldItem as T, newItem as T)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            val type = oldItem::class
            val diff = getItemDiffCallback(type)
            return diff.areContentsTheSame(oldItem, newItem)
        }

        override fun getChangePayload(oldItem: T, newItem: T): Any {
            val type = oldItem::class
            val diff = getItemDiffCallback(type)
            return diff.getChangePayload(oldItem, newItem)
        }
    }

    class Builder<T : Any> {
        private val items = ArrayList<AdapterItem.Delegate<*, out T>>()
        var placeHolder: AdapterItem.PlaceHolder<*, T>? = null
        fun add(delegate: AdapterItem.Delegate<*, out T>) = apply {
            items.add(delegate)
        }

        fun build(): AdapterDelegate<T> {
            return AdapterDelegate(
                diffCallback = DiffCallback(
                    items = items.associateBy { it.type }.mapValues { (_, v) -> v.diffCallback }
                ),
                placeHolder = placeHolder,
                items = items
            )
        }
    }

    inner class SpanLookup {
        fun getSpanSize(spanCount: Int, viewType: Int): Int {
            return if (viewType == placeHolder?.typeId) {
                placeHolder.config.span(spanCount)
            } else {
                items[viewType].config.span(spanCount)
            }
        }
    }
}