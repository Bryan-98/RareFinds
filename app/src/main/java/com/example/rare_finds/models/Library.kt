package edu.practice.utils.shared.com.example.rare_finds.models

import android.media.Image
import java.io.Serializable

data class Library(val libId: Int, val libName: String, val libDescription: String, val genre: String, val colId: Int):Serializable
