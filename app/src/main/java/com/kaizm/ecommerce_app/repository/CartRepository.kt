package com.kaizm.ecommerce_app.repository

import android.util.Log
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.model.Cart
import com.kaizm.ecommerce_app.model.Product

class CartRepository {

    fun readData(query: Query, iGetDataListener: IGetDataListener) {
        val list: MutableList<Cart> = mutableListOf()
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val cart = snapshot.getValue(Cart::class.java)
                list.add(cart!!)
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val cart = snapshot.getValue(Cart::class.java)
                if (list.isEmpty()) return

                for (i in list.indices) {
                    if (cart!!.id == list[i].id) {
                        list[i] = cart
                        break
                    }
                }
                iGetDataListener.onGetSuccess(list)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val cart = snapshot.getValue(Cart::class.java)
                if (list.isEmpty()) return

                for (i in list.indices) {
                    if (cart!!.id == list[i].id) {
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

    fun getQuantity(cart: Cart, iGetDataListener: IGetDataListener){
        val reference = FirebaseDatabase.getInstance().getReference("products")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val quantity = snapshot.child(cart.id.toString()).child("quantity").getValue(Long::class.java)
                if (quantity != null) {
                    iGetDataListener.onGetSuccess(quantity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}