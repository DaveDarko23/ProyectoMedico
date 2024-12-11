import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.proyectomedico.Adaptadores.PacientesAdapter
import com.android.example.proyectomedico.Entidades.Paciente
import com.android.example.proyectomedico.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PacientesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PacientesAdapter
    private lateinit var searchView: SearchView
    private val pacientesList = mutableListOf<Paciente>()
    private val pacientesFilteredList = mutableListOf<Paciente>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragment
        return inflater.inflate(R.layout.fragment_pacientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewPacientes)
        adapter = PacientesAdapter(requireContext(), pacientesFilteredList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Configurar SearchView
        searchView = view.findViewById(R.id.searchViewPacientes)
        configurarSearchView()

        // Obtener pacientes desde Firestore
        obtenerPacientesDelDoctor()
    }

    private fun configurarSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarPacientes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarPacientes(newText)
                return true
            }
        })
    }

    private fun filtrarPacientes(query: String?) {
        pacientesFilteredList.clear()
        if (!query.isNullOrEmpty()) {
            val queryLower = query.lowercase()
            pacientesFilteredList.addAll(pacientesList.filter { paciente ->
                paciente.nombre.lowercase().contains(queryLower)
            })
        } else {
            // Si no hay query, mostramos todos los pacientes
            pacientesFilteredList.addAll(pacientesList)
        }
        adapter.notifyDataSetChanged()
    }

    private fun obtenerPacientesDelDoctor() {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val doctorEmail = currentUser?.email

        if (doctorEmail != null) {
            firestore.collection("doctores").whereEqualTo("email", doctorEmail).get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val pacientes = document["pacientes"] as? List<Map<String, Any>>
                        if (pacientes != null) {
                            pacientesList.clear()
                            for (paciente in pacientes) {
                                val nombre = paciente["nombre"] as? String ?: "Sin nombre"
                                val uid = paciente["uid"] as? String ?: "Sin uid"

                                pacientesList.add(Paciente(nombre,uid))
                            }
                            pacientesFilteredList.clear()
                            pacientesFilteredList.addAll(pacientesList)
                            adapter.notifyDataSetChanged()
                        } else {
                            println("El campo 'pacientes' no existe o no es una lista")
                        }
                    } else {
                        println("No se encontró ningún doctor con el email: $doctorEmail")
                        Toast.makeText(requireContext(), "No se encontraron datos del doctor", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al obtener pacientes: ${exception.message}")
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            println("El doctor no está autenticado o no tiene un email")
            Toast.makeText(requireContext(), "Error: No se encontró el email del usuario autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
