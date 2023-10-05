/**
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.onerootv1

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.onerootv1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "TFLite - ODT"
    }

    // view binding
    private lateinit var binding:ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get permission first then open app
        getPermission()
        // A function to hide NavigationBar
        hideSystemUI()

        //shared preferences
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        // get sharedPref values
        val registrationDone = sharedPref.getBoolean("registered", false)
        val autoSignInDone = sharedPref.getBoolean("autoSignIn",false)
        val signedIn = sharedPref.getBoolean("signedIn",false)
        val sessionType = sharedPref.getInt("session",0)

        if (registrationDone)
        {
          if (autoSignInDone or signedIn)
          {
              replaceFragment(HomeFragment())
              binding.bottomNavigationView.setOnItemSelectedListener {
                  when(it.itemId)
                  {
                      R.id.home -> replaceFragment(HomeFragment())
                      R.id.profile -> replaceFragment(ProfileFragment())
                      R.id.settings -> replaceFragment(SettingsFragment())
                      else ->
                      {
                          replaceFragment(HomeFragment())
                      }
                  }
                  true
              }
          }
          else
          {
              if (sessionType == 0) {
                  editor?.apply {
                      putInt("session", 5)
                      apply() //asynchronously
                  }
              }
              // try sign in again
              replaceFragment(LogInFragment())
              this.supportFragmentManager.popBackStack()
          }
        }
        else
        {
            // try registration again
            Toast.makeText(this@MainActivity,"registering user", Toast.LENGTH_LONG).show()
            replaceFragment(RegisterFragment())
            this.supportFragmentManager.popBackStack()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val autoSignInDone = sharedPref.getBoolean("autoSignIn",false)
        if (!autoSignInDone) {
            val editor = sharedPref.edit()
            // shared preferences
            editor.apply {
                putBoolean("signedIn", false)
                apply()
            }
        }
    }

    // Function to hide NavigationBar
    @RequiresApi(Build.VERSION_CODES.S)
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(android.R.id.content)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())

            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * onClick(v: View?)
     *      Detect touches on the UI components
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home -> replaceFragment(HomeFragment())
            R.id.profile -> replaceFragment(ProfileFragment())
            R.id.settings -> replaceFragment(SettingsFragment())
        }
    }


    // permission for camera
    private fun getPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_WIFI_STATE,android.Manifest.permission.INTERNET), 101)
        }
    }

//    private fun restartActivity() {
//        val intent = intent
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        finish()
//        startActivity(intent)
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            getPermission()
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

}


