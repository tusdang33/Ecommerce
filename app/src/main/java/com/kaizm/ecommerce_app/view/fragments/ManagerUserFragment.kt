package com.kaizm.ecommerce_app.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.kaizm.ecommerce_app.view.activities.MainActivity
import com.kaizm.ecommerce_app.adapter.UserAdapter
import com.kaizm.ecommerce_app.databinding.FragmentManagerUserBinding
import com.kaizm.ecommerce_app.model.User
import com.kaizm.ecommerce_app.view.UpdateRoleBottomSheet
import com.kaizm.ecommerce_app.viewmodel.UserViewModel


class ManagerUserFragment : Fragment() {
    lateinit var binding : FragmentManagerUserBinding
    private val viewModel: UserViewModel by activityViewModels()
    lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).hideBottomNavigation()
        binding =  FragmentManagerUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillData()

        binding.userlistList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.userlistList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        userAdapter = UserAdapter(requireContext(), object : UserAdapter.IClickItemUser{
            override fun onClickUser(user: User) {
                openDialogEditUser(user)
            }
        })

        binding.userlistList.adapter = userAdapter

        binding.userlistBack.setOnClickListener {
            findNavController().popBackStack()

        }
    }

    private fun fillData() {
        viewModel.setData(FirebaseDatabase.getInstance().getReference("account"))
        viewModel.userLiveData.observe(viewLifecycleOwner){
            userAdapter.updateList(it)
        }
    }

    private fun openDialogEditUser(user: User) {
        val updateRoleBottomSheet = UpdateRoleBottomSheet(user)
        updateRoleBottomSheet.show(parentFragmentManager, null)
    }
    override fun onStop() {
        super.onStop()
        (activity as MainActivity).showBottomNavigation()
    }
}