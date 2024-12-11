package com.android.example.proyectomedico.Entidades

import java.io.Serializable

data class Doctor(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val especialidad: String = "",
    val distrito: String = "",
    val calle: String = "",
    val numero: String = "",
    val status: String = "",
    val numeroCedula: String = ""
) : Serializable
