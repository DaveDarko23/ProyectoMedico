package com.android.example.proyectomedico.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.example.proyectomedico.Entidades.Paciente
import com.android.example.proyectomedico.R
import com.android.example.proyectomedico.doctor.AgendarCitaActivity

class PacientesAdapter(
    private val context: Context,
    private val pacientesList: List<Paciente>
) : RecyclerView.Adapter<PacientesAdapter.PacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientesList[position]
        holder.nombreTextView.text = paciente.nombre

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AgendarCitaActivity::class.java).apply {
                putExtra("NOMBRE", paciente.nombre)
                putExtra("UID", paciente.uid)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pacientesList.size

    inner class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombre)
    }
}
