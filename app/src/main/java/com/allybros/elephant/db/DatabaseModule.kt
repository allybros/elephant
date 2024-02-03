package com.allybros.elephant.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Created by orcun on 6.07.2022
 */

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideItemDao(appDatabase: AppDatabase): ItemDao{
        return appDatabase.itemDao()
    }


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "ToDoDB"
        ).build()
    }
}