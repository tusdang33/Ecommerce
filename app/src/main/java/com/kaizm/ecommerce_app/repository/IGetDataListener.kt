package com.kaizm.ecommerce_app.repository

import com.kaizm.ecommerce_app.model.Product

interface IGetDataListener {
    fun onGetSuccess(any: Any)
}