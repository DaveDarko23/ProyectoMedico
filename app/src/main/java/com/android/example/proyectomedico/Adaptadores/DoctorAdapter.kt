package com.android.example.proyectomedico.Adaptadores

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.example.proyectomedico.Entidades.Doctor
import com.android.example.proyectomedico.R
import com.android.example.proyectomedico.administrador.DoctorDetallesActivity

class DoctorAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreDoctor)
        val tvEspecialidad: TextView = itemView.findViewById(R.id.tvEspecialidad)
        val tvDistrito: TextView = itemView.findViewById(R.id.tvDistrito)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_card_item, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]

        // Asignar los datos al ViewHolder
        holder.tvNombre.text = doctor.nombre
        holder.tvEspecialidad.text = "Especialidad: ${doctor.especialidad}"
        holder.tvDistrito.text = "Distrito: ${doctor.distrito}"
        holder.tvEstado.text = doctor.status.capitalize()
        holder.tvEstado.setTextColor(
            if (doctor.status == "activo") Color.GREEN else Color.RED
        )

        // Configurar el clic en el ítem
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DoctorDetallesActivity::class.java).apply {
                putExtra("doctor", doctor) // Pasar el objeto completo
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = doctorList.size
}
