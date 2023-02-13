package com.kaizm.ecommerce_app.repository

import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product

class ProductRepository {

    fun readData(query: Query, iGetDataListener: IGetDataListener) {
        val list: MutableList<Product> = mutableListOf()
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val product = snapshot.getValue(Product::class.java)!!
                list.add(product)
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val product = snapshot.getValue(Product::class.java)
                if (list.isEmpty()) return

                for (i in list.indices) {
                    if (product!!.id == list[i].id) {
                        list[i] = product
                        break
                    }
                }
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                if (list.isEmpty()) return

                for (i in list.indices) {
                    if (product!!.id == list[i].id) {
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