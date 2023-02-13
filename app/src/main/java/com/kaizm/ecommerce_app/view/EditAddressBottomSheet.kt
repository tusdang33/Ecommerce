package com.kaizm.ecommerce_app.view

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DatabaseReference
import com.kaizm.ecommerce_app.databinding.DialogEditAddressBinding
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.viewmodel.AddressViewModel

class EditAddressBottomSheet(private val address: Address?) : BottomSheetDialogFragment() {
    lateinit var binding : DialogEditAddressBinding
    private val viewModel: AddressViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEditAddressBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(address!=null){
            binding.editAddressAddress.setText(address.address.toString())
            binding.editAddressName.setText(address.name.toString())
            binding.editAddressPhone.setText(address.phoneNumber.toString())
            binding.editAddressOk.setOnClickListener {
                val tempAddress  = Address(address.id.toString(), binding.editAddressName.text.toString(), binding.editAddressAddress.text.toString(), binding.editAddressPhone.text.toString())
                viewModel.updateAddress(requireContext(),tempAddress )
                dismiss()
            }
        }else{
            binding.editAddressOk.setOnClickListener {
                if (TextUtils.isEmpty(binding.editAddressAddress.text.toString())) {
                    binding.editAddressAddress.error = "Không được trống địa chỉ"
                    binding.editAddressAddress.requestFocus()
                } else if (TextUtils.isEmpty(binding.editAddressName.text.toString())) {
                    binding.editAddressName.error = "Không được trống tên"
                    binding.editAddressName.requestFocus()
                } else if (TextUtils.isEmpty(binding.editAddressPhone.text.toString())) {
                    binding.editAddressPhone.error = "Không được trống số điện thoại"
                    binding.editAddressPhone.requestFocus()
                } else {
                    viewModel.addAddress(requireContext(), binding.editAddressName.text.toString(), binding.editAddressAddress.text.toString(), binding.editAddressPhone.text.toString())
                    dismiss()
                }
            }
        }
        binding.editAddressCancel.setOnClickListener {
            dismiss()
        }
    }
}