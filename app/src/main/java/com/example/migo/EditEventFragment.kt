package com.example.migo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.migo.databinding.FragmentEditEventBinding

class EditEventFragment : Fragment() {
    private lateinit var binding: FragmentEditEventBinding
    private lateinit var sqLitehelper: SQLitehelper
    private var eventId: Int = 0
    private lateinit var event: Event

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sqLitehelper = SQLitehelper(requireContext())

        arguments?.let {
            eventId = it.getInt("eventId")
        }

        event = sqLitehelper.getEventById(eventId) ?: return

        // Preencher os campos com os dados do evento
        binding.title.setText(event.titulo)
        binding.description.setText(event.descricao)
        binding.cep.setText(event.cep)
        binding.city.setText(event.cidade)
        binding.uf.setText(event.uf)
        binding.street.setText(event.rua)
        binding.number.setText(event.numero.toString())
        binding.nbh.setText(event.bairro)
        binding.time.setText(event.horario)
        binding.date.setText(event.data)

        // Carregar a imagem (opcional)
        if (event.imagem.isNotEmpty()) {
            val imageBytes = Base64.decode(event.imagem, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.layoutImageSelection.imageView.setImageBitmap(bitmap)
            binding.layoutImageSelection.imageView.visibility = View.VISIBLE
            binding.layoutImageSelection.placeholderLayout.visibility = View.GONE
        }

        binding.btnPublish.setOnClickListener {
            updateEvent()
        }
    }

    private fun updateEvent() {
        val titulo = binding.title.text.toString()
        val descricao = binding.description.text.toString()
        val cep = binding.cep.text.toString()
        val cidade = binding.city.text.toString()
        val uf = binding.uf.text.toString()
        val rua = binding.street.text.toString()
        val numeroString = binding.number.text.toString()
        val bairro = binding.nbh.text.toString()
        val horario = binding.time.text.toString()
        val data = binding.date.text.toString()

        var numero: Int? = null
        try {
            numero = numeroString.toInt()
            if (numero < 0) {
                throw NumberFormatException("O número não pode ser negativo")
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Por favor, insira um número válido", Toast.LENGTH_SHORT).show()
            return
        }

        event.titulo = titulo
        event.descricao = descricao
        event.cep = cep
        event.cidade = cidade
        event.uf = uf
        event.rua = rua
        event.numero = numero
        event.bairro = bairro
        event.horario = horario
        event.data = data

        val rowsAffected = sqLitehelper.updateEvent(event)

        if (rowsAffected > 0) {
            Toast.makeText(requireContext(), "Evento atualizado com sucesso", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_editEventFragment_to_homeFragment)
        } else {
            Log.e("UpdateEvent", "Erro ao atualizar o evento no banco de dados")
            Toast.makeText(requireContext(), "Não Atualizado", Toast.LENGTH_SHORT).show()
        }
    }
}
