package com.android.example.proyectomedico.paciente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.example.proyectomedico.R

class ConsultaCitasPacienteFragment : Fragment(R.layout.fragment_consulta_citas_paciente) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_consulta_citas_paciente, container, false)
    }
}
