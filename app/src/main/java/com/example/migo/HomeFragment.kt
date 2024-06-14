// HomeFragment.kt
package com.example.migo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.migo.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var eventAdapter: EventAdapter
    private var loggedInUserId: Int = 0
    private lateinit var sqLitehelper: SQLitehelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getString("userId", "0")?.toInt() ?: 0
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
        sqLitehelper = SQLitehelper(requireContext()) // Inicialize o SQLitehelper aqui

        loggedInUserId = sharedPreferences.getString("userId", "0")?.toInt() ?: 0
        eventAdapter = EventAdapter(emptyList(), loggedInUserId, sqLitehelper) // Passe o SQLitehelper para o EventAdapter
        binding.recyclerViewEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }

        loadEvents()

        binding.profileImg.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun loadEvents() {
        lifecycleScope.launch {
            val events = withContext(Dispatchers.IO) {
                val dbHelper = SQLitehelper(requireContext())
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM EVENTS WHERE FLAG_ATIVO = 'S'", null)
                val eventList = mutableListOf<Event>()

                while (cursor.moveToNext()) {
                    val idIndex = cursor.getColumnIndex("ID")
                    val userIdIndex = cursor.getColumnIndex("USER_ID")
                    val tituloIndex = cursor.getColumnIndex("TITULO")
                    val imagemIndex = cursor.getColumnIndex("IMAGEM")
                    val descricaoIndex = cursor.getColumnIndex("DESCRICAO")
                    val flagAtivoIndex = cursor.getColumnIndex("FLAG_ATIVO")
                    val flagPromocaoIndex = cursor.getColumnIndex("FLAG_PROMOCAO")
                    val descPromocaoIndex = cursor.getColumnIndex("DESC_PROMOCAO")
                    val horarioIndex = cursor.getColumnIndex("HORARIO")
                    val dataIndex = cursor.getColumnIndex("DATA")
                    val cepIndex = cursor.getColumnIndex("CEP")
                    val cidadeIndex = cursor.getColumnIndex("CIDADE")
                    val ufIndex = cursor.getColumnIndex("UF")
                    val ruaIndex = cursor.getColumnIndex("RUA")
                    val numeroIndex = cursor.getColumnIndex("NUMERO")
                    val bairroIndex = cursor.getColumnIndex("BAIRRO")

                    val eventId = cursor.getInt(idIndex)
                    val userId = cursor.getInt(userIdIndex)
                    val titulo = cursor.getString(tituloIndex)
                    val imagem = cursor.getString(imagemIndex)
                    val descricao = cursor.getString(descricaoIndex)
                    val flagAtivo = cursor.getString(flagAtivoIndex)
                    val flagPromocao = cursor.getString(flagPromocaoIndex)
                    val descPromocao = cursor.getString(descPromocaoIndex)
                    val horario = cursor.getString(horarioIndex)
                    val data = cursor.getString(dataIndex)
                    val cep = cursor.getString(cepIndex)
                    val cidade = cursor.getString(cidadeIndex)
                    val uf = cursor.getString(ufIndex)
                    val rua = cursor.getString(ruaIndex)
                    val numero = cursor.getInt(numeroIndex)
                    val bairro = cursor.getString(bairroIndex)

                    val event = Event(eventId, userId, titulo, imagem, descricao, flagAtivo, flagPromocao, descPromocao,
                        horario, data, cep, cidade, uf, rua, numero, bairro)
                    eventList.add(event)
                }

                cursor.close()
                eventList
            }
            eventAdapter.updateEvents(events)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.user_menu_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit_profile -> {
                    // Open profile edit fragment
                    true
                }
                R.id.menu_promote_event -> {
                    findNavController().navigate(R.id.action_homeFragment_to_eventFormFragment)
                    true
                }
                R.id.menu_my_events -> {
                    findNavController().navigate(R.id.action_homeFragment_to_myEventsFragment)
                    true
                }
                R.id.menu_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun logout() {
        sharedPreferences.edit().putBoolean("loggedIn", false).apply()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
