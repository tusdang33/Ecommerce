package com.kaizm.ecommerce_app.repository

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.model.Cart

class AddressRepository {
    

    
    fun readData(query: Query, iGetDataListener: IGetDataListener){
        val list:MutableList<Address> = mutableListOf()
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val address = snapshot.getValue(Address::class.java)
                list.add(address!!)
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val address = snapshot.getValue(Address::class.java)
                if (list.isEmpty()) return

                for (i in list.indices) {
                    if (address!!.id == list[i].id) {
                        list[i] = address
                        break
                    }
                }
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val address = snapshot.getValue(Address::class.java)
                if (list.isEmpty()) return

                for (i in list.indices) {
                    if (address!!.id == list[i].id) {
                        list.remove(list[i])
                        break
                    }
                }
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}