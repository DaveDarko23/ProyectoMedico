package com.android.example.proyectomedico.administrador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.proyectomedico.Adaptadores.DoctorAdapter
import com.android.example.proyectomedico.Entidades.Doctor
import com.android.example.proyectomedico.R
import com.google.firebase.firestore.FirebaseFirestore

class ListaDoctoresFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private val doctorList = mutableListOf<Doctor>()
    private val filteredDoctorList = mutableListOf<Doctor>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_doctores, container, false)

        val searchView: SearchView = view.findViewById(R.id.searchViewDoctores)
        recyclerView = view.findViewById(R.id.recyclerViewDoctores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        doctorAdapter = DoctorAdapter(filteredDoctorList) // Usamos la lista filtrada
        recyclerView.adapter = doctorAdapter

        // Escuchar cambios en la colección de doctores
        listenForDoctorChanges()

        // Configurar el buscador
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // No hacemos nada al presionar "Enter"
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDoctors(newText)
                return true
            }
        })

        return view
    }

    private fun listenForDoctorChanges() {
        val db = FirebaseFirestore.getInstance()

        // Obtener la referencia a la colección de doctores
        val doctorRef = db.collection("doctores")

        // Escuchar en tiempo real por cambios en la colección
        doctorRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            // Limpiar las listas
            doctorList.clear()
            filteredDoctorList.clear()

            // Procesar los documentos
            value?.documents?.forEach { document ->
                val doctor = document.toObject(Doctor::class.java)
                if (doctor != null) {
                    doctorList.add(doctor)
                }
            }

            // Actualizar la lista filtrada con todos los doctores
            filteredDoctorList.addAll(doctorList)
            doctorAdapter.notifyDataSetChanged()
        }
    }


    private fun filterDoctors(query: String?) {
        filteredDoctorList.clear()
        if (query.isNullOrEmpty()) {
            filteredDoctorList.addAll(doctorList) // Mostrar todos si el texto está vacío
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredDoctorList.addAll(
                doctorList.filter { doctor ->
                    doctor.nombre.lowercase().contains(lowerCaseQuery)
                }
            )
        }
        doctorAdapter.notifyDataSetChanged()
    }
}
