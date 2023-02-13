package com.kaizm.ecommerce_app.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.model.Role
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val viewModel = AuthViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Role.getInstance().getRole()
        Handler().postDelayed({
            viewModel.checkUser()
            viewModel.eventLiveData.observe(this){
                when(it){
                    is AuthViewModel.Event.IsLogged -> goToMainActivity()
                    is AuthViewModel.Event.IsNotLogged -> goToLogin()
                    else -> {}
                }
            }
        },1000)
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
    }

    private fun goToLogin() {
        startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
    }
}