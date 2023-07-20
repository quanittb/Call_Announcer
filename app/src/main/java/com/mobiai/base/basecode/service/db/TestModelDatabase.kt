package com.mobiai.base.basecode.service.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ModelTestDB::class], version = 1)
abstract class TestModelDatabase : RoomDatabase(){

    abstract fun getModelTestDao () : ModelTestDao

    companion object {
        private var db: TestModelDatabase? = null

        fun getInstance(context: Context): TestModelDatabase {
            if (db == null) {
                synchronized(TestModelDatabase::class) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            context.applicationContext,
                            TestModelDatabase::class.java,
                            "heart.db"
                        ).allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return db!!
        }

        fun destroyInstance() {
            db = null
        }
    }
}
val Context.testModelDB: ModelTestDao get() = TestModelDatabase.getInstance(applicationContext).getModelTestDao()
