package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.kaizm.ecommerce_app.adapter.AddressAdapter
import com.kaizm.ecommerce_app.databinding.FragmentAddressBinding
import com.kaizm.ecommerce_app.model.Address
import com.kaizm.ecommerce_app.view.DeleteBottomSheet
import com.kaizm.ecommerce_app.view.EditAddressBottomSheet
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.viewmodel.AddressViewModel

class AddressFragment : Fragment() {
    private val viewModel: AddressViewModel by activityViewModels()
    lateinit var binding: FragmentAddressBinding
    lateinit var addressAdapter: AddressAdapter

    interface IAddressButtonClickListener {
        fun onClickButtonBack()
        fun onClickItem(address: Address)
    }

    private var iAddressButtonClickListener: IAddressButtonClickListener? = null

    fun setOnButtonClickListener(listener: IAddressButtonClickListener?) {
        iAddressButtonClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).hideBottomNavigation()
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillData()

        binding.addressList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        addressAdapter = AddressAdapter(requireContext(), object : AddressAdapter.IClickItemAddress {
            override fun onClickChooseAddress(address: Address) {
                iAddressButtonClickListener?.onClickItem(address)
            }

            override fun onClickEditAddress(address: Address) {
                openAddressDialog(address)
            }

            override fun onClickDeleteAddress(address: Address) {
                openDeleteDialog(address)
            }
        })

        binding.addressList.adapter = addressAdapter

        binding.addressBack.setOnClickListener {
            if (iAddressButtonClickListener != null) {
                iAddressButtonClickListener!!.onClickButtonBack()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.addressAdd.setOnClickListener {
            openAddressDialog(null)
        }
    }

    private fun fillData() {
        viewModel.getData(FirebaseDatabase.getInstance().getReference("address"))
        viewModel.addressLiveData.observe(viewLifecycleOwner) {
            addressAdapter.updateList(it)
        }
    }

    private fun openAddressDialog(address: Address?) {
        val editAddressBottomSheet = EditAddressBottomSheet(address)
        editAddressBottomSheet.show(parentFragmentManager, null)
    }

    private fun openDeleteDialog(address: Address) {
        val deleteBottomSheet = DeleteBottomSheet(address)
        deleteBottomSheet.show(parentFragmentManager, null)
    }

    override fun onStop() {
        super.onStop()
        if (iAddressButtonClickListener == null)
            (activity as MainActivity).showBottomNavigation()
    }
}