package com.example.migo

data class Address(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String, // Cidade
    val uf: String, // Estado
    val ibge: String,
    val gia: String,
    val ddd: String,
    val siafi: String,
    val erro: Boolean = false
)