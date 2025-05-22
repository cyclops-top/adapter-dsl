package top.cyclops.adapter.sample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.cyclops.adapter.sample.databinding.ActivityMainBinding
import top.cyclops.adapter.sample.paging.createMyPagingAdapter
import top.cyclops.adapter.sample.paging.pager

class MainActivity : AppCompatActivity() {
    private val adapter = createMyPagingAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val layoutManager = GridLayoutManager(this, 8)
        binding.list.layoutManager = layoutManager
        adapter.bindSpanLookup(layoutManager)
        binding.list.adapter = adapter
        lifecycleScope.launch {
            pager.flow.collectLatest {
                adapter.submitData(it)
            }
        }
//        val items = (0..100).map {
//            if (it % 5 == 0) {
//                Item.Title(it, "this is title $it")
//            } else {
//                Item.Content(
//                    it,
//                    "this is content $it,this is content $it,this is content $it,this is content $it,this is content $it,this is content $it"
//                )
//            }
//        }
//        adapter.data = items
//        lifecycleScope.launch {
//            repeat(1000) { index ->
//                delay(5000)
//                adapter.data = items.map {
//                    if (it is Item.Title) {
//                        it.copy(text = "this is title [$index]")
//                    } else {
//                        it
//                    }
//                }
//            }
//        }
    }
}