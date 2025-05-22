package top.cyclops.adapter.sample.paging

import top.cyclops.adapter.AdapterItem
import top.cyclops.adapter.createPagingAdapter
import top.cyclops.adapter.sample.Item
import top.cyclops.adapter.sample.databinding.ItemContentBinding
import top.cyclops.adapter.sample.databinding.ItemTitleBinding

fun createMyPagingAdapter() = createPagingAdapter<Item> {
    item(Item.Title::class, ItemTitleBinding::inflate) {
        key { it.id }
        span = AdapterItem.Span.Full
        payload(Item.Title::text) {
            binding.title.text = data.text
        }
    }
    item(Item.Content::class, ItemContentBinding::inflate) {
        key { it.id }
        payload(Item.Content::text) {
            binding.title.text = data.text
        }
    }
}