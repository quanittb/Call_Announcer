package com.mobiai.base.basecode.service.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey

@Entity(tableName = "table_test")
class ModelTestDB(@PrimaryKey (autoGenerate = true)val id: Long? = null, @ColumnInfo var name : String, @ColumnInfo(name = "time_insert") var  time : Long )


@Dao

interface ModelTestDao{
    @Insert
    fun insertModelTest(heart: ModelTestDB)

}