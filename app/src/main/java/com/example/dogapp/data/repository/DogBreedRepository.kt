package com.example.dogapp.data.repository

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class DogBreed(val breed: String, val subBreed: String? = null) {
    val name: String
        get() = listOfNotNull(subBreed, breed)
            .joinToString(" ") { it.replaceFirstChar { first -> first.titlecase() } }
}

class DogBreedRepository internal constructor(private val api: DogApi) {
    constructor() : this(
        Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApi::class.java)
    )

    suspend fun getBreeds(): List<DogBreed> = api.breeds().requireMessage()
        .flatMap { (breed, subBreeds) ->
            listOf(DogBreed(breed)) + subBreeds.map { DogBreed(breed, it) }
        }
        .sortedBy { it.name }

    suspend fun getImageUrl(breed: DogBreed): String {
        val response = breed.subBreed?.let { api.subBreedImage(breed.breed, it) }
            ?: api.breedImage(breed.breed)
        return response.requireMessage()
    }
}

internal interface DogApi {
    @GET("breeds/list/all")
    suspend fun breeds(): DogApiResponse<Map<String, List<String>>>

    @GET("breed/{breed}/images/random")
    suspend fun breedImage(@Path("breed") breed: String): DogApiResponse<String>

    @GET("breed/{breed}/{subBreed}/images/random")
    suspend fun subBreedImage(
        @Path("breed") breed: String,
        @Path("subBreed") subBreed: String
    ): DogApiResponse<String>
}

internal data class DogApiResponse<T>(
    @SerializedName("message") val message: T?,
    @SerializedName("status") val status: String
) {
    fun requireMessage(): T {
        check(status == "success")
        return checkNotNull(message)
    }
}
