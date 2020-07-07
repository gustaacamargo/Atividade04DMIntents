package com.camargo.compartilhadora

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity()  {

    var message = ""
    var uri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_share.setOnClickListener {
            message = message_text.text.toString();

            if(message != "") {
                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()

                val shareIntent = Intent(Intent.ACTION_SEND)
                with(shareIntent) {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Compartilhar")
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                startActivity(shareIntent)
            }
            else if(uri != "") {
                Toast.makeText(applicationContext,uri,Toast.LENGTH_SHORT).show()

                val shareIntent = Intent(Intent.ACTION_SEND)
                with(shareIntent) {
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                }
                startActivity(shareIntent)
            }

        }

        bt_select.setOnClickListener{
            askForPermissions();

            openGalleryForImage();
        }
    }

    fun isPermissionsAllowed(): Boolean {
        return if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        if(requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission is granted, you can perform your operation here
            } else {
                // permission is denied, you can ask for permission again, if you want
                //  askForPermissions()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            imageView.setImageURI(data?.data) // handle chosen image
            uri = data?.data.toString()
        }
    }
}
