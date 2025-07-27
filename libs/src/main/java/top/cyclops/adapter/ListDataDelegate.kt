package top.cyclops.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * `ListDataDelegate` 类是一个委托类，用于管理 `RecyclerView.Adapter` 中的列表数据。
 * 它使用 `AsyncListDiffer` 来计算新旧列表之间的差异，并自动更新适配器，从而提高性能。
 *
 * @param T 列表项的数据类型。
 * @param itemCallback 用于比较列表项差异的 `DiffUtil.ItemCallback` 实例。
 */
class ListDataDelegate<T>(private val itemCallback: DiffUtil.ItemCallback<T>) :
    ReadWriteProperty<RecyclerView.Adapter<*>, List<T>> {
    // 用于计算列表差异的 AsyncListDiffer 实例
    private var diff: AsyncListDiffer<T>? = null

    /**
     * 获取当前适配器管理的列表数据。
     * 如果 `AsyncListDiffer` 尚未初始化，则会进行初始化。
     *
     * @param thisRef 委托属性所在的 `RecyclerView.Adapter` 实例。
     * @param property 委托属性的 `KProperty` 实例。
     * @return 当前管理的列表数据。
     */
    override fun getValue(
        thisRef: RecyclerView.Adapter<*>,
        property: KProperty<*>,
    ): List<T> {
        if (diff == null) {
            diff = AsyncListDiffer(thisRef, itemCallback)
        }
        return diff!!.currentList
    }

    /**
     * 设置适配器管理的列表数据。
     * 如果 `AsyncListDiffer` 尚未初始化，则会进行初始化。
     * 调用 `submitList` 方法提交新的列表数据，`AsyncListDiffer` 会自动计算差异并更新适配器。
     *
     * @param thisRef 委托属性所在的 `RecyclerView.Adapter` 实例。
     * @param property 委托属性的 `KProperty` 实例。
     * @param value 要设置的新列表数据。
     */
    override fun setValue(
        thisRef: RecyclerView.Adapter<*>,
        property: KProperty<*>,
        value: List<T>,
    ) {
        if (diff == null) {
            diff = AsyncListDiffer(thisRef, itemCallback)
        }
        diff!!.submitList(value)
    }
}
