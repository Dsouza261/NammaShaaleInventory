package com.nammashaalee.inventory.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_checks",
    foreignKeys = [
        ForeignKey(
            entity = Asset::class,
            parentColumns = ["id"],
            childColumns = ["assetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("assetId")]
)
data class HealthCheckEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val condition: AssetCondition,
    val note: String? = null,
    val photoPath: String? = null,
    val checkedBy: String = "Teacher",
    val checkedAt: Long = System.currentTimeMillis()
)