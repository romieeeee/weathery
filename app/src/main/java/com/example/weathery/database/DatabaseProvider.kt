package com.example.weathery.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
                .addMigrations(MIGRATION_2_3)
                .build()
            INSTANCE = instance
            instance
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 기존 테이블을 삭제하기 전 새로운 임시 테이블 생성
            db.execSQL("CREATE TABLE city_temp (cityId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, cityName TEXT NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL)")
            // 기존 city 테이블에서 데이터 복사
            db.execSQL("INSERT INTO city_temp (cityId, cityName, latitude, longitude) SELECT cityId, cityName, latitude, longitude FROM city")
            // 기존 city 테이블 삭제
            db.execSQL("DROP TABLE city")
            // 임시 테이블을 city로 이름 변경
            db.execSQL("ALTER TABLE city_temp RENAME TO city")
            // 인덱스 추가
            db.execSQL("CREATE UNIQUE INDEX index_city_cityName ON city (cityName)")
        }
    }
}