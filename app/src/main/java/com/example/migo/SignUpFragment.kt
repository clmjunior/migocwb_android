package com.example.migo

import ViaCepService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.migo.databinding.FragmentSignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var sqLitehelper: SQLitehelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqLitehelper = SQLitehelper(requireContext())

        binding.goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.btnCreateUser.setOnClickListener {
            createUser()
        }

        binding.cep.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                fetchAddressFromCep(binding.cep.text.toString())
            }
        }
    }

    fun fetchAddressFromCep(cep: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ViaCepService::class.java)
        val call = service.getAddress(cep)

        call.enqueue(object : Callback<Address> {
            override fun onResponse(call: Call<Address>, response: Response<Address>) {
                if (response.isSuccessful) {
                    val address = response.body()
                    if (address != null && !address.erro) {
                        // Atualizar os campos do formulário com os dados do endereço
                        binding.uf.setText(address.uf)
                        binding.city.setText(address.localidade)
                        binding.nbh.setText(address.bairro)
                        binding.street.setText(address.logradouro)
                        // Também pode ser necessário atualizar outros campos, conforme necessário
                    } else {
                        Log.e("Address", "CEP não encontrado ou inválido")
                    }
                } else {
                    Log.e("Address", "Erro na requisição: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Address>, t: Throwable) {
                Log.e("Address", "Erro na requisição: ${t.message}")
            }
        })
    }



    private fun createUser() {
        val name = binding.name.text.toString()
        val lastName = binding.lastName.text.toString()
        val email = binding.email.text.toString()
        val password = binding.pass.text.toString()
        val confirmPassword = binding.pass2.text.toString()
        val cep = binding.cep.text.toString()
        val number = binding.number.text.toString()
        val uf = binding.uf.text.toString()
        val city = binding.city.text.toString()
        val neighborhood = binding.nbh.text.toString()
        val street = binding.street.text.toString()

        if (password == confirmPassword) {
            val user = User(
                name = name,
                last_name = lastName,
                email = email,
                password = password,
                cep = cep,
                number = number,
                uf = uf,
                city = city,
                neighborhood = neighborhood,
                street = street
            )
            val status = sqLitehelper.createUser(user)

            if (status > -1) {
                Toast.makeText(requireContext(), "Adicionado com sucesso", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "Não Salvo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "As senhas não condizem", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
