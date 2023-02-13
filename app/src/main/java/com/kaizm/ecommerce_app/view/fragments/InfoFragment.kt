package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.databinding.FragmentInfoBinding
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel

class InfoFragment : Fragment() {
    lateinit var binding: FragmentInfoBinding
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).hideBottomNavigation()
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.infoEmail.text = viewModel.getUserEmail()

        binding.infoBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.infoUpdate.setOnClickListener {
            val name: String = binding.infoName.text.toString()
            val pass: String = binding.infoPass.text.toString()
            val cPass: String = binding.infoCpass.text.toString()

            if (pass != cPass) {
                binding.infoCpass.setError("Mật khẩu không khớp")
                binding.infoCpass.requestFocus()
            } else if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(name)) {
                if (pass.length < 6) {
                    binding.infoPass.error = "Mật khẩu phải trên 6 ký tự"
                    binding.infoPass.requestFocus()
                } else {
                    updatePass(pass)
                    updateName(name)
                }
            } else if (!TextUtils.isEmpty(pass)) {
                if (pass.length < 6) {
                    binding.infoPass.setError("Mật khẩu phải trên 6 ký tự")
                    binding.infoPass.requestFocus()
                } else {
                    updatePass(pass)
                }
            } else if (!TextUtils.isEmpty(name)) {
                updateName(name)
            } else {
                Toast.makeText(requireContext(), "Không có gì để update", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updatePass(pass: String) {
        viewModel.updateUserPassword(requireContext(), pass)
    }

    private fun updateName(name: String) {
        viewModel.updateUserName(requireContext(), name)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).showBottomNavigation()
    }
}