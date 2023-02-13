package com.kaizm.ecommerce_app.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kaizm.ecommerce_app.databinding.ActivityRegisterBinding
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel
import com.transitionbutton.TransitionButton

class RegisterActivity : AppCompatActivity() {

    private val viewModel = AuthViewModel()

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnReg.setOnClickListener {
            binding.btnReg.startAnimation()
            Handler().postDelayed({

                createAccount()
            }, 1000)
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount() {
        val email: String = binding.edtEmail.text.toString()
        val pass: String = binding.edtPass.text.toString()
        val cpass: String = binding.edtCpass.text.toString()

        if (TextUtils.isEmpty(email)) {
            binding.edtEmail.error = "Không được để trống mail"
            binding.edtEmail.requestFocus()
            binding.btnReg.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
        } else if (TextUtils.isEmpty(pass)) {
            binding.edtPass.error = "Không được để trống pass"
            binding.edtPass.requestFocus()
            binding.btnReg.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
        } else if (pass != cpass) {
            binding.edtCpass.error = "Mật khẩu không trùng khớp"
            binding.edtCpass.requestFocus()
            binding.btnReg.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
        } else {
            viewModel.createAccount(binding.edtEmail.text.toString(), binding.edtPass.text.toString() )
            viewModel.eventLiveData.observe(this){
                when(it){
                    is AuthViewModel.Event.CreateSuccess -> navigateToMainActivity()
                    is AuthViewModel.Event.CreateFail -> stopAnimation()
                    else -> {}
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        Toast.makeText(this@RegisterActivity, "Đăng kí thành công", Toast.LENGTH_LONG).show()
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun stopAnimation() {
        viewModel.errorCodeLiveData.observe(this){
            when (it) {
                "ERROR_INVALID_EMAIL" -> {
                    binding.edtEmail.error = "Email không đúng định dạng"
                    binding.edtEmail.requestFocus()
                }
                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                    binding.edtEmail.error = "Email đã được đăng kí bởi tài khoản khác"
                    binding.edtEmail.requestFocus()
                }
                "ERROR_WEAK_PASSWORD" -> {
                    binding.edtPass.error = "Mật khẩu ít nhất 6 kí tự"
                    binding.edtPass.requestFocus()
                }
                else -> {

                }
            }
        }
        binding.btnReg.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
    }

}