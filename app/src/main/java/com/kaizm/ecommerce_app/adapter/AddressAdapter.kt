package com.kaizm.ecommerce_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.ecommerce_app.databinding.ItemAddressBinding
import com.kaizm.ecommerce_app.databinding.ItemCartBinding
import com.kaizm.ecommerce_app.model.Address

class AddressAdapter(val context: Context, val iClickItemAddress: IClickItemAddress) :
    RecyclerView.Adapter<AddressAdapter.AddressHolder>() {

    interface IClickItemAddress{
        fun onClickChooseAddress(address: Address)
        fun onClickEditAddress(address: Address)
        fun onClickDeleteAddress(address: Address)
    }

    private val list: MutableList<Address> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        return AddressHolder(
            ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = list[position]
        holder.bind(address)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Address>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class AddressHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address) {
            binding.addressName.text = address.name.toString()
            binding.addressAddress.text = address.address.toString()
            binding.addressPhone.text = address.phoneNumber.toString()
            binding.addressEdit.setOnClickListener {
                iClickItemAddress.onClickEditAddress(address)
            }
            binding.addressDelete.setOnClickListener {
                iClickItemAddress.onClickDeleteAddress(address)
            }
            binding.addressContainer.setOnClickListener{
                iClickItemAddress.onClickChooseAddress(address)
            }
        }
    }

}