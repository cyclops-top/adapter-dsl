package top.cyclops.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * `AdapterItem` 类封装了与 RecyclerView 适配器列表项相关的核心逻辑和数据结构。
 * 它提供了创建、配置和管理列表项的功能，包括视图绑定、数据差异比较、部分更新等。
 */
class AdapterItem {
    /**
     * `Payload` 类表示列表项的部分更新信息。
     * 它包含一个比较函数和一个绑定函数，用于在数据部分更新时进行差异比较和绑定操作。
     *
     * @param compare 用于比较两个数据项是否有部分内容发生变化的函数。
     * @param bind 当部分内容发生变化时，执行的绑定操作函数。
     */
    internal class Payload<VB : ViewBinding, T>(
        val compare: (T, T) -> Boolean,
        val bind: ViewHolderBinderScope<VB, T>.() -> Unit,
    )

    /**
     * `Builder` 类用于构建 `Delegate` 实例，负责配置列表项的各种属性。
     *
     * @param type 列表项的数据类型。
     * @param viewCreator 用于创建视图绑定的函数。
     */
    internal class Builder<VB : ViewBinding, T : Any>(
        private val type: KClass<T>,
        private val viewCreator: ViewBindingCreator<VB>,
    ) {
        private val payloads = ArrayList<Payload<VB, T>>()
        var setup: (VB.() -> Unit)? = null
        var beforeBind: (ViewHolderBinderScope<VB, T>.() -> Unit)? = null
        var afterBind: (ViewHolderBinderScope<VB, T>.() -> Unit)? = null
        var span: Span = Span.Size(1)
        var bind: (ViewHolderBinderScope<VB, T>.() -> Unit)? = null
        var keyLoader: (T) -> Any = { it }

        /**
         * 添加一个部分更新的 `Payload` 到列表中。
         *
         * @param payload 要添加的 `Payload` 实例。
         * @return 返回当前 `Builder` 实例，以便进行链式调用。
         */
        fun add(payload: Payload<VB, T>) = apply {
            payloads.add(payload)
        }

        /**
         * 根据当前配置构建 `Delegate` 实例。
         *
         * @return 构建好的 `Delegate` 实例。
         */
        fun build(): Delegate<VB, T> {
            val binder = Binder(
                setup = setup,
                beforeBind = beforeBind,
                bind = bind ?: {
                    payloads.forEach { payload -> payload.bind.invoke(this) }
                },
                payloadBinds = payloads.map { it.bind },
                afterBind = afterBind
            )
            return Delegate(
                type = type,
                factory = Factory(viewCreator, binder),
                config = Config(span),
                diffCallback = DiffCallback(keyLoader, payloads.map { it.compare })
            )
        }
    }

    /**
     * `Delegate` 类封装了列表项的核心配置信息，包括数据类型、视图工厂、配置和差异比较器。
     *
     * @param type 列表项的数据类型。
     * @param factory 用于创建 `ViewHolder` 的工厂类。
     * @param config 列表项的配置信息，如跨度。
     * @param diffCallback 用于比较数据项差异的回调类。
     */
    internal class Delegate<VB : ViewBinding, T : Any>(
        val type: KClass<T>,
        val factory: Factory<VB, T>,
        val config: Config,
        val diffCallback: DiffCallback<T>,
    )

    /**
     * `Config` 类存储列表项的配置信息，目前仅包含跨度设置。
     *
     * @param span 列表项的跨度设置。
     */
    internal data class Config(val span: Span)

    /**
     * `PlaceHolder` 类表示列表中的占位项。
     * 它包含视图创建函数、绑定器和配置信息，用于创建占位项的 `ViewHolder`。
     *
     * @param typeId 占位项的类型 ID。
     * @param viewCreator 用于创建视图绑定的函数。
     * @param binder 用于绑定数据的绑定器。
     * @param config 占位项的配置信息。
     */
    internal data class PlaceHolder<VB : ViewBinding, T : Any>(
        val typeId: Int = Int.MAX_VALUE,
        val viewCreator: ViewBindingCreator<VB>,
        private val binder: Binder<VB, T?>,
        val config: Config,
    ) {
        /**
         * 创建占位项的 `ViewHolder` 实例。
         *
         * @param layoutInflater 用于加载布局的 `LayoutInflater` 实例。
         * @param parent 父视图组。
         * @return 创建好的 `ViewHolder` 实例。
         */
        fun createViewHolder(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): ViewHolder<VB, T?> {
            return ViewHolder(viewCreator(layoutInflater, parent, false), binder)
        }
    }

