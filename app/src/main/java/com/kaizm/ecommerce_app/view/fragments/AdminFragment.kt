package com.kaizm.ecommerce_app.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.view.activities.LoginActivity
import com.kaizm.ecommerce_app.databinding.FragmentAdminBinding
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel


class AdminFragment : Fragment() {
    lateinit var binding: FragmentAdminBinding

    private val viewModel:AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.adminName.text = viewModel.getUserName()

        binding.adminBtnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AdminFragment.requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.adminBtnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_infoFragment)
        }

        binding.adminBtnProduct.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_managerProductFragment)
        }

        binding.adminBtnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_managerUserFragment)
        }

        binding.adminBtnAddress.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_addressFragment)
        }
    }
}