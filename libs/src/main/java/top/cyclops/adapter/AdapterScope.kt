package top.cyclops.adapter

import androidx.viewbinding.ViewBinding
import top.cyclops.adapter.AdapterItem.Binder
import kotlin.reflect.KClass

/**
 * `AdapterScope` 接口定义了用于配置 `RecyclerView` 适配器的 DSL 作用域。
 * 它提供了方法来添加列表项和占位符的配置，允许以简洁的方式构建适配器。
 *
 * @param T 列表项数据的基类型。
 */
@AdapterDslMarker
interface AdapterScope<T : Any> {
    /**
     * 定义列表项的配置。
     *
     * @param VB 视图绑定类的类型。
     * @param D 列表项数据的具体类型，必须是 `T` 的子类型。
     * @param type 列表项数据的 `KClass`。
     * @param creator 用于创建视图绑定实例的函数。
     * @param block 用于配置列表项的 DSL 块。
     */
    fun <VB : ViewBinding, D : T> item(
        type: KClass<D>,
        creator: ViewBindingCreator<VB>,
        block: AdapterItemScope<VB, D>.() -> Unit,
    )

    /**
     * 定义适配器的占位符配置。
     *
     * @param VB 视图绑定类的类型。
     * @param creator 用于创建视图绑定实例的函数。
     * @param typeId 占位符的类型 ID，默认为 `Int.MAX_VALUE`。
     * @param span 占位符的跨度设置，默认为 `AdapterItem.Span.Size(1)`。
     * @param block 用于设置占位符视图的 DSL 块。
     */
    fun <VB : ViewBinding> placeholder(
        creator: ViewBindingCreator<VB>,
        typeId: Int = Int.MAX_VALUE,
        span: AdapterItem.Span = AdapterItem.Span.Size(1),
        block: VB.() -> Unit,
    )

    companion object {
        /**
         * 创建 `AdapterScope` 实例的工厂方法。
         *
         * @param builder `AdapterDelegate.Builder` 实例，用于构建适配器的配置。
         * @return 一个新的 `AdapterScope` 实例。
         */
        internal operator fun <T : Any> invoke(builder: AdapterDelegate.Builder<T>): AdapterScope<T> {
            return DefaultAdapterScope(builder)
        }
    }
}

/**
 * `DefaultAdapterScope` 类是 `AdapterScope` 接口的默认实现。
 * 它通过委托给 `AdapterDelegate.Builder` 来实现各种配置方法，将 DSL 作用域中的配置传递给构建器。
 *
 * @param T 列表项数据的基类型。
 * @param builder `AdapterDelegate.Builder` 实例，用于构建适配器的配置。
 */
private class DefaultAdapterScope<T : Any>(val builder: AdapterDelegate.Builder<T>) :
    AdapterScope<T> {
    /**
     * 实现 `AdapterScope` 接口的 `item` 方法，用于配置列表项。
     *
     * @param VB 视图绑定类的类型。
     * @param D 列表项数据的具体类型，必须是 `T` 的子类型。
     * @param type 列表项数据的 `KClass`。
     * @param creator 用于创建视图绑定实例的函数。
     * @param block 用于配置列表项的 DSL 块。
     */
    override fun <VB : ViewBinding, D : T> item(
        type: KClass<D>,
        creator: ViewBindingCreator<VB>,
        block: AdapterItemScope<VB, D>.() -> Unit,
    ) {
        val itemBuilder = AdapterItem.Builder(type, creator)
        AdapterItemScope(itemBuilder).apply(block)
        builder.add(itemBuilder.build())
    }

    /**
     * 实现 `AdapterScope` 接口的 `placeholder` 方法，用于配置占位符。
     *
     * @param VB 视图绑定类的类型。
     * @param creator 用于创建视图绑定实例的函数。
     * @param typeId 占位符的类型 ID。
     * @param span 占位符的跨度设置。
     * @param block 用于设置占位符视图的 DSL 块。
     */
    override fun <VB : ViewBinding> placeholder(
        creator: ViewBindingCreator<VB>,
        typeId: Int,
        span: AdapterItem.Span,
        block: VB.() -> Unit,
    ) {
        val binder = Binder<VB, T?>(
            setup = block,
            beforeBind = null,
            bind = {},
            payloadBinds = emptyList(),
            afterBind = null
        )
        builder.placeHolder = AdapterItem.PlaceHolder(
            typeId = typeId, viewCreator = creator, binder = binder,
            config = AdapterItem.Config(span)
        )
    }
}