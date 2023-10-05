package com.example.onerootv1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment


class LogInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_log_in,container,false)
        // get reference to all views
        val userText = view.findViewById<EditText>(R.id.username)
        val passText = view.findViewById<EditText>(R.id.password)
        val signInBtn = view.findViewById<Button>(R.id.signInButton)
        val signInCheck = view.findViewById<CheckBox>(R.id.signInCheck)

        //shared preferences
        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        signInBtn?.setOnClickListener {
            val sgUsername = userText?.text.toString()
            val sgPassword = passText?.text.toString()
            val autoSignIn = signInCheck?.isChecked

            val userName = sharedPref?.getString("username", null)
            val passWord = sharedPref?.getString("password",null)

            // your code to validate the user_name and password combination
            // and verify the same
            if ((sgUsername == userName) && (sgPassword== passWord)) {
                Toast.makeText(activity,"signed in", Toast.LENGTH_LONG).show()
                editor?.apply {
                    if (autoSignIn == true) {
                        putBoolean("autoSignIn", true)
                    }
                    else
                    {
                        putBoolean("autoSignIn", false)
                    }
                    putBoolean("signedIn",true)

                    apply()
                }
                // go to main fragment
                replaceFragment(HomeFragment())
            }
            else{
                editor?.apply {
                    putBoolean("signedIn",false)
                    putBoolean("autoSignIn",false)
                    apply()
                }
                if (sgUsername != userName) {
                    Toast.makeText(activity, "user name was wrong try again", Toast.LENGTH_LONG).show()
                }
                if (sgPassword != passWord)
                {
                    Toast.makeText(activity, "password was incorrect try again", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }


//    private fun returnFragmentBack() {
//        activity?.supportFragmentManager?.popBackStack()
//
//    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()

    }

}