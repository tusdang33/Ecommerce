package com.kaizm.ecommerce_app.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.kaizm.ecommerce_app.model.User
import com.kaizm.ecommerce_app.repository.IGetDataListener
import com.kaizm.ecommerce_app.repository.UserRepository

class UserViewModel: ViewModel() {

    private val userRepository = UserRepository()

    private val _userLiveData = MutableLiveData<List<User>>()
    val userLiveData: LiveData<List<User>>
        get() = _userLiveData

    fun setData(query: Query){
        userRepository.readData(query, object  : IGetDataListener{
            override fun onGetSuccess(any: Any) {
                @Suppress("UNCHECKED_CAST")
                _userLiveData.postValue(any as List<User>)
            }
        })
    }

    fun setRole(context: Context, user: User, role :Int){
        val databaseReference = FirebaseDatabase.getInstance().getReference("account")
        if (role == 1 ) {
            user.role = 1
        } else {
            user.role = 0
        }
        databaseReference.child(user.id).updateChildren(
            user.updateRole()
        ) { error, ref ->
            Toast.makeText(context, "Phân quyền thành công", Toast.LENGTH_SHORT).show()
        }
    }
}