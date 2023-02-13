package com.kaizm.ecommerce_app.viewmodel

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Role
import com.kaizm.ecommerce_app.model.User

class AuthViewModel : ViewModel() {

    sealed class Event {
        object LoginSuccess : Event()
        object LoginFailed : Event()
        object IsLogged : Event()
        object IsNotLogged : Event()
        object CreateSuccess : Event()
        object CreateFail : Event()
    }


    private val _errorCodeLiveData = MutableLiveData<String>()
    val errorCodeLiveData: LiveData<String>
        get() = _errorCodeLiveData

    private val _eventLiveData = MutableLiveData<Event>()
    val eventLiveData: LiveData<Event>
        get() = _eventLiveData

    fun signIn(email: String, pass: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("account")
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    databaseReference.child(firebaseUser!!.uid).child("role")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val role: Int = snapshot.value.toString().toInt()
                                Role.getInstance().setRole(role)
                                _eventLiveData.postValue(Event.LoginSuccess)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                } else {
                    _eventLiveData.postValue(Event.LoginFailed)
                }
            }
    }

    fun checkUser() {
        val mUser = FirebaseAuth.getInstance().currentUser
        if (mUser != null) {
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("account")
            databaseReference.child(mUser.uid).child("role")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val role: Int = snapshot.value.toString().toInt()
                        Role.getInstance().setRole(role)
                        Log.i("AAA", "$role")
                        _eventLiveData.postValue(Event.IsLogged)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } else {
            _eventLiveData.postValue(Event.IsNotLogged)
        }
    }

    fun createAccount(email: String, pass: String) {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("account")
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val mUser: FirebaseUser = firebaseAuth.currentUser!!
                    val mAccount: DatabaseReference = databaseReference.child(mUser.uid)
                    val user = User(mUser.uid, email, 0)
                    Role.getInstance().setRole(0)
                    mAccount.setValue(user, object : DatabaseReference.CompletionListener {
                        override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                            _eventLiveData.postValue(Event.CreateSuccess)
                        }
                    })
                } else {
                    _eventLiveData.postValue(Event.CreateFail)
                }
            }
    }

    fun getUserEmail(): String {
        return FirebaseAuth.getInstance().currentUser!!.email!!
    }

    fun getUserName(): String {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (!TextUtils.isEmpty(firebaseUser!!.displayName)) {
            firebaseUser.displayName!!
        } else {
            Role.getInstance().getName()
        }
    }

    fun updateUserName(context: Context, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(null)
            .build()

        FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Update thành công", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, task.exception.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    fun updateUserPassword(context: Context, pass: String) {
        FirebaseAuth.getInstance().currentUser!!.updatePassword(pass)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Update thành công", Toast.LENGTH_LONG).show()
                }
            })
    }
}