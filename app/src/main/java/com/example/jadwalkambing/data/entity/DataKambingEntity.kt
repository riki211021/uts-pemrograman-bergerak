package com.example.jadwalkambing.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kambing")
data class DataKambingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val indukan: String,
    val pejantan: String,
    val kandang: String,
    val kawin: String,
    val lahir: String,
    val vaksin: String,
    val cek: String
)
