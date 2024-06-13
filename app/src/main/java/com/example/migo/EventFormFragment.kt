package com.example.migo

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.migo.databinding.FragmentEventFormBinding
import java.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class EventFormFragment : Fragment() {

    private var _binding: FragmentEventFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var sqLitehelper: SQLitehelper
    private val REQUEST_CODE_SELECT_IMAGE = 100
    private val SELECT_IMAGE_REQUEST_CODE = 1
    private var base64ImageString: String? = null


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sqLitehelper = SQLitehelper(requireContext())

        val imageSelectionLayout = binding.layoutImageSelection.root
        imageSelectionLayout.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    openGallery()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    openGallery()
                }
            }
        }

        binding.closeButton.setOnClickListener {
            findNavController().navigate(R.id.action_eventFormFragment_to_homeFragment)
        }
        timeEditText = binding.time
        dateEditText = binding.date

        timeEditText.setOnClickListener { showTimePickerDialog() }
        dateEditText.setOnClickListener { showDatePickerDialog() }

        binding.btnPublish.setOnClickListener { createEvent() }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minuteOfHour ->
            val time = String.format("%02d:%02d", hourOfDay, minuteOfHour)
            timeEditText.setText(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year1, month1, dayOfMonth ->
            val date = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1)
            dateEditText.setText(date)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                val imageView = binding.layoutImageSelection.imageView
                val placeholderLayout = binding.layoutImageSelection.placeholderLayout
                imageView.setImageURI(selectedImageUri)
                imageView.visibility = View.VISIBLE
                placeholderLayout.visibility = View.GONE

                base64ImageString = convertImageToBase64(selectedImageUri)
            }
        }
    }


    private fun convertImageToBase64(imageUri: Uri): String {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        return if (bytes != null) {
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } else {
            ""
        }
    }

    private fun createEvent() {
        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
        val userIdString = sharedPreferences.getString("userId", null)

        val userId: Int? = userIdString?.toIntOrNull()

        if (userId != null && base64ImageString != null) {
            val titulo = binding.title.text.toString()
            val imagem = base64ImageString
            val descricao = binding.description.text.toString()
            val flagAtivo = "S"
            val flagPromocao = binding.isPromotion.toString()
            val descPromocao = binding.promotionDescription.text.toString()
            val horario = binding.time.text.toString()
            val data = binding.date.text.toString()
            val cep = binding.cep.text.toString()
            val cidade = binding.city.text.toString()
            val uf = binding.uf.text.toString()
            val rua = binding.street.text.toString()
            val numeroString = binding.number.text.toString()
            val bairro = binding.nbh.text.toString()

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

            val event = Event(
                userId = userId,
                titulo = titulo,
                imagem = imagem!!,
                descricao = descricao,
                flagAtivo = flagAtivo,
                flagPromocao = flagPromocao,
                descPromocao = descPromocao,
                horario = horario,
                data = data,
                cep = cep,
                cidade = cidade,
                uf = uf,
                rua = rua,
                numero = numero,
                bairro = bairro
            )

            val status = sqLitehelper.createEvent(event)

            if (status > -1) {
                Toast.makeText(requireContext(), "Adicionado com sucesso", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_eventFormFragment_to_homeFragment)
            } else {
                Log.e("CreateEvent", "Erro ao salvar o evento no banco de dados")
                Toast.makeText(requireContext(), "Não Salvo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("CreateEvent", "userId ou base64ImageString é nulo")
            Toast.makeText(context, "Usuário não está logado ou ID inválido ou imagem não selecionada", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
