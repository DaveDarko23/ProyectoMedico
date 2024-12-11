package com.android.example.proyectomedico.paciente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.proyectomedico.Entidades.Doctor
import com.android.example.proyectomedico.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MisDoctoresFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorPacienteViewAdapter
    private val db = FirebaseFirestore.getInstance()
    private val doctorList = mutableListOf<Doctor>()

    private val especialidades = listOf(
        "Cardiología", "Pediatría", "Dermatología", "Neurología", "Ginecología"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_doctores, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewDoctores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = DoctorPacienteViewAdapter(doctorList) { doctor ->
            // Acción al hacer clic en un doctor
            // Por ejemplo, podrías abrir un detalle del doctor o mostrar información adicional
        }
        recyclerView.adapter = adapter

        fetchDoctors()

        return view
    }

    private fun fetchDoctors() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val pacienteUID = currentUser?.uid

        if (pacienteUID != null) {
            db.collection("pacientes").document(pacienteUID)
                .get()
                .addOnSuccessListener { document ->
                    doctorList.clear()
                    if (document != null && document.contains("medicos")) {
                        val medicosMap = document.get("medicos") as Map<String, String>

                        // Crear objetos Doctor basados en las especialidades
                        especialidades.forEach { especialidad ->
                            val doctorNombre = medicosMap[especialidad] ?: "Doctor aún no seleccionado"
                            val doctor = Doctor(
                                nombre = doctorNombre,
                                especialidad = especialidad
                            )
                            doctorList.add(doctor)
                        }
                    } else {
                        // Si no hay datos en "medicos", crear una lista con doctores no asignados
                        especialidades.forEach { especialidad ->
                            val doctor = Doctor(
                                nombre = "Doctor aún no seleccionado",
                                especialidad = especialidad
                            )
                            doctorList.add(doctor)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Manejo de errores al cargar los datos
                    exception.printStackTrace()
                }
        }
    }
}
