package com.example.buscacep.api

import com.example.buscacep.model.Endereco
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoAPI {

    @GET("ws/{cep}/json/")
    suspend fun recuperarEnderecoAPI(@Path("cep") cep: String) : Response<Endereco>

}