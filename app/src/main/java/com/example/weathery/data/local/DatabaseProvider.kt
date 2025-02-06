package com.example.weathery.data.local

import android.content.Context
import androidx.room.Room

/**
 * Room DB 인스턴스를 제공하는 싱글톤 개체
 */

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "weathery"
            )
                .fallbackToDestructiveMigration() // 기존 데이터를 삭제하고 새로운 테이블 생성
                .build()
            INSTANCE = instance
            instance
        }
    }
}
