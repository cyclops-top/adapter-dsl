package top.cyclops.adapter

import androidx.viewbinding.ViewBinding

/**
 * `AdapterItemScope` 接口定义了用于配置 `RecyclerView` 适配器中单个列表项的 DSL 作用域。
 * 它提供了一系列方法来设置列表项的各种属性，如跨度、数据键、视图设置、数据绑定等。
 * 通过这个接口，可以以更简洁和直观的方式配置列表项，避免了复杂的手动设置。
 *
 * @param VB 视图绑定类的类型。
 * @param T 列表项数据的类型。
 */
@AdapterDslMarker
interface AdapterItemScope<VB : ViewBinding, T : Any> {
    /**
     * 列表项的跨度设置。
     * 可以通过这个属性设置列表项在 `GridLayoutManager` 中的跨度大小。
     */
    var span: AdapterItem.Span

    /**
     * 设置用于唯一标识列表项数据的键函数。
     * 这个键函数用于在数据更新时比较不同的数据项，确保只有真正发生变化的数据项才会被更新。
     *
     * @param block 一个接收列表项数据并返回唯一键的函数。
     */
    fun key(block: (T) -> Any)

    /**
     * 设置列表项视图的初始设置操作。
     * 这个操作会在视图创建后立即执行，通常用于设置一些固定的视图属性，如字体、颜色等。
     *
     * @param block 一个接收视图绑定对象的函数，用于执行视图设置操作。
     */
    fun setup(block: VB.() -> Unit)

    /**
     * 设置在数据绑定之前执行的操作。
     * 这个操作会在数据绑定之前执行，通常用于进行一些数据预处理或视图状态的初始化。
     *
     * @param block 一个接收 `ViewHolderBinderScope` 对象的函数，用于执行绑定前操作。
     */
    fun beforeBind(block: ViewHolderBinderScope<VB, T>.() -> Unit)

    /**
     * 设置数据绑定操作。
     * 这个操作会在数据绑定过程中执行，通常用于将数据绑定到视图上。
     *
     * @param block 一个接收 `ViewHolderBinderScope` 对象的函数，用于执行数据绑定操作。
     */
    fun bind(block: ViewHolderBinderScope<VB, T>.() -> Unit)

    /**
     * 添加一个部分更新的 `Payload` 到列表项中。
     * `Payload` 用于在数据部分更新时进行差异比较和绑定操作，避免整个视图的重新绑定。
     *
     * @param compare 一个用于比较两个数据项是否有部分内容发生变化的函数。
     * @param block 当部分内容发生变化时，执行的绑定操作函数。
     */
    fun payload(compare: (T, T) -> Boolean, block: ViewHolderBinderScope<VB, T>.() -> Unit)

    /**
     * 添加一个部分更新的 `Payload` 到列表项中，使用属性比较的方式。
     * 这个方法会自动生成一个比较函数，用于比较两个数据项的指定属性是否相同。
     *
     * @param property 一个接收列表项数据并返回指定属性的函数。
     * @param block 当部分内容发生变化时，执行的绑定操作函数。
     */
    fun payload(property: (T) -> Any?, block: ViewHolderBinderScope<VB, T>.() -> Unit) {
        payload({ o, n -> property(o) == property(n) }, block)
    }

    /**
     * 设置在数据绑定之后执行的操作。
     * 这个操作会在数据绑定之后执行，通常用于进行一些视图状态的更新或动画效果的设置。
     *
     * @param block 一个接收 `ViewHolderBinderScope` 对象的函数，用于执行绑定后操作。
     */
    fun afterBind(block: ViewHolderBinderScope<VB, T>.() -> Unit)

    companion object {
        /**
         * 创建 `AdapterItemScope` 实例的工厂方法。
         *
         * @param builder `AdapterItem.Builder` 实例，用于构建列表项的配置。
         * @return 一个新的 `AdapterItemScope` 实例。
         */
        internal operator fun <VB : ViewBinding, T : Any> invoke(
            builder: AdapterItem.Builder<VB, T>
        ): AdapterItemScope<VB, T> {
            return DefaultAdapterItemScope(builder)
        }
    }
}

/**
 * `DefaultAdapterItemScope` 类是 `AdapterItemScope` 接口的默认实现。
 * 它通过委托给 `AdapterItem.Builder` 来实现各种配置方法，将 DSL 作用域中的配置传递给构建器。
 *
 * @param VB 视图绑定类的类型。
 * @param T 列表项数据的类型。
 * @param builder `AdapterItem.Builder` 实例，用于构建列表项的配置。
 */
private class DefaultAdapterItemScope<VB : ViewBinding, T : Any>(val builder: AdapterItem.Builder<VB, T>) :
    AdapterItemScope<VB, T> {

    override var span: AdapterItem.Span
        get() = builder.span
        set(value) {
            builder.span = value
        }

    override fun key(block: (T) -> Any) {
        builder.keyLoader = block
    }

    override fun setup(block: VB.() -> Unit) {
        builder.setup = block
    }

    override fun beforeBind(block: ViewHolderBinderScope<VB, T>.() -> Unit) {
        builder.beforeBind = block
    }

    override fun bind(block: ViewHolderBinderScope<VB, T>.() -> Unit) {
        builder.bind = block
    }

    override fun payload(
        compare: (T, T) -> Boolean,
        block: ViewHolderBinderScope<VB, T>.() -> Unit,
    ) {
        builder.add(AdapterItem.Payload(compare, block))
    }

    override fun afterBind(block: ViewHolderBinderScope<VB, T>.() -> Unit) {
        // Bug 修复: 调用 builder 的 afterBind 属性并赋值
        builder.afterBind = block
    }
}
