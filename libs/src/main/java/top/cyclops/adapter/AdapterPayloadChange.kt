package top.cyclops.adapter

/**
 * `AdapterPayloadChange` 密封接口定义了 `RecyclerView` 适配器中数据更新时的不同类型的负载变化情况。
 * 它包含三种状态：无变化、全部更新和部分更新，用于在数据更新时通知适配器哪些数据项发生了变化。
 */
sealed interface AdapterPayloadChange {
    /**
     * `None` 数据对象表示数据没有发生任何变化，不需要进行更新操作。
     */
    data object None : AdapterPayloadChange

    /**
     * `All` 数据对象表示数据发生了全部更新，需要重新绑定整个视图。
     */
    data object All : AdapterPayloadChange

    /**
     * `Part` 数据类表示数据发生了部分更新，只需要更新指定位置的视图。
     *
     * @param payload 一个包含需要更新的视图位置的列表。
     */
    data class Part(val payload: List<Int>) : AdapterPayloadChange
}