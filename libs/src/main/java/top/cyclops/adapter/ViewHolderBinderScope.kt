package top.cyclops.adapter

import android.content.Context
import androidx.viewbinding.ViewBinding

/**
 * `ViewHolderBinderScope` 接口定义了在绑定 `ViewHolder` 时使用的 DSL 作用域。
 * 它提供了访问 `ViewHolder` 绑定视图、数据、位置和上下文的属性，方便在 DSL 块中操作视图和数据。
 *
 * @param VB 视图绑定类的类型。
 * @param T 列表项数据的类型。
 */
@AdapterDslMarker
interface ViewHolderBinderScope<VB : ViewBinding, T> {
    /**
     * 视图绑定实例，用于访问视图控件。
     */
    val binding: VB

    /**
     * 当前绑定的数据项。
     */
    val data: T

    /**
     * 当前 `ViewHolder` 在适配器中的位置。
     */
    val position: Int

    /**
     * 当前 `ViewHolder` 所在的上下文。
     */
    val context: Context get() = binding.root.context

    companion object {
        /**
         * 创建 `ViewHolderBinderScope` 实例的工厂方法。
         *
         * @param VB 视图绑定类的类型。
         * @param T 列表项数据的类型。
         * @param holder `AdapterItem.ViewHolder` 实例，用于获取绑定视图和位置信息。
         * @param data 当前绑定的数据项。
         * @return 一个新的 `ViewHolderBinderScope` 实例。
         */
        operator fun <VB : ViewBinding, T> invoke(
            holder: AdapterItem.ViewHolder<VB, T>,
            data: T,
        ): ViewHolderBinderScope<VB, T> {
            return DefaultViewHolderBinderScope(holder, data)
        }
    }
}

/**
 * `DefaultViewHolderBinderScope` 类是 `ViewHolderBinderScope` 接口的默认实现。
 * 它通过接收 `AdapterItem.ViewHolder` 实例和数据项，提供了对绑定视图、数据和位置的访问。
 *
 * @param VB 视图绑定类的类型。
 * @param T 列表项数据的类型。
 * @param holder `AdapterItem.ViewHolder` 实例，用于获取绑定视图和位置信息。
 * @param data 当前绑定的数据项。
 */
private class DefaultViewHolderBinderScope<VB : ViewBinding, T>(
    holder: AdapterItem.ViewHolder<VB, T>,
    override val data: T,
) : ViewHolderBinderScope<VB, T> {
    /**
     * 视图绑定实例，从 `AdapterItem.ViewHolder` 中获取。
     */
    override val binding: VB = holder.binding

    /**
     * 当前 `ViewHolder` 在适配器中的位置，从 `AdapterItem.ViewHolder` 中获取。
     */
    override val position: Int = holder.absoluteAdapterPosition
}