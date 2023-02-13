package com.kaizm.ecommerce_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaizm.ecommerce_app.databinding.ItemManagerProductBinding
import com.kaizm.ecommerce_app.model.Product
import java.text.DecimalFormat

class ManagerProductAdapter(val context: Context, val iClickItemManagerProduct: IClickItemManagerProduct) :
    RecyclerView.Adapter<ManagerProductAdapter.ManagerProductHolder>() {

    interface IClickItemManagerProduct {
        fun onClickToItem(product: Product)
        fun onClickToEdit(product: Product)
        fun onClickToDelete(product: Product)
    }

    private val list: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerProductHolder {
        return ManagerProductHolder(
            ItemManagerProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ManagerProductHolder, position: Int) {
        val product = list[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Product>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class ManagerProductHolder(private val binding: ItemManagerProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            Glide.with(context).load(product.image).into(binding.productImg)
            binding.productTitle.text = product.name
            binding.productPrice.text = currencyFormat(product.price.toString())
            binding.productEdit.setOnClickListener { 
                iClickItemManagerProduct.onClickToEdit(product)
            }
            binding.productDel.setOnClickListener {
                iClickItemManagerProduct.onClickToDelete(product)
            }
            binding.productImg.setOnClickListener{
                iClickItemManagerProduct.onClickToItem(product)
            }
        }
    }

    private fun currencyFormat(price: String): String? {
        val decimalFormat = DecimalFormat("###,###,##0" + " đ")
        return decimalFormat.format(price.toDouble())
    }

}