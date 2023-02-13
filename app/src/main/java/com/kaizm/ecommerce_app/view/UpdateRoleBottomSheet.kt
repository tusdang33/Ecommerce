package com.kaizm.ecommerce_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaizm.ecommerce_app.databinding.DialogEditAddressBinding
import com.kaizm.ecommerce_app.databinding.DialogRoleBinding
import com.kaizm.ecommerce_app.model.User
import com.kaizm.ecommerce_app.viewmodel.AddressViewModel
import com.kaizm.ecommerce_app.viewmodel.UserViewModel

class UpdateRoleBottomSheet(val user: User):BottomSheetDialogFragment() {
    lateinit var binding : DialogRoleBinding
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogRoleBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogAccId.text = user.id
        binding.dialogAccEmail.text = user.email

        if (user.role == 1) {
            binding.dialogAccRoleAdmin.isChecked = true
        } else {
            binding.dialogAccRoleUser.isChecked = true
        }

        binding.dialogAccCancel.setOnClickListener {
            dismiss()
        }

        binding.dialogAccSet.setOnClickListener {
            viewModel.setRole(requireContext(),user, if(binding.dialogAccRoleAdmin.isChecked) 1 else 0)
            dismiss()
        }
    }
}