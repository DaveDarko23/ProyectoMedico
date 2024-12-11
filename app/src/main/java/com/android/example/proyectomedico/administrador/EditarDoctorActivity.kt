package com.android.example.proyectomedico.administrador

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.proyectomedico.R
import com.google.firebase.firestore.FirebaseFirestore

class EditarDoctorActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_doctor)

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los campos del formulario
        val nombreEditText: EditText = findViewById(R.id.etNombre)
        val emailEditText: EditText = findViewById(R.id.etEmail)
        val telefonoEditText: EditText = findViewById(R.id.etTelefono)
        val calleEditText: EditText = findViewById(R.id.etCalle)
        val distritoEditText: EditText = findViewById(R.id.etDistrito)
        val numeroEditText: EditText = findViewById(R.id.etNumero)
        val numeroCedulaEditText: EditText = findViewById(R.id.etNumeroCedula)
        val spinnerEspecialidad: Spinner = findViewById(R.id.spinnerEspecialidad)
        val btnActualizarDoctor: Button = findViewById(R.id.btnActualizarDoctor)

        // Configurar el Spinner con las especialidades
        val especialidades = arrayOf("Cardiología", "Pediatría", "Dermatología", "Neurología", "Ginecología")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, especialidades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEspecialidad.adapter = adapter

        // Obtener el email del doctor desde el intent (pasado desde la actividad anterior)
        val email = intent.getStringExtra("emailDoctor")

        if (email != null) {
            // Cargar los datos del doctor para editar
            db.collection("doctores").document(email).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nombreEditText.setText(document.getString("nombre"))
                        emailEditText.setText(document.getString("email"))
                        telefonoEditText.setText(document.getString("telefono"))
                        calleEditText.setText(document.getString("calle"))
                        distritoEditText.setText(document.getString("distrito"))
                        numeroEditText.setText(document.getString("numero"))
                        numeroCedulaEditText.setText(document.getString("numeroCedula"))

                        // Seleccionar la especialidad en el spinner
                        val especialidad = document.getString("especialidad")
                        val especialidadesList = especialidades.toList()
                        val position = especialidadesList.indexOf(especialidad)
                        spinnerEspecialidad.setSelection(position)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Configurar el botón de actualización
        btnActualizarDoctor.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val especialidad = spinnerEspecialidad.selectedItem.toString()
            val calle = calleEditText.text.toString().trim()
            val distrito = distritoEditText.text.toString().trim()
            val numero = numeroEditText.text.toString().trim()
            val numeroCedula = numeroCedulaEditText.text.toString().trim()

            // Validar los campos
            if (nombre.isEmpty() || telefono.isEmpty() || especialidad.isEmpty() ||
                calle.isEmpty() || distrito.isEmpty() || numero.isEmpty() || numeroCedula.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear un objeto con los datos actualizados
            val doctorData = hashMapOf(
                "nombre" to nombre,
                "email" to email,  // Mantener el email para no cambiarlo
                "telefono" to telefono,
                "especialidad" to especialidad,
                "calle" to calle,
                "distrito" to distrito,
                "numero" to numero,
                "numeroCedula" to numeroCedula,
                "status" to "activo"  // Mantener el estado como "activo"
            )

            // Actualizar los datos del doctor en Firestore
            if (email != null) {
                db.collection("doctores").document(email).set(doctorData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Doctor actualizado correctamente", Toast.LENGTH_SHORT).show()
                        finish()  // Finalizar la actividad después de la actualización
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar doctor: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Configurar el botón de "regresar"
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
