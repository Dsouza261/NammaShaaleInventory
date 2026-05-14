package com.nammashaalee.inventory.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.nammashaalee.inventory.data.dao.AssetDao
import com.nammashaalee.inventory.data.dao.HealthCheckDao
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.data.entity.HealthCheckEntry

class Converters {
    @TypeConverter
    fun fromCondition(condition: AssetCondition): String = condition.name

    @TypeConverter
    fun toCondition(value: String): AssetCondition = AssetCondition.valueOf(value)
}

@Database(
    entities = [Asset::class, HealthCheckEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun healthCheckDao(): HealthCheckDao
}