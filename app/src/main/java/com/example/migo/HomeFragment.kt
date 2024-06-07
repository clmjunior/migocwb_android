package com.example.migo

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.migo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicialize as SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!sharedPreferences.getBoolean("loggedIn", false)) {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            return
        }

        binding.profileImg.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.user_menu_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit_profile -> {
                    // Abrir fragment de edição de perfil
                    true
                }
                R.id.menu_promote_event -> {
                    findNavController().navigate(R.id.action_homeFragment_to_eventFormFragment)
                    true
                }
                R.id.menu_my_events -> {
                    // Abrir fragment de meus eventos
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
