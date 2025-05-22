package top.cyclops.adapter.sample.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.cyclops.adapter.sample.Item

class AppPagingSource : PagingSource<Int, Item>() {
    override fun getRefreshKey(state: PagingState<Int, Item>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val key = params.key ?: 0
        val item = build(key..(key + params.loadSize))
        val next = item.last().id + 1

        return LoadResult.Page(item, null, next)
    }

    private fun build(range: IntRange): List<Item> {
        return range.map { it ->
            if (it % 20 == 0) {
                Item.Title(it, "this is title $it")
            } else {
                Item.Content(
                    it,
                    "this is content $it, ".repeat(10)
                )
            }
        }
    }
}