    /**
     * `Factory` 类用于创建 `ViewHolder` 实例。
     *
     * @param viewCreator 用于创建视图绑定的函数。
     * @param binder 用于绑定数据的绑定器。
     */
    internal class Factory<VB : ViewBinding, T>(
        private val viewCreator: ViewBindingCreator<VB>,
        private val binder: Binder<VB, T>,
    ) {
        /**
         * 创建 `ViewHolder` 实例。
         *
         * @param layoutInflater 用于加载布局的 `LayoutInflater` 实例。
         * @param parent 父视图组。
         * @return 创建好的 `ViewHolder` 实例。
         */
        fun createViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): ViewHolder<VB, T> {
            return ViewHolder(viewCreator(layoutInflater, parent, false), binder)
        }
    }

    /**
     * `Binder` 类封装了列表项的绑定逻辑，包括视图设置、绑定前操作、绑定操作、部分更新绑定操作和绑定后操作。
     *
     * @param setup 视图设置函数。
     * @param beforeBind 绑定前操作函数。
     * @param bind 绑定操作函数。
     * @param payloadBinds 部分更新绑定操作函数列表。
     * @param afterBind 绑定后操作函数。
     */
    internal class Binder<VB : ViewBinding, T>(
        val setup: (VB.() -> Unit)?,
        val beforeBind: (ViewHolderBinderScope<VB, T>.() -> Unit)?,
        val bind: ViewHolderBinderScope<VB, T>.() -> Unit,
        val payloadBinds: List<ViewHolderBinderScope<VB, T>.() -> Unit>,
        val afterBind: (ViewHolderBinderScope<VB, T>.() -> Unit)?,
    )

    /**
     * `ViewHolder` 类继承自 `RecyclerView.ViewHolder`，用于管理列表项的视图和数据绑定。
     *
     * @param binding 视图绑定实例。
     * @param binder 用于绑定数据的绑定器。
     */
    class ViewHolder<VB : ViewBinding, T> internal constructor(
        val binding: VB,
        private val binder: Binder<VB, T>,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binder.setup?.invoke(binding)
        }

        /**
         * 不安全地绑定数据，会进行类型转换。
         *
         * @param data 要绑定的数据。
         * @param payloads 部分更新信息。
         */
        internal fun bindUnSafe(data: Any?, payloads: AdapterPayloadChange) {
            @Suppress("UNCHECKED_CAST")
            bind(data as T, payloads)
        }

        /**
         * 绑定数据到视图。
         *
         * @param data 要绑定的数据。
         * @param payloads 部分更新信息。
         */
        private fun bind(data: T, payloads: AdapterPayloadChange) {
            val scope = ViewHolderBinderScope(this, data)
            when (payloads) {
                AdapterPayloadChange.All -> {
                    binder.beforeBind?.invoke(scope)
                    binder.bind(scope)
                    binder.afterBind?.invoke(scope)
                }

                is AdapterPayloadChange.Part -> {
                    binder.beforeBind?.invoke(scope)
                    payloads.payload.map {
                        binder.payloadBinds[it]
                    }.forEach { payloadBinder ->
                        payloadBinder(scope)
                    }
                    binder.afterBind?.invoke(scope)
                }

                AdapterPayloadChange.None -> {}
            }
        }
    }

    /**
     * `DiffCallback` 类继承自 `DiffUtil.ItemCallback`，用于比较数据项的差异。
     *
     * @param keyLoader 用于获取数据项唯一标识的函数。
     * @param payloadsComparators 部分更新比较函数列表。
     */
    @Suppress("UNCHECKED_CAST")
    internal class DiffCallback<T : Any>(
        val keyLoader: (T) -> Any,
        private val payloadsComparators: List<(T, T) -> Boolean>,
    ) : DiffUtil.ItemCallback<Any>() {
        /**
         * 比较两个数据项是否为同一个项。
         *
         * @param oldItem 旧的数据项。
         * @param newItem 新的数据项。
         * @return 如果是同一个项返回 `true`，否则返回 `false`。
         */
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return keyLoader(oldItem as T) == keyLoader(newItem as T)
        }

        /**
         * 比较两个数据项的内容是否相同。
         *
         * @param oldItem 旧的数据项。
         * @param newItem 新的数据项。
         * @return 如果内容相同返回 `true`，否则返回 `false`。
         */
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            val payload = getChangePayload(oldItem, newItem)
            return payload == AdapterPayloadChange.None
        }

        /**
         * 获取数据项的部分更新信息。
         *
         * @param oldItem 旧的数据项。
         * @param newItem 新的数据项。
         * @return 部分更新信息。
         */
        override fun getChangePayload(oldItem: Any, newItem: Any): AdapterPayloadChange {
            return payloadsComparators.mapIndexedNotNull { index, comparator ->
                index.takeIf { !comparator(oldItem as T, newItem as T) }
            }.let { payloads ->
                if (payloads.isEmpty()) {
                    AdapterPayloadChange.None
                } else {
                    AdapterPayloadChange.Part(payloads)
                }
            }
        }
    }

    /**
     * `Span` 接口表示列表项的跨度设置。
     */
    sealed interface Span {
        /**
         * 根据最大跨度计算当前列表项的跨度。
         *
         * @param maxCount 最大跨度。
         * @return 当前列表项的跨度。
         */
        operator fun invoke(maxCount: Int): Int

        /**
         * `Size` 类表示固定大小的跨度设置。
         *
         * @param size 固定的跨度大小。
         */
        data class Size(val size: Int) : Span {
            override fun invoke(maxCount: Int): Int {
                return size.coerceIn(1, maxCount)
            }
        }

        /**
         * `Full` 类表示占据整个跨度的设置。
         */
        data object Full : Span {
            override fun invoke(maxCount: Int): Int {
                return maxCount
            }
        }
    }
}
