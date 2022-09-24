package com.friendschat.textdetection

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.friendschat.textdetection.ui.common.BaseActivity
import com.friendschat.textdetection.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding?.root)
        binding?.apply {
            setSupportActionBar(toolbar)
        }
        setNavigation()
    }

    private fun setNavigation() {
        val navHostFramgnet =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFramgnet.findNavController()
        navController?.let { setupActionBarWithNavController(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() == true || super.onSupportNavigateUp()
    }
}