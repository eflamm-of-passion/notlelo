package io.eflamm.notlelo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.eflamm.notlelo.model.Product
import io.eflamm.notlelo.repository.ProductRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    fun insert(product: Product) = viewModelScope.launch {
        repository.insert(product)
    }
}

class ProductViewModelFactory(private val repository: ProductRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}