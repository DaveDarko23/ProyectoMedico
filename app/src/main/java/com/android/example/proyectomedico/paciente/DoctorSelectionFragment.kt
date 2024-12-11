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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

class DoctorSelectionFragment : Fragment() {

    private lateinit var doctorList: List<Doctor>
    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorPacienteViewAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_doctor_selection, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerViewDoctors)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Aquí debes cargar los datos de los doctores desde Firestore
        loadDoctors()

        return rootView
    }

    private fun loadDoctors() {
        firestore.collection("doctores")
            .get()
            .addOnSuccessListener { result ->
                doctorList = result.map { document ->
                    val doctor = document.toObject(Doctor::class.java)
                    doctor
                }
                doctorAdapter = DoctorPacienteViewAdapter(doctorList) { doctor ->
                    showConfirmationDialog(doctor)
                }
                recyclerView.adapter = doctorAdapter
            }
            .addOnFailureListener { exception ->
                Snackbar.make(requireView(), "Error al cargar doctores", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun showConfirmationDialog(doctor: Doctor) {
        val dialog = DoctorSelectionDialogFragment.newInstance(doctor)
        dialog.setOnDoctorSelectedListener { selectedDoctor ->
            saveDoctorToPatient(selectedDoctor)
        }
        dialog.show(childFragmentManager, "DoctorSelectionDialog")
    }


    private fun saveDoctorToPatient(doctor: Doctor) {
        val userUid = auth.currentUser?.uid ?: return
        val patientRef = firestore.collection("pacientes").document(userUid)

        // Crear o actualizar el documento del paciente
        val doctorMap = mapOf(doctor.especialidad to doctor.nombre)
        patientRef.set(
            mapOf(
                "paciente" to userUid,
                "medicos" to doctorMap
            ),
            SetOptions.merge() // merge = true permite que no se sobrescriban otros campos del documento
        )
            .addOnSuccessListener {
                // Ahora vamos a actualizar el documento del doctor
                savePatientToDoctor(doctor, userUid)
            }
            .addOnFailureListener {
                Snackbar.make(requireView(), "Error al guardar la selección", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun savePatientToDoctor(doctor: Doctor, patientUid: String) {
        val doctorRef = firestore.collection("doctores").document(doctor.email)

        // Crear el objeto con el UID y el nombre del paciente
        val patientInfo = mapOf(
            "uid" to patientUid,
            ("nombre" to auth.currentUser?.displayName ?: "Nombre desconocido") as Pair<Any, Any>
        )

        // Actualizar el documento del doctor para añadir este paciente a la lista
        doctorRef.update(
            "pacientes", FieldValue.arrayUnion(patientInfo) // Agrega el paciente a la lista de pacientes
        )
            .addOnSuccessListener {
                Snackbar.make(requireView(), "Doctor seleccionado con éxito", Snackbar.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Snackbar.make(requireView(), "Error al actualizar el doctor: ${exception.message}", Snackbar.LENGTH_LONG).show()
            }
    }



}
