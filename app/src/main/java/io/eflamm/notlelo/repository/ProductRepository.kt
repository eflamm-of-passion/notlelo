package io.eflamm.notlelo.repository

import androidx.annotation.WorkerThread
import io.eflamm.notlelo.database.ProductDao
import io.eflamm.notlelo.model.Product

class ProductRepository(private val productDao: ProductDao) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(product: Product) {
        productDao.insert(product)
    }
}