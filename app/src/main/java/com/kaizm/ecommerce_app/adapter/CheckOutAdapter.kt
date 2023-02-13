package com.kaizm.ecommerce_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.ecommerce_app.databinding.FragmentCheckOutBinding
import com.kaizm.ecommerce_app.databinding.ItemCheckoutBinding
import com.kaizm.ecommerce_app.model.Cart
import java.text.DecimalFormat

class CheckOutAdapter(val context: Context, val iClickItemCheckout: IClickItemCheckout) :
    RecyclerView.Adapter<CheckOutAdapter.CheckOutHolder>() {

    private var list: MutableList<Cart> = mutableListOf()

    interface IClickItemCheckout{
        fun onClickToItem(cart: Cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckOutHolder {
        return CheckOutHolder(ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context),  parent,false))
    }

    override fun onBindViewHolder(holder: CheckOutHolder, position: Int) {
        var cart = list[position]
        holder.bind(cart)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Cart>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }


    inner class CheckOutHolder(private val binding: ItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            val productImg: ImageView = binding.checkoutImg
            Glide.with(context).load(cart.image).into(productImg)
            binding.checkoutTitle.text = cart.name
            binding.checkoutPrice.text = currencyFormat(cart.price.toString())
            binding.checkoutQuantity.text = "Số lượng: ${cart.quantity}"

            binding.checkoutImg.setOnClickListener {
                iClickItemCheckout.onClickToItem(cart)
            }

        }
    }

    private fun currencyFormat(price: String): String? {
        val decimalFormat = DecimalFormat("###,###,##0" + " đ")
        return decimalFormat.format(price.toDouble())
    }
}