package top.cyclops.adapter.sample.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig

val pager = Pager(config = PagingConfig(pageSize = 30)) {
    AppPagingSource()
}