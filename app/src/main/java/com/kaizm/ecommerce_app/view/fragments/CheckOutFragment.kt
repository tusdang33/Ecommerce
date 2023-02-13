package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.adapter.CartAdapter
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.adapter.CheckOutAdapter
import com.kaizm.ecommerce_app.databinding.FragmentCheckOutBinding
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.view.AddressBottomSheet
import com.kaizm.ecommerce_app.viewmodel.AddressViewModel
import com.kaizm.ecommerce_app.viewmodel.CartViewModel
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel
import java.text.DecimalFormat

class CheckOutFragment : Fragment() {
    private lateinit var binding : FragmentCheckOutBinding
    lateinit var checkOutAdapter: CheckOutAdapter

    private val cartViewModel : CartViewModel by activityViewModels()
    private val addressViewModel : AddressViewModel by activityViewModels()
    private val productViewModel : ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckOutBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillData()



        binding.checkoutBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.checkoutBtnAddress.setOnClickListener {
            openDialogAddress()
        }

        checkOutAdapter = CheckOutAdapter(requireContext(), object : CheckOutAdapter.IClickItemCheckout{
            override fun onClickToItem(cart: Cart) {
                cartViewModel.productLiveData.observe(viewLifecycleOwner){
                    val bundle = Bundle().apply { putParcelable("cart", it) }
                    findNavController().navigate(R.id.detailFragment, bundle)
                }
            }
        })

        binding.checkoutList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.checkoutList.addItemDecoration(dividerItemDecoration)
        binding.checkoutList.adapter = checkOutAdapter
    }

    private fun openDialogAddress() {
        val addressBottomSheet = AddressBottomSheet(object : AddressBottomSheet.ISendAddress{
            override fun sendAddress(address: Address) {
                binding.checkoutAddress.text = address.address
                binding.checkoutName.text = address.name
                binding.checkoutPhone.text = address.phoneNumber
            }
        })
        addressBottomSheet.show(parentFragmentManager, null)
    }

    private fun fillData() {
        val key = arguments?.getString("key")
        val quantity = arguments?.getInt("quantity")
        if(key== null){
            cartViewModel.setData()
            cartViewModel.cartLiveData.observe(viewLifecycleOwner){
                var totalPrice = 0
                for (i in it) {
                    totalPrice += i.price * i.quantity
                }
                binding.checkoutPrice.text = currencyFormat(totalPrice.toString())
                checkOutAdapter.updateList(it)
            }
        }else{
            productViewModel.getProductByID(key)
            productViewModel.productLiveData.observe(viewLifecycleOwner){
                val cart = Cart(it!!.id, it.image,it.name,it.price,quantity!!)
                val list : MutableList<Cart> = mutableListOf()
                list.add(cart)
                val totalPrice = it.price * quantity
                binding.checkoutPrice.text = currencyFormat(totalPrice.toString())
                checkOutAdapter.updateList(list)
            }
        }

        addressViewModel.setFirstAddress(FirebaseDatabase.getInstance().getReference("address").limitToFirst(1))
        addressViewModel.addressLiveData.observe(viewLifecycleOwner){
            binding.checkoutAddress.text = it[0].address
            binding.checkoutName.text = it[0].name
            binding.checkoutPhone.text = it[0].phoneNumber
        }
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