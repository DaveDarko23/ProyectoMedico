package com.android.example.proyectomedico

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.proyectomedico.administrador.MenuActivity
import com.android.example.proyectomedico.doctor.MenuDoctorActivity
import com.android.example.proyectomedico.paciente.MenuPacienteActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.btnSignIn).setOnClickListener { signInWithGoogle() }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    private fun saveUserDataToFirestore(user: FirebaseUser) {
        val userEmail = user.email ?: ""

        // Log para verificar el correo electrónico que estamos utilizando
        Log.d("LoginActivity", "Email del usuario: $userEmail")

        // Consulta en la colección "doctores"
        db.collection("doctores")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Si no se encuentra el correo en "doctores", verificamos si el tipo ya está establecido
                    db.collection("Contactos").document(user.uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Verificar si el tipo ya está establecido en Firestore
                                val tipoActual = document.getString("tipo")
                                if (tipoActual == null) {
                                    // Si no existe el campo "tipo", lo asignamos como "Usuario"
                                    val contacto = hashMapOf(
                                        "nombre" to (user.displayName ?: "Nombre no disponible"),
                                        "correo" to userEmail,
                                        "tipo_registro" to "Google",
                                        "fecha_registro" to FieldValue.serverTimestamp(),
                                        "uid" to user.uid,
                                        "tipo" to "Usuario"  // Tipo "Usuario" si no se encuentra el tipo
                                    )
                                    db.collection("Contactos").document(user.uid).set(contacto)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Datos de usuario guardados como Usuario", Toast.LENGTH_SHORT).show()
                                            redirectUser(user.uid)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // Si el tipo ya está establecido, no hacer nada
                                    redirectUser(user.uid)
                                }
                            } else {
                                // Si el documento no existe, lo guardamos como "Usuario"
                                val contacto = hashMapOf(
                                    "nombre" to (user.displayName ?: "Nombre no disponible"),
                                    "correo" to userEmail,
                                    "tipo_registro" to "Google",
                                    "fecha_registro" to FieldValue.serverTimestamp(),
                                    "uid" to user.uid,
                                    "tipo" to "Usuario"  // Tipo "Usuario"
                                )
                                db.collection("Contactos").document(user.uid).set(contacto)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Datos de usuario guardados como Usuario", Toast.LENGTH_SHORT).show()
                                        redirectUser(user.uid)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                } else {
                    // Si se encuentra el correo en "doctores", lo guardamos como tipo "Doctor"
                    val doctorData = hashMapOf(
                        "nombre" to (user.displayName ?: "Nombre no disponible"),
                        "correo" to userEmail,
                        "tipo_registro" to "Google",
                        "fecha_registro" to FieldValue.serverTimestamp(),
                        "uid" to user.uid,
                        "tipo" to "Doctor"  // Tipo "Doctor"
                    )
                    db.collection("Contactos").document(user.uid).set(doctorData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Datos de usuario guardados como Doctor", Toast.LENGTH_SHORT).show()
                            redirectUser(user.uid)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar el correo en doctores: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun redirectUser(uid: String) {
        // Obtener el tipo del usuario desde Firestore
        db.collection("Contactos").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tipo = document.getString("tipo") // Obtener el tipo de usuario

                    // Redirigir según el tipo
                    val intent = when (tipo) {
                        "Administrador" -> Intent(this, MenuActivity::class.java)
                        "Doctor" -> Intent(this, MenuDoctorActivity::class.java)
                        "Usuario" -> Intent(this, MenuPacienteActivity::class.java)
                        else -> {
                            Toast.makeText(this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }
                    }
                    startActivity(intent)
                    finish()  // Termina la actividad de login
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener los datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            saveUserDataToFirestore(it)
                        }
                    } else {
                        Toast.makeText(this, "Error: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
