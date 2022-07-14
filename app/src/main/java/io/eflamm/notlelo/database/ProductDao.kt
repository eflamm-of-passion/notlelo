package io.eflamm.notlelo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.eflamm.notlelo.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Query("SELECT * FROM Product WHERE uuid = :uuid")
    fun getProductByUuid(uuid: String): Flow<Product>
}