package com.example.onerootv1

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {

    private lateinit var loadButtonEvent: Button
    private lateinit var unLoadButtonEvent: Button
    private lateinit var resumeSessionButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        // inflate the layout and bind to the _binding
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        loadButtonEvent = view.findViewById(R.id.LoadButton)!!
        unLoadButtonEvent = view.findViewById(R.id.unLoadButton)!!
        resumeSessionButton = view.findViewById(R.id.sessionButton)!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = this.activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        val sessionStatus = sharedPref?.getInt("session",5)
        println("sessionStatus: $sessionStatus")
        when (sessionStatus) {
            // loading paused
            3 -> {
                println("user paused loading")
                // disable other buttons
                loadButtonEvent.isEnabled = false
                loadButtonEvent.visibility = View.GONE
                unLoadButtonEvent.isEnabled = false
                unLoadButtonEvent.visibility = View.GONE

                // enable resume button
                resumeSessionButton.isEnabled = true
                resumeSessionButton.visibility = View.VISIBLE

                // press resume session
                resumeSessionButton.setOnClickListener { dispatchTakeVideoIntent() }

            }
            // unloading paused
            4 -> {
                println("user paused unloading")
                loadButtonEvent.isEnabled = false
                loadButtonEvent.visibility = View.GONE
                unLoadButtonEvent.isEnabled = false
                unLoadButtonEvent.visibility = View.GONE

                // enable resume button
                resumeSessionButton.isEnabled = true
                resumeSessionButton.visibility = View.VISIBLE

                // press resume session
                resumeSessionButton.setOnClickListener { dispatchTakeVideoIntent() }
            }
            else -> {
                // disable pause session button
                resumeSessionButton.isEnabled = false
                resumeSessionButton.visibility = View.GONE

                // show other buttons
                loadButtonEvent.isEnabled = true
                loadButtonEvent.visibility = View.VISIBLE
                unLoadButtonEvent.isEnabled = true
                unLoadButtonEvent.visibility = View.VISIBLE

                println("user was choosing: ")
                loadButtonEvent.setOnClickListener {
                    try {
                        println("user chosen loading")
                        // loading play
                        editor?.apply {
                            putInt("session", 1)
                            apply() //asynchronously
                        }
                        // take video
                        dispatchTakeVideoIntent()

                    } catch (e: ActivityNotFoundException)
                    {
                        Log.e(MainActivity.TAG, e.message.toString())
                    }
                }
                unLoadButtonEvent.setOnClickListener {
                    try {
                        // unloading play
                        println("user chosen unloading")
                        editor?.apply {
                            putInt("session", 2)
                            apply() //asynchronously
                        }
                        // take video
                        dispatchTakeVideoIntent()
                    } catch (e: ActivityNotFoundException) {
                        Log.e(MainActivity.TAG, e.message.toString())
                    }
                }
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//    }

//    private fun returnFragmentBack() {
//        activity?.supportFragmentManager?.popBackStack()
//
//    }

    // video intent function
    private fun dispatchTakeVideoIntent() {
        val intent = Intent(activity, VideoActivity::class.java)
        startActivity(intent)
        println("video activity closed")
        replaceFragment(HomeFragment())
        activity?.finish()
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()
    }

    private fun restartActivity() {
        super.onDestroy()
        val i = Intent(activity, MainActivity::class.java)
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
        startActivity(i)
//        activity?.overridePendingTransition(0, 0)
    }

}
