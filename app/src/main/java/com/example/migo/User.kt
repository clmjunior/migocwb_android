package com.example.migo

import kotlin.random.Random

data class User(
    var id: Int = User.getID(),
    var name : String = "",
    var last_name : String = "",
    var email : String = "",
    var password : String = "",
    var cep : String = "",
    var number : String = "",
    var uf : String = "",
    var city : String = "",
    var neighborhood : String = "",
    var street : String = "",
)
{

    companion object{
        fun getID():Int {
            return Random.nextInt(100)
        }
    }

}