package top.cyclops.adapter.sample

import top.cyclops.adapter.AdapterItem
import top.cyclops.adapter.createAdapter
import top.cyclops.adapter.sample.databinding.ItemContentBinding
import top.cyclops.adapter.sample.databinding.ItemTitleBinding

fun createMyAdapter() = createAdapter<Item> {
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