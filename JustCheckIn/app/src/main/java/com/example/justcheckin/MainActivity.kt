package com.example.justcheckin

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.NetworkUtils
import com.example.justcheckin.databinding.ActivityMainBinding
import com.example.justcheckin.utils.Constant
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var quit: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_account
            )
        )
        //取消actionbar，注释下边这条
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /*双击退出程序*/
    override fun onBackPressed() {
        if (quit == false) { //询问是否退出程序
            Toast.makeText(this@MainActivity, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            Timer(true).schedule(object : TimerTask() {
                override fun run() {
                    quit = false
                }
            }, 2000)
            quit = true
        } else {
            super.onBackPressed()
            finish()
        }
    }

}