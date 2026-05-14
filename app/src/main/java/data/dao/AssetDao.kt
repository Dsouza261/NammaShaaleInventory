package com.nammashaalee.inventory.data.dao

import androidx.room.*
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets ORDER BY updatedAt DESC")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE condition = :condition ORDER BY updatedAt DESC")
    fun getAssetsByCondition(condition: AssetCondition): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE id = :id")
    fun getAssetById(id: Long): Flow<Asset?>

    @Query("SELECT * FROM assets WHERE name LIKE '%' || :query || '%' OR room LIKE '%' || :query || '%'")
    fun searchAssets(query: String): Flow<List<Asset>>

    @Query("SELECT COUNT(*) FROM assets")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE condition = :condition")
    fun getCountByCondition(condition: AssetCondition): Flow<Int>

    @Query("SELECT DISTINCT room FROM assets ORDER BY room")
    fun getAllRooms(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset): Long

    @Update
    suspend fun updateAsset(asset: Asset)

    @Delete
    suspend fun deleteAsset(asset: Asset)
}