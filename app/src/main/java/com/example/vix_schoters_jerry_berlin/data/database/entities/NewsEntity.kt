package com.example.vix_schoters_jerry_berlin.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vix_schoters_jerry_berlin.model.Result
import com.example.vix_schoters_jerry_berlin.util.Constant.NEWS_TABLE

@Entity(tableName = NEWS_TABLE)
class NewsEntity(
    var result: Result
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}