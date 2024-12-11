package com.android.example.proyectomedico

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.example.proyectomedico.administrador.MenuActivity
import com.android.example.proyectomedico.doctor.MenuDoctorActivity
import com.android.example.proyectomedico.paciente.MenuPacienteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Obtener la instancia de FirebaseAuth
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Verificar en tiempo real si el usuario sigue existiendo
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser != null) {
                        // Obtener el tipo de usuario desde Firestore
                        db.collection("Contactos").document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val tipo = document.getString("tipo")
                                    when (tipo) {
                                        "Administrador" -> {
                                            val adminIntent = Intent(this, MenuActivity::class.java)
                                            startActivity(adminIntent)
                                            // No cerrar la actividad principal aquí
                                        }
                                        "Doctor" -> {
                                            val doctorIntent = Intent(this, MenuDoctorActivity::class.java)
                                            startActivity(doctorIntent)
                                            // No cerrar la actividad principal aquí
                                        }
                                        "Usuario" -> {
                                            val usuarioIntent = Intent(this, MenuPacienteActivity::class.java)
                                            startActivity(usuarioIntent)
                                            finish()
                                            // No cerrar la actividad principal aquí
                                        }
                                    }
                                } else {
                                    // Si no se encuentra el documento en Firestore
                                    redirectToLogin()
                                }
                            }
                            .addOnFailureListener {
                                // Error al obtener el tipo de usuario
                                redirectToLogin()
                            }
                    } else {
                        // Usuario eliminado, redirigir a LoginActivity
                        redirectToLogin()
                    }
                } else {
                    // Manejar error en la recarga (por ejemplo, problemas de red)
                    redirectToLogin()
                }
            }
        } else {
            // No hay usuario autenticado, redirigir a LoginActivity
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        Log.d("MainActivity", "Redirigiendo a LoginActivity")
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        // No usar finish() ni moveTaskToBack() aquí
    }
}
