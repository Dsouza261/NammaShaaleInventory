package com.nammashaalee.inventory.data.database

import android.content.Context
import androidx.room.Room
import com.nammashaalee.inventory.data.dao.AssetDao
import com.nammashaalee.inventory.data.dao.HealthCheckDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "namma_shaale_db"
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideAssetDao(db: AppDatabase): AssetDao = db.assetDao()

    @Provides
    fun provideHealthCheckDao(db: AppDatabase): HealthCheckDao = db.healthCheckDao()
}