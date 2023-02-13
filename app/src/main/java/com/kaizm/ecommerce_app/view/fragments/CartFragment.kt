package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.style.ChasingDots
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.adapter.CartAdapter
import com.kaizm.ecommerce_app.databinding.FragmentCartBinding
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.viewmodel.CartViewModel
import java.text.DecimalFormat
import java.util.*

class CartFragment : Fragment() {

    private val search: String = ""
    lateinit var binding: FragmentCartBinding
    lateinit var noneImg: ImageView
    lateinit var noneTxt: TextView
    lateinit var noneBtn: Button
    lateinit var cartDone: Button
    lateinit var cartProgress: ProgressBar
    lateinit var cartBack: ImageButton
    lateinit var cartPrice: TextView
    lateinit var cartAdapter: CartAdapter
    lateinit var rcProduct: RecyclerView

    private val viewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item()
        fillData()
        val chasingDots = ChasingDots()
        cartProgress.indeterminateDrawable = chasingDots

        val handler = Handler()
        handler.postDelayed({
            cartDone.visibility = View.VISIBLE
            cartProgress.visibility = View.GONE
            if (cartAdapter.itemCount == 0) {
                binding.cartPrice.text = "0đ"
                noneImg.visibility = View.VISIBLE
                noneTxt.visibility = View.VISIBLE
                noneBtn.visibility = View.VISIBLE
                noneBtn.setOnClickListener {
                    findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
                }
            } else {
                rcProduct.layoutManager = LinearLayoutManager(requireContext())
                rcProduct.adapter = cartAdapter
            }
        }, 1000)

        binding.cartDone.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_checkOutFragment)
        }
    }

    private fun fillData() {
        viewModel.setData()
        viewModel.cartLiveData.observe(viewLifecycleOwner) {
            var totalPrice = 0
            for (i in it) {
                totalPrice += i.price * i.quantity
            }
            binding.cartPrice.text = currencyFormat(totalPrice.toString())
            cartAdapter.updateList(it)
        }
    }

    private fun currencyFormat(price: String): String? {
        val decimalFormat = DecimalFormat("###,###,##0" + " đ")
        return decimalFormat.format(price.toDouble())
    }

    private fun item() {
        noneImg = binding.cartNoneImg
        noneTxt = binding.cartNoneTxt
        noneBtn = binding.cartNoneBtn
        cartDone = binding.cartDone
        cartProgress = binding.cartProgress
        cartPrice = binding.cartPrice
        rcProduct = binding.cartList
        cartAdapter = CartAdapter(requireContext(), object : CartAdapter.IClickItemCart {
            override fun onClickToPlus(cart: Cart, editText: EditText) {
                viewModel.plusCart(requireContext(), cart, editText)
            }

            override fun onClickToMinus(cart: Cart, editText: EditText) {
                viewModel.minusCart(requireContext(), cart, editText)
            }

            override fun onClickToItem(cart: Cart) {
                val bundle = Bundle().apply { putString("cart", cart.id) }
                findNavController().navigate(R.id.action_cartFragment_to_detailFragment, bundle)
            }

            override fun onEditQuantity(cart: Cart, s: String) {
                viewModel.updateCartQuantity(cart, s.toInt())
            }
        })
    }
}