package com.android.example.proyectomedico.administrador

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.android.example.proyectomedico.Entidades.Doctor
import com.android.example.proyectomedico.LoginActivity
import com.android.example.proyectomedico.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class DoctorDetallesActivity : AppCompatActivity() {

    var email = ""
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_detalles)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Configurar la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar el botón de regreso
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Referencias a las vistas
        val nombreTextView: TextView = findViewById(R.id.tvNombre)
        val emailTextView: TextView = findViewById(R.id.tvEmail)
        val telefonoTextView: TextView = findViewById(R.id.tvTelefono)
        val especialidadTextView: TextView = findViewById(R.id.tvEspecialidad)
        val direccionTextView: TextView = findViewById(R.id.tvDireccion)
        val numeroCedulaTextView: TextView = findViewById(R.id.tvNumeroCedula)
        val statusTextView: TextView = findViewById(R.id.tvEstado)

        // Obtener el objeto Doctor del intent
        val doctor = intent.getSerializableExtra("doctor") as Doctor

        // Mostrar los datos inicialmente
        nombreTextView.text = doctor.nombre
        emailTextView.text = doctor.email
        email = doctor.email
        telefonoTextView.text = doctor.telefono
        especialidadTextView.text = doctor.especialidad
        direccionTextView.text = "${doctor.calle}, ${doctor.distrito}, ${doctor.numero}"
        numeroCedulaTextView.text = doctor.numeroCedula
        statusTextView.text = doctor.status.capitalize()

        // Escuchar cambios en el documento del doctor en Firestore
        val doctorRef = db.collection("doctores").document(doctor.email)

        doctorRef.addSnapshotListener { snapshot: DocumentSnapshot?, e ->
            if (e != null) {
                Toast.makeText(this, "Error al escuchar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Si hay cambios, actualizamos la UI con los nuevos datos
                val updatedDoctor = snapshot.toObject(Doctor::class.java)
                updatedDoctor?.let {
                    nombreTextView.text = it.nombre
                    telefonoTextView.text = it.telefono
                    especialidadTextView.text = it.especialidad
                    direccionTextView.text = "${it.calle}, ${it.distrito}, ${it.numero}"
                    numeroCedulaTextView.text = it.numeroCedula
                    statusTextView.text = it.status.capitalize()
                }
            }
        }
    }

    // Inflar el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_doctor, menu)
        return true
    }

    // Manejar clics en los elementos del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()  // Acción de regresar
                return true
            }
            R.id.menu_opcion1 -> {
                val intent = Intent(this, EditarDoctorActivity::class.java)
                intent.putExtra("emailDoctor", email)  // Enviar el email
                startActivity(intent)

                return true
            }
            R.id.menu_opcion2 -> {
                cerrarSesion()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun cerrarSesion() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()

        // Cerrar sesión de Firebase
        FirebaseAuth.getInstance().signOut()

        // Obtener la instancia de GoogleSignInClient
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Cerrar sesión de Google
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Redirige al LoginActivity después de cerrar sesión
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                // Manejar error si ocurre
                Toast.makeText(this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
