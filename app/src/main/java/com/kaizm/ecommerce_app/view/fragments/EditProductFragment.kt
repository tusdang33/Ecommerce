package com.kaizm.ecommerce_app.view.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kaizm.ecommerce_app.databinding.FragmentEditProductBinding
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel

class EditProductFragment : Fragment() {
    lateinit var binding: FragmentEditProductBinding

    private val viewModel: ProductViewModel by activityViewModels()

    var items = arrayOf("Laptop", "Ram", "Hdd", "Ssd", "Monitor", "Mouse", "Keyboard", "Headset")
    var imgUri: Uri? = null
    var currentImgUri: Uri? = null
    private var category: String = ""

    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                if (result!!.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    imgUri = intent!!.data!!
                    Glide.with(requireContext()).load(imgUri).into(binding.editImg)
                }
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getParcelable<Product>("product")
        product?.let { fillData(it) }

        binding.editCategory.setOnItemClickListener { parent, view, position, id ->
            category = parent.getItemAtPosition(position).toString()
        }

        binding.editImg.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            activityResultLauncher.launch(galleryIntent)
        }

        binding.editEdit.setOnClickListener {
            product?.let { it1 -> uploadToFirebase(it1) }
        }

        binding.editBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun fillData(product: Product) {
        imgUri = Uri.parse(product.image)
        currentImgUri = imgUri
        Glide.with(requireContext()).load(product.image).into(binding.editImg)
        binding.editName.setText(product.name)
        binding.editPrice.setText(java.lang.String.valueOf(product.price))

        for (i in items.indices) {
            if (items[i] == product.category) {
                binding.editCategory.setText(items[i], false)
                category = items[i]
            }
        }
        binding.editQuantity.setText(java.lang.String.valueOf(product.quantity))
        binding.editDescription.setText(product.description)
    }

    private fun uploadToFirebase(product: Product) {
        val name: String = binding.editName.text.toString().trim()
        val price: Int = binding.editPrice.text.toString().toInt()
        val category: String = binding.editCategory.text.toString().trim()
        val quantity: Int = binding.editQuantity.text.toString().toInt()
        val description: String = binding.editDescription.text.toString().trim()

        if(currentImgUri != imgUri){
            viewModel.updateProduct(requireContext(), product.id!!,imgUri,name,price,category,quantity,description)
            viewModel.uploadProgressLiveData.observe(viewLifecycleOwner) {
                binding.editProgress.progress = it.toInt()
            }
        }else{
            viewModel.updateProduct(requireContext(), product.id!!,null ,name,price,category,quantity,description)
            viewModel.uploadProgressLiveData.observe(viewLifecycleOwner) {
                binding.editProgress.progress = it.toInt()
            }
        }

        viewModel.uploadStatusLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ProductViewModel.Status.Done -> {
                    binding.editEdit.text = "Update"
                    binding.editEdit.setTextColor(Color.parseColor("#000000"))
                    binding.editEdit.isClickable = true
                }
                is ProductViewModel.Status.NotDone -> {
                    binding.editEdit.text = "Updating..."
                    binding.editEdit.setTextColor(Color.parseColor("#949494"))
                    binding.editEdit.isClickable = false
                }
                else -> {}
            }
        }
    }
}