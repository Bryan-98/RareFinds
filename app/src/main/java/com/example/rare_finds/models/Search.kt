package edu.practice.utils.shared.com.example.rare_finds.models

import android.media.Image
import java.io.Serializable

data class Search(var searchId:Long, var searchName: String, var searchPublisher: String, var colDescription: String, var yearRelease: String, var genre: String, var imageUrl: String):Serializable