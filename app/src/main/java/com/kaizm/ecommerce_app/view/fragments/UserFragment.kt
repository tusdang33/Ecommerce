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
import com.kaizm.ecommerce_app.databinding.FragmentUserBinding
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel


class UserFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
    lateinit var binding: FragmentUserBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userName.text = viewModel.getUserName()

        binding.userBtnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_infoFragment)
        }

        binding.userBtnAddress.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_addressFragment)
        }


        binding.userBtnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@UserFragment.requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

    }
}