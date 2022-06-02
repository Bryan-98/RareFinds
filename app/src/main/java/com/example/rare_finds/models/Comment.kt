package edu.practice.utils.shared.com.example.rare_finds.models

import java.io.Serializable
import java.util.*

data class Comment(val comId: Int, val libName: String, val userName: String, val comment: String, val date: Date, val rating: Int, val userImage: String):Serializable
