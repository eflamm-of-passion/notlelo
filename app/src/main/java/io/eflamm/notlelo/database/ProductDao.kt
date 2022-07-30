package io.eflamm.notlelo.database

import androidx.room.*
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

    @Query("DELETE FROM Product WHERE id NOT IN (SELECT DISTINCT product_id FROM Picture)")
    suspend fun clearEmptyProducts()

    @MapInfo(keyColumn = "name", valueColumn = "count")
    @Query("SELECT DISTINCT name, COUNT(name) AS count FROM Product GROUP BY name ORDER BY count LIMIT :numberOfNames")
    fun getProductOccurrence(numberOfNames: Int): Flow<Map<String, Int>>
}