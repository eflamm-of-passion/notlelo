package io.eflamm.notlelo

import android.hardware.lights.Light
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import io.eflamm.notlelo.databinding.LibraryActivityBinding
import io.eflamm.notlelo.model.*
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory
import io.eflamm.notlelo.viewmodel.ProductViewModel
import io.eflamm.notlelo.viewmodel.ProductViewModelFactory
import java.util.*

class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: LibraryActivityBinding
    private lateinit var selectedEvent: Event
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as NotleloApplication).productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LibraryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = this.intent.extras
        selectedEvent = bundle?.getSerializable(getString(R.string.selected_event_key)) as Event

        fillLibrary(selectedEvent.id)
    }

    private fun fillLibrary(eventId: Long) {
        eventViewModel.eventWithProducts(eventId).observe(this){ eventWithProducts ->
            val se = structureEvent(eventWithProducts.products)
            eventWithProducts.products.forEach { product ->
                val textView = TextView(this)
                textView.text = product.name
                binding.root.addView(textView)
            }
        }
    }

    private fun structureEvent(products: List<Product>): StructuredEvent {
        var structuredEvent = StructuredEvent( mutableMapOf())
        products.forEach { p ->
            var meal = structuredEvent.days.getOrDefault(p.date, LightMeal(p.meal, mutableMapOf()))
            var product = meal.products.getOrDefault(p.name, LightProduct(p.id, p.name, mutableListOf()))
            var pictures = mutableListOf<String>()

            product.pictures = pictures
            meal.products[product.name] = product
            structuredEvent.days[p.date] = meal
        }

        return structuredEvent
    }

}