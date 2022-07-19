package io.eflamm.notlelo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.eflamm.notlelo.model.Product
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Query("SELECT * FROM Product WHERE uuid = :uuid")
    fun getProductByUuid(uuid: String): Flow<Product>

    @Query("DELETE FROM Product WHERE uuid = (:uuid)")
    suspend fun deleteByUuid(uuid: UUID)
}