package com.kaizm.ecommerce_app.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.databinding.ItemCartBinding
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product
import java.text.DecimalFormat

class CartAdapter(val context: Context, val iClickItemCart: IClickItemCart) :
    RecyclerView.Adapter<CartAdapter.CartHolder>() {

    interface IClickItemCart{
        fun onClickToPlus(cart: Cart, editText: EditText)
        fun onClickToMinus(cart: Cart, editText: EditText)
        fun onClickToItem(cart: Cart)
        fun onEditQuantity(cart: Cart, s: String)
    }

    private var list: MutableList<Cart> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        return CartHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val cart = list[position]
        holder.bind(cart)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun currencyFormat(price: String): String? {
        val decimalFormat = DecimalFormat("###,###,##0" + " đ")
        return decimalFormat.format(price.toDouble())
    }

    fun updateList(list: List<Cart>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class CartHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            val productImg: ImageView = binding.productImg
            Glide.with(context).load(cart.image).into(productImg)
            binding.productTitle.text = cart.name
            binding.productPrice.text = currencyFormat(cart.price.toString())
            binding.productQuantity.setText(cart.quantity.toString())

            binding.productImg.setOnClickListener{
                iClickItemCart.onClickToItem(cart)
            }

            binding.productMinus.setOnClickListener(View.OnClickListener {
                iClickItemCart.onClickToMinus(cart, binding.productQuantity)
            })

            binding.productPlus.setOnClickListener(View.OnClickListener {
                iClickItemCart.onClickToPlus(cart, binding.productQuantity)
            })

            binding.productQuantity.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(s!=null && s.toString()!=""){
                        iClickItemCart.onEditQuantity(cart,s.toString())
                    }
                }
            })

        }
    }
}