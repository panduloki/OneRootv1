package com.example.onerootv1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket



import android.widget.Toast
import org.json.JSONObject
import java.io.FileReader



class ServerFragment : Fragment() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view1 = inflater.inflate(R.layout.fragment_server, container, false)
        val sendButton = view1.findViewById<Button>(R.id.sendDataButton)
        val syncProfButton = view1.findViewById<Button>(R.id.saveProfDataButton)
        // Get the reference to the TextView element
        val textView = view1.findViewById<TextView>(R.id.textView2)

        // sending all images through server
        sendButton?.setOnClickListener {
            storeProfileData()
            textView.text = "image sent"
            Toast.makeText(activity,"photo sent", Toast.LENGTH_LONG).show()
        }

        // saving profile data in internal storage
        syncProfButton.setOnClickListener {
            try {
                storeProfileData()
                textView.text = "profile data saved"
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }
        return view1
    }

    // create profile data class
    class ProfileData(private val username:String, private val mobileNo:String, private val imageNo:Int)
    {
        override fun toString(): String {
            return "Category [name: ${this.username}, phNo: ${this.mobileNo}, noImg: ${this.imageNo}]"
        }
    }

    private fun storeProfileData() {
        fun storeJsonData(context: Context, fileName: String, jsonData: String)
        {
            // https://www.youtube.com/watch?v=JUlZYddw03o
            // https://github.com/sandipapps/ExternalStorageDemo/blob/master/app/src/main/java/com/sandipbhattacharya/externalstoragedemo/MainActivity.java

            val filepath = "OneRootFiles"
            println("storing json data")

            fun isExternalStorageWritable(): Boolean {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED == state
            }

            fun isExternalStorageReadable(): Boolean {
                val state = Environment.getExternalStorageState()
                return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
            }

            fun saveToExternalStorage() {

                val myExternalFile = File(context.getExternalFilesDir(filepath), fileName)
                try {
                    println("file saved in $myExternalFile")
                    val fileOutputStream = FileOutputStream(myExternalFile)
                    fileOutputStream.write(jsonData.toByteArray())
                    fileOutputStream.close()

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            fun readFromExternalStorage(): String {
                val myExternalFile = File(context.getExternalFilesDir(filepath), fileName)
                val stringBuilder = StringBuilder()
                try {
                    val fileReader = FileReader(myExternalFile)
                    val bufferedReader = BufferedReader(fileReader)
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    bufferedReader.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return stringBuilder.toString()
            }

            // write file  to external storage
            if (isExternalStorageWritable()) {
                saveToExternalStorage()
                println(" json data stored")
            } else {
                Toast.makeText(context, "External storage not available for writing", Toast.LENGTH_SHORT).show()
            }

            // read file  from external storage
            if (isExternalStorageReadable()) {
                println("reading from json data file")
                val dataFromJson = readFromExternalStorage()
                println(" json data:  $dataFromJson")
                val jsonObject = JSONObject(dataFromJson)
                val usernameFromFile = jsonObject.getString("username")
                val mobileNoFromFile = jsonObject.getString("mobileNo")
                println("username from file: $usernameFromFile")
                println("mobile No from file: $mobileNoFromFile")
            }
            else
            {
                Toast.makeText(context, "External storage not available for reading", Toast.LENGTH_SHORT).show()
            }
        }

        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val userName = sharedPref?.getString("username", null)
        val location = ""
        val mobileNo = sharedPref?.getString("mobileNo", null)
        val noOfImages = sharedPref?.getInt("imageNo", 0)!!

        val gson = Gson()
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        // creating class object
        val tut = ProfileData(userName.toString(), mobileNo.toString(), noOfImages)
        // convert to json object
        val jsonTut: String = gson.toJson(tut)
        println(jsonTut)
        // convert to pretty json object
        val jsonTutPretty: String = gsonPretty.toJson(tut)
        println(jsonTutPretty)
        // saving in json file
        context?.let { storeJsonData(it,"profile.json",jsonTutPretty) }
    }

}


