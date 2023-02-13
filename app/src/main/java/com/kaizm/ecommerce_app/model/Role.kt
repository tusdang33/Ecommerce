package com.kaizm.ecommerce_app.model

class Role() {

    private var role: Int = 0

    private object Holder {
        val INSTANCE = Role()
    }

    companion object {
        @JvmStatic
        fun getInstance(): Role {
            return Holder.INSTANCE
        }
    }

    public fun getRole(): Int {
        return role
    }

    public fun setRole(role: Int) {
        this.role = role;
    }

    public fun getName():String{
        return if (role == 1) {
            "ADMIN"
        } else {
            "USER"
        }
    }
}