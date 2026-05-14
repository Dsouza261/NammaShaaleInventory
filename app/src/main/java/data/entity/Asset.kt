package com.nammashaalee.inventory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AssetCondition { WORKING, NEEDS_REPAIR, BROKEN }

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val serialNumber: String,
    val room: String,
    val condition: AssetCondition,
    val photoPath: String? = null,
    val issueNote: String? = null,
    val addedBy: String = "Teacher",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)