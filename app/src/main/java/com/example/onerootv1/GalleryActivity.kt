package com.example.onerootv1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {
    lateinit var rs:Cursor
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        listImages()
    }
    @SuppressLint("Recycle")
    private fun listImages(){
        val gridview: GridView = findViewById(R.id.gridview)
        //https://www.youtube.com/watch?v=ysXd-CupSa0
        val cols = listOf(MediaStore.Images.Thumbnails.DATA).toTypedArray()
//        rs = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            cols,
//            null,
//            null,
//            null)!!
        // getting images from specified folder
        rs = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cols, MediaStore.Images.Media.DATA + " like ?",
            arrayOf("%" + Environment.getExternalStorageDirectory().absolutePath + "/DCIM/one_root_images%"), MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")!!

        gridview.adapter =ImageAdaptor(applicationContext)
        gridview.setOnItemClickListener { parent, view, position, id ->
            rs.moveToPosition(position)
            val path = rs.getString(0)
            val i = Intent(applicationContext, DisplayImageActivity::class.java)
            i.putExtra("path",path)
            startActivity(i)
        }
    }
    inner class ImageAdaptor: BaseAdapter {
        var context: Context
        constructor(context: Context)
        {
            this.context = context
        }
        override fun getCount(): Int {
            return rs.count
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val iv = ImageView(context)
            rs.moveToPosition(position)
            val path = rs.getString(0)
            val bitmap= BitmapFactory.decodeFile(path)
            iv.setImageBitmap(bitmap)
            iv.layoutParams = AbsListView.LayoutParams(300,300)
            return iv
        }

    }
}
