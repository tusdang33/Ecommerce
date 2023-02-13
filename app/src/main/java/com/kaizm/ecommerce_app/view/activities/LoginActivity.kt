package com.kaizm.ecommerce_app.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.kaizm.ecommerce_app.databinding.ActivityLoginBinding
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel
import com.transitionbutton.TransitionButton

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            binding.btnLogin.startAnimation()
            Handler().postDelayed({ startLogin() }, 1000)
        }

        binding.btnReg.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun stopAnimation() {
        binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
    }

    private fun navigateToMainActivity() {
        Toast.makeText(
            this@LoginActivity,
            "Đăng nhập thành công từ Login",
            Toast.LENGTH_SHORT
        ).show()
        val intent: Intent =
            Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun startLogin() {
        val email: String = binding.edtEmail.text.toString()
        val pass: String = binding.edtPass.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.edtEmail.error = "Không được để trống mail"
            binding.edtEmail.requestFocus()
            binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
        } else if (TextUtils.isEmpty(pass)) {
            binding.edtPass.error = "Không được để trống pass"
            binding.edtPass.requestFocus()
            binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
        } else{
            viewModel.signIn(email, pass)
            viewModel.eventLiveData.observe(this) {
                when (it) {
                    is AuthViewModel.Event.LoginSuccess -> navigateToMainActivity()
                    is AuthViewModel.Event.LoginFailed -> stopAnimation()
                    else -> {}
                }
            }
        }
    }
}