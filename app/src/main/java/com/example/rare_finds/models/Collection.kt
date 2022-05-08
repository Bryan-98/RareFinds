package edu.practice.utils.shared.com.example.rare_finds.models

import android.media.Image
import java.io.Serializable

data class Collection(val colId:Int, val colName: String, val colDescription: String, val price: Double, val genre: String, val userId: Int):Serializable
