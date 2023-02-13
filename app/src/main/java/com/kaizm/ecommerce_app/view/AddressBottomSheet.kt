package com.kaizm.ecommerce_app.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.view.fragments.AddressFragment


class AddressBottomSheet(private val iSendAddress: ISendAddress):BottomSheetDialogFragment(), AddressFragment.IAddressButtonClickListener {

    interface ISendAddress{
        fun sendAddress(address: Address)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(com.google.android.material.R.layout.design_bottom_sheet_dialog, container, false)
        val fragmentManager: FragmentManager = childFragmentManager
        val addressFragment = AddressFragment()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.google.android.material.R.id.container, addressFragment)
        fragmentTransaction.commit()
        addressFragment.setOnButtonClickListener(this)
        (activity as MainActivity).hideBottomNavigation()
        return view
    }

    override fun onClickButtonBack() {
        (activity as MainActivity).hideBottomNavigation()
        this.dismiss()
    }

    override fun onClickItem(address: Address) {
        iSendAddress.sendAddress(address)
        this.dismiss()
    }
}