package com.kaizm.ecommerce_app.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(var id: String = "", var email: String = "", var role: Int = 0) {

    @Exclude
    fun updateRole(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["role"] = role
        return map
    }
}
