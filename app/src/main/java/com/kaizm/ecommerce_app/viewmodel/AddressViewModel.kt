package com.kaizm.ecommerce_app.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.repository.AddressRepository
import com.kaizm.ecommerce_app.repository.IGetDataListener

class AddressViewModel:ViewModel() {
    private val addressRepository = AddressRepository()

    private val _addressLiveData = MutableLiveData<List<Address>>()
    val addressLiveData: LiveData<List<Address>>
        get() = _addressLiveData

    fun getData(query: Query){
        addressRepository.readData(query, object : IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _addressLiveData.postValue(any as List<Address>?)
            }
        })
    }

    fun updateAddress(context: Context, address:Address){
        FirebaseDatabase.getInstance().getReference("address").child(address.id!!).updateChildren(
            address.updateMap()
        ) { _, _ ->
            Toast.makeText(
                context,
                "Sửa thành công",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun addAddress(context: Context,name:String, address:String, phone:String ){
        val databaseReference = FirebaseDatabase.getInstance().getReference("address")
        val addressID: String = databaseReference.push().key!!
        val tempAddress = Address(addressID, name, address, phone)
        databaseReference.child(addressID).setValue(tempAddress,
            DatabaseReference.CompletionListener { _, _ ->
                Toast.makeText(
                    context,
                    "Thêm địa chỉ thành công",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    fun deleteAddress(context: Context,address: Address){
        val reference =
            FirebaseDatabase.getInstance().getReference("address").child(address.id!!)
        reference.removeValue { _, _ ->
            Toast.makeText(
                context,
                "Xoá địa chỉ thành công",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun setFirstAddress(query:Query) {
        addressRepository.readData(query, object : IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _addressLiveData.postValue(any as List<Address>?)
            }
        })
    }
}