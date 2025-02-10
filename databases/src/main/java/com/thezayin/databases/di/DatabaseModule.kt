package com.thezayin.databases.di

import androidx.room.Room
import com.thezayin.databases.dao.QrItemDao
import com.thezayin.databases.dao.ScanResultDao
import com.thezayin.databases.db.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "qrscanner_database"
        ).fallbackToDestructiveMigration().build()
    }
    single<ScanResultDao> { get<AppDatabase>().scanResultDao() }
    single<QrItemDao> { get<AppDatabase>().createItemDao() }
}