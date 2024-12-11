package com.android.example.proyectomedico.paciente

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.android.example.proyectomedico.Entidades.Doctor
import com.android.example.proyectomedico.R

class DoctorSelectionDialogFragment : DialogFragment() {

    private var onDoctorSelectedListener: ((Doctor) -> Unit)? = null

    companion object {
        private const val ARG_DOCTOR = "doctor"

        // Cambiar el método para usar Serializable
        fun newInstance(doctor: Doctor): DoctorSelectionDialogFragment {
            val fragment = DoctorSelectionDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_DOCTOR, doctor) // Usamos putSerializable para pasar el objeto Doctor
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val doctor = arguments?.getSerializable(ARG_DOCTOR) as Doctor // Obtener el doctor como Serializable

        return AlertDialog.Builder(requireContext())
            .setTitle("Confirmación")
            .setMessage("¿Seguro que quieres elegir a ${doctor.nombre} de la especialidad ${doctor.especialidad}?")
            .setPositiveButton("Sí") { _, _ ->
                onDoctorSelectedListener?.invoke(doctor)
            }
            .setNegativeButton("No", null)
            .create()
    }

    fun setOnDoctorSelectedListener(listener: (Doctor) -> Unit) {
        onDoctorSelectedListener = listener
    }
}
