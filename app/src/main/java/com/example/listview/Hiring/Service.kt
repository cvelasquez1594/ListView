package com.example.listview.Hiring


import retrofit2.http.GET

interface ApiService {
    @GET("/hiring.json")
    suspend fun getItems(): List<Item>
}

data class Item(
    val id: Int,
    val listId: Int,
    val name: String?
)