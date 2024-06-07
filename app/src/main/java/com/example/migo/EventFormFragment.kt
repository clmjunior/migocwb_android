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

class EventFormFragment : Fragment() {

    private var _binding: FragmentEventFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText

    private val REQUEST_CODE_SELECT_IMAGE = 100

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
    ): View? {
        _binding = FragmentEventFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.btnPublish.setOnClickListener{ createEvent() }
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
            }
        }
    }

    private fun createEvent() {
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
