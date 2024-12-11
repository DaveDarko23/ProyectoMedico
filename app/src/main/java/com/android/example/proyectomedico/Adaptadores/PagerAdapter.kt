package com.android.example.proyectomedico.paciente

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(activity: MenuPacienteActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Número de fragmentos

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DoctorSelectionFragment() // Fragmento para elegir doctor
            1 -> ConsultaCitasPacienteFragment() // Fragmento para ver citas
            2 -> MisDoctoresFragment() // Fragmento para ver citas
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
