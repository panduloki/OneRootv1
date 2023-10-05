package com.example.onerootv1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class SettingsFragment : Fragment() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view3 = inflater.inflate(R.layout.fragment_settings, container, false)
        val minDistTextElement = view3.findViewById<EditText>(R.id.minNumberDecimal)
        val maxDistTextElement = view3.findViewById<EditText>(R.id.maxNumberDecimal)
        val saveButton = view3.findViewById<Button>(R.id.settingsButton)
        val numElement = view3.findViewById<EditText>(R.id.no)
        val serverButton = view3.findViewById<Button>(R.id.serverButton)
        val locationButton = view3.findViewById<Button>(R.id.locationButton)


        // getting shared preference
        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        // getting min and max distance from shared pref
        val minArrowDistance = sharedPref?.getInt("minArrowDist", 10)
        val maxArrowDistance = sharedPref?.getInt("maxArrowDist", 100)
        val totalImageNo = sharedPref?.getInt("imageNo",0)


        // hint changed to saved preference values
        minDistTextElement.hint = minArrowDistance.toString()
        maxDistTextElement.hint = maxArrowDistance.toString()
        numElement.hint = totalImageNo.toString()

        val minDistText = minDistTextElement?.text
        val maxDistText = maxDistTextElement?.text
        val newNo = numElement.text

        saveButton?.setOnClickListener {
            println("user minDistText $minDistText")
            println("user maxDistText $maxDistText")
            println("user total no $newNo")

            println("saved minArrowDistance $minArrowDistance")
            println("saved maxArrowDistance $maxArrowDistance")
            println("saved total Images no $totalImageNo")

            // checking user gave input

                editor?.apply {
                    if (minDistText.toString()!="")
                    {
                        putInt("minArrowDist", minDistText.toString().toInt())
                    }
                    if (maxDistText.toString()!="")
                    {
                        putInt("maxArrowDist", maxDistText.toString().toInt())
                    }
                    if (newNo.toString()!="")
                    {
                        putInt("imageNo",newNo.toString().toInt())
                    }
                    apply()
                }
                Toast.makeText(activity, "settings updated", Toast.LENGTH_LONG).show()

        }

        serverButton.setOnClickListener {
            replaceFragment(ServerFragment())
        }

        locationButton.setOnClickListener {
            replaceFragment(LocationFragment())
        }

        return view3
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()
    }
}