package com.kaizm.ecommerce_app.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.kaizm.ecommerce_app.model.User

class UserRepository {

    private var list : MutableList<User> = mutableListOf()


    fun readData(query: Query, iGetDataListener: IGetDataListener){
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val exId = FirebaseAuth.getInstance().currentUser!!.uid
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    if (java.lang.String.valueOf(user.id) != exId) {
                        list.add(user)
                        iGetDataListener.onGetSuccess(list)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if ( list.isEmpty()) return
                for (i in list.indices) {
                    if (user!!.id == list[i].id) {
                        list[i] = user
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}