package com.example.onerootv1


import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.onerootv1.MainActivity.Companion.TAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class RegisterFragment : Fragment() {
    private var rgUsername = ""
    private var mobile = ""
    private var  selectedLocation = "None"
    private var  selectedRole = "None"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_register,container,false)

        // get reference to all views
        val registerUserName = view.findViewById<EditText>(R.id.register_user_name)
        val registerPassword = view.findViewById<EditText>(R.id.register_password)
        val mobileNo = view.findViewById<EditText>(R.id.mobile_no)

        val btnReset = view.findViewById<Button>(R.id.reset)
        val btnSubmit = view.findViewById<Button>(R.id.submit)

        //<---------------------------- location auto complete ---------------------------->
        // https://www.geeksforgeeks.org/exposed-drop-down-menu-in-android/
        //https://www.youtube.com/watch?v=741l_fPKL3Y&t=518s
        // get reference to the string array that we just created
        val locationArray = resources.getStringArray(R.array.location_list)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = activity?.let { ArrayAdapter(it, R.layout.dropdown_item, locationArray) }
        // get reference to the autocomplete text view
        val autocompleteTV = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV?.setAdapter(arrayAdapter)
        autocompleteTV?.setOnItemClickListener { _, _, _, _ ->
            selectedLocation = autocompleteTV.text.toString()
            println("location: $selectedLocation")
        }

        //<---------------------------- role auto complete ---------------------------->
        val roleArray = resources.getStringArray(R.array.role_list)
        val arrayAdapter2 = activity?.let { ArrayAdapter(it, R.layout.dropdown_item, roleArray) }
        val autocompleteTV2 = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView2)
        autocompleteTV2?.setAdapter(arrayAdapter2)
        autocompleteTV2?.setOnItemClickListener { _, _, _, _ ->
            selectedRole = autocompleteTV2.text.toString()
            println("Role: $selectedRole")
        }
        //<---------------------------------------------------------------------------------->

        //shared preferences
        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        // at first put registration boolean to false
        editor?.apply {
            putBoolean("registered",false)
            apply() //asynchronously
        }

        btnReset?.setOnClickListener {
            // clearing user_name and password edit text views on reset button click
            registerUserName?.setText("")
            registerPassword?.setText("")
            mobileNo?.setText("")
            Toast.makeText(activity,"data cleared", Toast.LENGTH_LONG).show()
        }

        // submit button pressed
        btnSubmit?.setOnClickListener {
            rgUsername = registerUserName?.text.toString()
            val rgPassword = registerPassword?.text.toString()
            mobile = mobileNo?.text.toString()

            Toast.makeText(activity,"$rgUsername was registered ", Toast.LENGTH_LONG).show()
            editor?.apply {
                putBoolean("registered",true)
                putString("username",rgUsername)
                putString("password",rgPassword)
//                putString("mobileNo",mobile)
                putInt("imageNo",1)
                putInt("sessionNo",0)
                putInt("minArrowDist", 8)
                putInt("maxArrowDist",99)
                apply() //asynchronously
            }
            Toast.makeText(activity,"preferences saved", Toast.LENGTH_LONG).show()
            // saving profile data in a json file
            saveProfileDataToFolder()

            // check internet connection before storing in database
            if (activity?.let { it1 -> checkForInternet(it1) } == true)
            {
                Toast.makeText(activity, "internet available", Toast.LENGTH_SHORT).show()
                // saving profile data in a firebase database
                saveProfileDataToFirebase()
                // reading From database
                //readProfileDataFromFirebase()
            }
            else
            {

                Toast.makeText(activity, "internet  not available", Toast.LENGTH_SHORT).show()
                // <------------------ alert box ----------------------------------->
                // https://www.digitalocean.com/community/tutorials/android-alert-dialog-using-kotlin
                // alert color problem https://stackoverflow.com/questions/39481735/alertdialog-do-not-show-positive-and-negative-button
                val builder = activity?.let { it1 -> AlertDialog.Builder(it1) }
                //set title for alert dialog
                builder?.setTitle("internet alert")
                //set message for alert dialog
                builder?.setMessage("Please connect to internet")
                builder?.setIcon(android.R.drawable.ic_popup_reminder)

                // get wifi settings
                val wifi = activity?.getSystemService(WIFI_SERVICE) as WifiManager
                builder?.setPositiveButton("connect to wifi") { _, _ ->
                    wifi.isWifiEnabled = true
                }
                builder?.setNegativeButton("cancel") { _, _ ->
                    wifi.isWifiEnabled = false
                }

                // colors for alert buttons
                val alert = builder?.create()
                alert?.setOnShowListener {
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(Color.BLUE)
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.BLUE)
                }
                alert?.show()
            }

            // go to sign in fragment
            replaceFragment(LogInFragment())
        }

        // Inflate the layout for this fragment
        return view

    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.frame_layout,fragment)
        fragmentTransaction?.commit()
    }

    private  fun saveProfileDataToFolder()
    {
        val filepath = "OneRootFiles"
        val fileName  = "profile.json"
        println("storing json data on registration")

        fun isExternalStorageWritable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

        fun saveToExternalStorage(jsonData:String) {
            val filePath = context?.getExternalFilesDir(filepath)
            val myExternalFile = File(filePath, fileName)
            try {
                val fileOutputStream = FileOutputStream(myExternalFile)
                fileOutputStream.write(jsonData.toByteArray())
                fileOutputStream.close()
                println("file saved in $myExternalFile")

            } catch (e: IOException) {
                e.printStackTrace()
                println("exception in save to external storage")
            }
        }

        // profile data created and saved in folder
        val profileJson = JsonObject()
        try {
            profileJson.addProperty("username", rgUsername)
            profileJson.addProperty("mobileNo", mobile)
            profileJson.addProperty("numberOfCoconuts", 1)
            profileJson.addProperty("numberOfSessions", 1)
            profileJson.addProperty("location", selectedLocation)
            profileJson.addProperty("role",selectedRole)

            // converting json object to json string
            val gson = Gson()
            val jsonString = gson.toJson(profileJson)
            if (isExternalStorageWritable()) {
                saveToExternalStorage(jsonString)
                Toast.makeText(activity, "profile json data stored on registration", Toast.LENGTH_SHORT).show()
                println("<--- profile.json file stored successfully ")
            }
            else
            {
                Toast.makeText(activity, "External storage not available for writing profile json on registration", Toast.LENGTH_SHORT).show()
                println("External storage not available for storing profile.json file")
            }
        }
        catch (e: JSONException) {
            println("error in register fragment: json file cant be saved")
            e.printStackTrace()
        }
    }

    // creating database
    private val db = Firebase.firestore
    private fun saveProfileDataToFirebase()
    {
        // firebase activation
        //https://www.youtube.com/watch?v=rFTJTLdoGDY&list=PLHQRWugvckFry9Q1OT6hLNfyUizT73PwX&index=2
        try {
            val user = hashMapOf(
                "username" to rgUsername,
                "mobileNo" to mobile,
                "numberOfCoconuts" to 1,
                "numberOfSessions" to 1,
                "location" to selectedLocation,
                "role" to selectedRole
            )

            // Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
//                    Toast.makeText(activity, "firebase profile data updated", Toast.LENGTH_SHORT).show()
                    println("<-------------database updated ------------->")
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document to firebase", e)
                }
        }
        catch (e:Exception)
        {
            println("error in save profile data in fire base: $e" )
        }



    }

    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val netInfo = cm!!.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals(
                    "WIFI",
                    ignoreCase = true
                )
            ) if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals(
                    "MOBILE",
                    ignoreCase = true
                )
            ) if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection

        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }

    private fun readProfileDataFromFirebase()
    {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

}

