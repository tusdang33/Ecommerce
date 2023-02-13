package com.kaizm.ecommerce_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.databinding.DialogDeleteBinding
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.viewmodel.AddressViewModel
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel

class DeleteBottomSheet(val any: Any) : BottomSheetDialogFragment(){
    lateinit var binding : DialogDeleteBinding
    private val productViewModel: ProductViewModel by activityViewModels()
    private val addressViewModel: AddressViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteBinding.inflate(inflater, container,false)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogDelCancel.setOnClickListener {
            dismiss()
        }

        if(any is Product){
            binding.dialogDelOk.setOnClickListener {
                productViewModel.deleteProduct(requireContext(), any)
                dismiss()
            }
        }else{
            binding.dialogDelOk.setOnClickListener {
                addressViewModel.deleteAddress(requireContext(), any as Address)
                dismiss()
            }
        }

    }
}