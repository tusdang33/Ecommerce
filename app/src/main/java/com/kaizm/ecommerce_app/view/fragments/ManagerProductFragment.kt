package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.adapter.ManagerProductAdapter
import com.kaizm.ecommerce_app.databinding.FragmentManagerProductBinding
import com.kaizm.ecommerce_app.model.Product
import com.kaizm.ecommerce_app.view.DeleteBottomSheet
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.viewmodel.ProductViewModel

class ManagerProductFragment : Fragment() {
    private lateinit var binding: FragmentManagerProductBinding

    private val viewModel: ProductViewModel by activityViewModels()

    private lateinit var managerAdapter: ManagerProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).hideBottomNavigation()
        binding = FragmentManagerProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillData(FirebaseDatabase.getInstance().getReference("products"))

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        binding.managerCategory.adapter = adapter

        binding.managerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent!!.getItemAtPosition(position).toString() != "All") {
                    viewModel.filterProductByCategory(
                        parent.getItemAtPosition(position).toString()
                    )
                    viewModel.productListLiveData.observe(viewLifecycleOwner) {
                        managerAdapter.updateList(it)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        binding.managerBtnPopular.setOnClickListener {
            fillData(FirebaseDatabase.getInstance().getReference("products").orderByChild("time"))
        }

        binding.managerBtnPrice.setOnClickListener {
            if (binding.managerBtnPrice.text == "Giá tăng") {
                viewModel.filterProductByPrice(false)
                binding.managerBtnPrice.text = "Giá giảm"
            } else {
                viewModel.filterProductByPrice(true)
                binding.managerBtnPrice.text = "Giá tăng"
            }
            viewModel.productListLiveData.observe(viewLifecycleOwner) {
                managerAdapter.updateList(it)
            }
        }

        binding.managerSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.filterProductBySearch(it) }
                viewModel.productListLiveData.observe(viewLifecycleOwner) {
                    managerAdapter.updateList(it)
                }
                return true
            }
        })

        binding.managerList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.managerList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        managerAdapter = ManagerProductAdapter(requireContext(), object : ManagerProductAdapter.IClickItemManagerProduct {
            override fun onClickToItem(product: Product) {
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                findNavController().navigate(R.id.detailFragment, bundle)
            }

            override fun onClickToEdit(product: Product) {
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                findNavController().navigate(
                    R.id.action_managerProductFragment_to_editProductFragment,
                    bundle
                )
            }

            override fun onClickToDelete(product: Product) {
                openDialogDelItem(product)
            }
        })
        binding.managerList.adapter = managerAdapter

        binding.prolistBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.prolistAdd.setOnClickListener {
            findNavController().navigate(R.id.action_managerProductFragment_to_addProductFragment)
        }


    }

    private fun fillData(query: Query) {
        viewModel.setListData(query)
        viewModel.productListLiveData.observe(viewLifecycleOwner) {
            managerAdapter.updateList(it)
        }
    }

    private fun openDialogDelItem(product: Product) {
        val deleteBottomSheet = DeleteBottomSheet(product)
        deleteBottomSheet.show(parentFragmentManager, null)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).showBottomNavigation()
    }
}