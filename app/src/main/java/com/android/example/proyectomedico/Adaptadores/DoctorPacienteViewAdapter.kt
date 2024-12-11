package com.android.example.proyectomedico.paciente

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.proyectomedico.Entidades.Doctor
import com.android.example.proyectomedico.R
import com.android.example.proyectomedico.databinding.ItemDoctorBinding

class DoctorPacienteViewAdapter(
    private val doctorList: List<Doctor>,
    private val onDoctorClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorPacienteViewAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.bind(doctor)
    }

    override fun getItemCount(): Int = doctorList.size

    inner class DoctorViewHolder(private val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: Doctor) {
            binding.tvDoctorName.text = doctor.nombre
            binding.tvDoctorSpecialty.text = doctor.especialidad

            binding.root.setOnClickListener {
                onDoctorClick(doctor)
            }
        }
    }
}
