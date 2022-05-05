package edu.practice.utils.shared.com.example.rare_finds.models

import android.media.Image
import java.io.Serializable

data class Collection(val ColId:Int, val colName: String, val colDescription: String, val Price: Double, val genre: String, val userId: Int):Serializable
