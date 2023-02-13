package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.databinding.FragmentDetailBinding
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.viewmodel.CartViewModel
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel
import java.text.DecimalFormat
import kotlin.let


class DetailFragment : Fragment() {
    lateinit var binding: FragmentDetailBinding

    private val productViewModel: ProductViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var key: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity?)!!.hideBottomNavigation()
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getParcelable<Product>("product")
        product?.let {
            key = product.id!!
            setView(it)
        }

        val id = arguments?.getString("cart")
        id?.let {
            key = it
            setData(it)
        }

        binding.detailBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.detailMinus.setOnClickListener {
            var currentQuantity: Int = binding.detailQuantity.text.toString().toInt()
            if (currentQuantity > 1) {
                currentQuantity--
                binding.detailQuantity.setText(currentQuantity.toString())
            }
        }

        binding.detailPlus.setOnClickListener {
            var currentQuantity = binding.detailQuantity.text.toString().toInt()
            currentQuantity++
            binding.detailQuantity.setText(currentQuantity.toString())
        }

        binding.detailAdd.setOnClickListener {
            productViewModel.getProductByID(key)
            productViewModel.clearCurrentProduct()
            productViewModel.productLiveData.observe(viewLifecycleOwner) {
                it?.let { it1 ->
                    productViewModel.addToCart(
                        requireContext(),
                        it1,
                        binding.detailQuantity.text.toString().toInt()
                    )
                }
            }
        }

        binding.detailBuy.setOnClickListener {
            val bundle = Bundle().apply { putString("key", key)
            putInt("quantity", binding.detailQuantity.text.toString().toInt())}
            findNavController().navigate(R.id.action_detailFragment_to_checkOutFragment, bundle)
        }
    }

    private fun setData(key: String) {
        productViewModel.getProductByID(key)
        productViewModel.productLiveData.observe(viewLifecycleOwner) {
            it?.let { it1 -> setView(it1) }
        }
    }

    private fun setView(product: Product) {
        Glide.with(requireContext()).load(product!!.image).into(binding.detailImg)
        binding.detailTitle.text = product.name
        binding.detailPrice.text = currencyFormat(product.price.toString())
        binding.detailDescription.text = product.description
        binding.detailQuantityLeft.text = "Còn lại: + ${product!!.quantity}  sản phẩm"
    }

    private fun currencyFormat(price: String): String? {
        val decimalFormat = DecimalFormat("###,###,##0" + " đ")
        return decimalFormat.format(price.toDouble())
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)!!.showBottomNavigation()
    }
}