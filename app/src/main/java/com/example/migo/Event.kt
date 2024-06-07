package com.example.migo

import kotlin.random.Random

data class Event(
    var id: Int = Event.getID(),
    var userId: Int,
    var titulo: String = "",
    var imagem: String = "",
    var descricao: String = "",
    var flagAtivo: String = "S",
    var flagPromocao: String = "N",
    var descPromocao: String = "",
    var horario: String = "",
    var data: String = "", // Use appropriate date format later
    var cep: String = "",
    var cidade: String = "",
    var uf: String = "",
    var rua: String = "",
    var numero: Int,
    var bairro: String = ""
) {
    companion object {
        fun getID(): Int {
            return Random.nextInt(100)
        }
    }
}
