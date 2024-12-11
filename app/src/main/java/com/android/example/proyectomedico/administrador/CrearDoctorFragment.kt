package com.android.example.proyectomedico.administrador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.example.proyectomedico.R
import com.google.firebase.firestore.FirebaseFirestore

class CrearDoctorFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crear_doctor, container, false)

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los campos del formulario
        val nombreEditText: EditText = view.findViewById(R.id.etNombre)
        val emailEditText: EditText = view.findViewById(R.id.etEmail)
        val telefonoEditText: EditText = view.findViewById(R.id.etTelefono)
        val calleEditText: EditText = view.findViewById(R.id.etCalle)
        val distritoEditText: EditText = view.findViewById(R.id.etDistrito)
        val numeroEditText: EditText = view.findViewById(R.id.etNumero)
        val numeroCedulaEditText: EditText = view.findViewById(R.id.etNumeroCedula)
        val spinnerEspecialidad: Spinner = view.findViewById(R.id.spinnerEspecialidad)
        val btnRegistrarDoctor: Button = view.findViewById(R.id.btnRegistrarDoctor)

        // Configurar el Spinner con las especialidades
        val especialidades = arrayOf("Cardiología", "Pediatría", "Dermatología", "Neurología", "Ginecología")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, especialidades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEspecialidad.adapter = adapter

        // Configurar el botón de registro
        btnRegistrarDoctor.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val especialidad = spinnerEspecialidad.selectedItem.toString()
            val calle = calleEditText.text.toString().trim()
            val distrito = distritoEditText.text.toString().trim()
            val numero = numeroEditText.text.toString().trim()
            val numeroCedula = numeroCedulaEditText.text.toString().trim()

            // Validar los campos
            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || especialidad.isEmpty() ||
                calle.isEmpty() || distrito.isEmpty() || numero.isEmpty() || numeroCedula.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear un documento con los datos del doctor
            val doctorData = hashMapOf(
                "nombre" to nombre,
                "email" to email,
                "telefono" to telefono,
                "especialidad" to especialidad,
                "calle" to calle,
                "distrito" to distrito,
                "numero" to numero,
                "numeroCedula" to numeroCedula,
                "status" to "activo"  // El campo status está activo por defecto
            )

            // Registrar los datos del doctor en Firestore
            db.collection("doctores")
                .document(email)  // Usamos el email como identificador único
                .set(doctorData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Doctor registrado correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al registrar doctor: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}
