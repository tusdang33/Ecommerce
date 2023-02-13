package com.kaizm.ecommerce_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kaizm.ecommerce_app.databinding.FragmentManagerUserBinding
import com.kaizm.ecommerce_app.databinding.ItemUserBinding
import com.kaizm.ecommerce_app.model.User

class UserAdapter(val context: Context, val iClickItemUser: IClickItemUser) : RecyclerView.Adapter<UserAdapter.UserHolder>() {

    interface IClickItemUser {
        fun onClickUser(user: User)
    }

    private val list : MutableList<User> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<User>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class UserHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User){
            binding.userEmail.text = user.email
            binding.userId.text = user.id
            binding.userEdit.setOnClickListener {
                iClickItemUser.onClickUser(user)
            }
        }
    }
}