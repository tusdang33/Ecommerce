package com.kaizm.ecommerce_app.view.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kaizm.ecommerce_app.R
import com.kaizm.ecommerce_app.databinding.ActivityMainBinding
import com.kaizm.ecommerce_app.model.Role
import com.kaizm.ecommerce_app.view.fragments.HomeFragment
import com.kaizm.ecommerce_app.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.main_fragment)
        if(Role.getInstance().getRole()==1){
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(R.menu.nav_menu_admin)
        }else{
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(R.menu.nav_menu_user)
        }
        binding.bottomNav.setupWithNavController(navController)
    }

    fun hideBottomNavigation() {
        binding.bottomNav.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    fun getBottomNav(navController: NavController) {
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}