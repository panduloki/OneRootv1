package com.example.onerootv1

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment



class ProfileFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // shared preference
        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val userName = sharedPref?.getString("username", null)
        val noOfCoconuts = sharedPref?.getInt("imageNo", 0)!!
        val sessionsNo = sharedPref.getInt("sessionNo", 0)

        // Inflate the layout for this fragment
        val view1 = inflater.inflate(R.layout.fragment_profile, container, false)

        // put user name in profile
        val myTextView = view1.findViewById<TextView>(R.id.textView)
        myTextView.text = userName

        // displaying no of coconuts collected
        val coconutElement = view1.findViewById<TextView>(R.id.coconutNo)
        coconutElement.text = noOfCoconuts.toString()

        val sessionElement = view1.findViewById<TextView>(R.id.SessionsNo)
        sessionElement.text = sessionsNo.toString()

        // gallery button press
        val galleryButton = view1.findViewById<Button>(R.id.gallery_button)
        galleryButton?.setOnClickListener {
            dispatchGalleryIntent()
        }
        return view1

    }
    private fun dispatchGalleryIntent() {
        val i = Intent(activity, GalleryActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }
}
