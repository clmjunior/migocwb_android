package com.example.migo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.migo.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sqLitehelper: SQLitehelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Verifique se o usuário já está logado
        val loggedIn = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
            .getBoolean("loggedIn", false)

        if (loggedIn) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        // Inicialize o binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqLitehelper = SQLitehelper(requireContext())

        binding.goToSignupButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.btnLogin.setOnClickListener {
            findUser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findUser() {
        val sEmail = binding.email.text.toString()
        val sPassword = binding.pass.text.toString()

        if (sEmail.isEmpty() || sPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show()
            return
        }

        val userId = sqLitehelper.findUser(sEmail, sPassword)

        if (userId != null) {
            Toast.makeText(requireContext(), "Usuario encontrado", Toast.LENGTH_LONG).show()
            val sharedPref = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)

            with(sharedPref.edit()) {
                putBoolean("loggedIn", true)
                putString("userId", userId)
                apply()
            }

            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        } else {
            Toast.makeText(requireContext(), "E-mail ou senha incorretos", Toast.LENGTH_LONG).show()
        }
    }
}
