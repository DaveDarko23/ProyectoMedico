package com.android.example.proyectomedico.doctor

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.proyectomedico.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AgendarCitaActivity : AppCompatActivity() {

    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private var emailDoctor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_cita)

        val nombrePaciente = intent.getStringExtra("NOMBRE")
        val uidPaciente = intent.getStringExtra("UID")

        val textViewPaciente: TextView = findViewById(R.id.textViewPaciente)
        val textViewFecha: TextView = findViewById(R.id.textViewFecha)
        val textViewHora: TextView = findViewById(R.id.textViewHora)
        val buttonAgendar: Button = findViewById(R.id.buttonAgendar)
        val buttonFecha: Button = findViewById(R.id.buttonFecha)
        val buttonHora: Button = findViewById(R.id.buttonHora)

        textViewPaciente.text = "Paciente: $nombrePaciente"

        obtenerEmailDoctor { email ->
            emailDoctor = email
        }

        // Botón para seleccionar la fecha
        buttonFecha.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = sdf.format(Date(selection))
                textViewFecha.text = "Fecha seleccionada: $selectedDate"
            }

            datePicker.show(supportFragmentManager, "datePicker")
        }

        // Botón para seleccionar la hora
        buttonHora.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Selecciona la hora")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                textViewHora.text = "Hora seleccionada: $selectedTime"
            }

            timePicker.show(supportFragmentManager, "timePicker")
        }

        // Botón para agendar la cita
        buttonAgendar.setOnClickListener {
            if (selectedDate != null && selectedTime != null && uidPaciente != null && emailDoctor != null) {
                val cita = hashMapOf(
                    "uidPaciente" to uidPaciente,
                    "nombrePaciente" to nombrePaciente,
                    "fecha" to selectedDate,
                    "hora" to selectedTime,
                    "emailDoctor" to emailDoctor
                )

                firestore.collection("citas")
                    .add(cita)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cita agendada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al agendar cita: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerEmailDoctor(callback: (String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val doctorEmail = currentUser?.email

        if (doctorEmail != null) {
            firestore.collection("doctores")
                .whereEqualTo("email", doctorEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        callback(doctorEmail)
                    } else {
                        Toast.makeText(this, "No se encontró el doctor en Firestore", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener el doctor: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
        } else {
            Toast.makeText(this, "El usuario no tiene un email registrado", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }
}
