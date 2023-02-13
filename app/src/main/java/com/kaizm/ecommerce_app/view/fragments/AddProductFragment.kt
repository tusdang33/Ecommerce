package com.kaizm.ecommerce_app.view.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.databinding.FragmentAddProductBinding
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel

class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding

    private val viewModel: ProductViewModel by activityViewModels()

    var items = arrayOf("Laptop", "Ram", "Hdd", "Ssd", "Monitor", "Mouse", "Keyboard", "Headset")
    var imgUri: Uri? = null

    private var category: String = ""

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {

                if (result!!.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    imgUri = intent!!.data!!
                    Glide.with(requireContext()).load(imgUri).into(binding.addImg)
                }
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addImg.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            activityResultLauncher.launch(galleryIntent)
        }

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, items)
        binding.addCategory.setAdapter(arrayAdapter)

        binding.addCategory.setOnItemClickListener { parent, view, position, id ->
            category = parent.getItemAtPosition(position).toString()
        }

        binding.addAdd.setOnClickListener(View.OnClickListener {
            if (imgUri == null) {
                Toast.makeText(
                    requireContext(),
                    "Không được để trống ảnh",
                    Toast.LENGTH_LONG
                ).show()
            } else if (TextUtils.isEmpty(category)) {
                binding.addCategory.error = "Không được để trống"
                binding.addCategory.requestFocus()
            } else if (TextUtils.isEmpty(binding.addName.text.toString())) {
                binding.addName.error = "Không được để trống"
                binding.addName.requestFocus()
            } else if (TextUtils.isEmpty(binding.addPrice.text.toString())) {
                binding.addPrice.error = "Không được để trống"
                binding.addPrice.requestFocus()
            } else if (TextUtils.isEmpty(binding.addQuantity.text.toString())) {
                binding.addQuantity.error = "Không được để trống"
                binding.addQuantity.requestFocus()
            } else if (TextUtils.isEmpty(binding.addDescription.getText().toString())) {
                binding.addDescription.error = "Không được để trống"
                binding.addDescription.requestFocus()
            } else {
                binding.addAdd.text = "Adding..."
                binding.addAdd.isClickable = false
                uploadToFirebase()
            }
        })

        binding.addBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun uploadToFirebase() {
        val name: String = binding.addName.text.toString().trim()
        val price: Int = binding.addPrice.text.toString().toInt()
        val category: String = binding.addCategory.text.toString().trim()
        val quantity: Int = binding.addQuantity.text.toString().toInt()
        val description: String = binding.addDescription.text.toString().trim()

        viewModel.uploadProduct(
            requireContext(),
            imgUri!!,
            name,
            price,
            category,
            quantity,
            description
        )
        viewModel.uploadProgressLiveData.observe(viewLifecycleOwner) {
            binding.addProgcess.progress = it.toInt()
        }
        viewModel.uploadStatusLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ProductViewModel.Status.Done -> {
                    binding.addAdd.text = "Add"
                    binding.addAdd.setTextColor(Color.parseColor("#000000"))
                    binding.addAdd.isClickable = true
                }
                is ProductViewModel.Status.NotDone ->{
                    binding.addAdd.text = "Adding..."
                    binding.addAdd.setTextColor(Color.parseColor("#949494"))
                    binding.addAdd.isClickable = false
                }
                else -> {}
            }
        }
    }
}