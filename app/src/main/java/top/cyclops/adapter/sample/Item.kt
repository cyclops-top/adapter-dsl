package top.cyclops.adapter.sample

sealed interface Item {
    val id: Int

    data class Title(override val id: Int, val text: String) : Item
    data class Content(override val id: Int, val text: String) : Item

}