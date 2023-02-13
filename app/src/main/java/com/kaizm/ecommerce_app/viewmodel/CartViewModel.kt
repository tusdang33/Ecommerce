package com.kaizm.ecommerce_app.viewmodel

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.repository.CartRepository
import com.kaizm.ecommerce_app.repository.IGetDataListener
import com.kaizm.ecommerce_app.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred

class CartViewModel : ViewModel() {

    private val cartRepository = CartRepository()
    private val productRepository = ProductRepository()
    private var checkQuantity: Long = 0

    private val _cartLiveData = MutableLiveData<List<Cart>>()
    val cartLiveData: LiveData<List<Cart>>
        get() = _cartLiveData

    private val _productLiveData = MutableLiveData<Product>()
    val productLiveData: LiveData<Product>
        get() = _productLiveData

    fun setData() {
        cartRepository.readData(
            FirebaseDatabase.getInstance().getReference("cart")
                .child(FirebaseAuth.getInstance().currentUser!!.uid), object : IGetDataListener {
                override fun onGetSuccess(any: Any) {
                    @Suppress("UNCHECKED_CAST")
                    _cartLiveData.postValue(any as List<Cart>?)
                }
            }
        )
    }

    fun plusCart(context: Context, cart: Cart, editText: EditText) {
        var currentQuantity = cart.quantity
        getProductQuantity(cart)
        if (currentQuantity >= checkQuantity) {
            Toast.makeText(context, "Quá số lượng trong kho", Toast.LENGTH_SHORT).show()
        } else {
            currentQuantity++
            updateCartQuantity(cart, currentQuantity)
            editText.setText(currentQuantity.toString())
        }
    }
    fun minusCart(context: Context, cart: Cart, editText: EditText) {
        var currentQuantity = cart.quantity
        if (currentQuantity > 0) {
            currentQuantity--
            updateCartQuantity(cart, currentQuantity)
            editText.setText(currentQuantity.toString())
        }
    }

    fun updateCartQuantity(cart: Cart, currentQuantity: Int) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("cart").child(uid).child(cart.id!!)
        if (currentQuantity == 0) {
            databaseReference.removeValue()
        } else {
            val result: MutableMap<String, Any> = HashMap()
            result["quantity"] = currentQuantity
            databaseReference.updateChildren(result)
        }
    }

    private fun getProductQuantity(cart: Cart) {
        viewModelScope.launch {
            cartRepository.getQuantity(cart, object : IGetDataListener {
                override fun onGetSuccess(any: Any) {
                    checkQuantity = any as Long
                }
            })
        }
    }
}