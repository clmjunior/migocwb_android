package com.example.migo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.migo.databinding.FragmentMyEventsBinding

class MyEventsFragment : Fragment() {

    private var _binding: FragmentMyEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sqLitehelper: SQLitehelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
        sqLitehelper = SQLitehelper(requireContext())

        val loggedInUserId = sharedPreferences.getString("userId", "0")?.toInt() ?: 0
        val sqLitehelper = SQLitehelper(requireContext())
        eventAdapter = EventAdapter(emptyList(), loggedInUserId, sqLitehelper)
        binding.recyclerViewMyEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
        binding.imageView.setOnClickListener {
            findNavController().navigate(R.id.action_myEventsFragment_to_homeFragment)
        }
        loadMyEvents(loggedInUserId)
    }

    private fun loadMyEvents(userId: Int) {
        val events = sqLitehelper.getEventsByUserId(userId)
        eventAdapter.updateEvents(events)
    }

    private fun editEvent(event: Event) {
        // Implementar a lógica para abrir o fragment de edição
        // Você já tem um exemplo disso no seu fragment original
    }

    private fun deleteEvent(event: Event) {
        // Implementar a lógica para deletar o evento
        // Você já tem um exemplo disso no seu fragment original
    }

    private fun reactivateEvent(event: Event) {
        // Implementar a lógica para reativar o evento
        // Isso pode envolver atualizar o flagAtivo no banco de dados
        event.flagAtivo = "S" // Ativar o evento
        sqLitehelper.updateEvent(event) // Método para atualizar o evento no banco de dados
        loadMyEvents(event.userId) // Recarregar a lista de eventos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
