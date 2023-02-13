package com.kaizm.ecommerce_app.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kaizm.ecommerce_app.databinding.ItemProductBinding
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel
import java.text.DecimalFormat

class ProductAdapter(var context: Context) :
    RecyclerView.Adapter<ProductAdapter.ProductHolder>() {
    lateinit var iClickItemProduct: IClickItemProduct
    private var list: MutableList<Product> = mutableListOf()

    interface IClickItemProduct {
        fun onClickToItem(product: Product)
        fun onClickAddItem(product: Product)
    }

    constructor(
        context: Context,
        iClickItemProduct: IClickItemProduct
    ) : this(context) {
        this.context = context
        this.iClickItemProduct = iClickItemProduct
    }

    constructor(
        context: Context,
        list: MutableList<Product>,
        iClickItemProduct: IClickItemProduct
    ) : this(context) {
        this.context = context
        this.list = list
        this.iClickItemProduct = iClickItemProduct
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        return ProductHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = list[position]
        holder.bind(product)
    }

    fun updateList(list: List<Product>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ProductHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun currencyFormat(price: String): String? {
            val decimalFormat = DecimalFormat("###,###,##0" + " đ")
            return decimalFormat.format(price.toDouble())
        }

        fun bind(product: Product) {
            Glide.with(context).load(product.image).into(binding.productImg)
            binding.productTitle.text = product.name
            binding.productPrice.text = currencyFormat(product.price.toString())

            binding.productImg.setOnClickListener {
                iClickItemProduct.onClickToItem(product)
            }

            binding.btnAdd.setOnClickListener {
                iClickItemProduct.onClickAddItem(product)
            }
        }
    }
}