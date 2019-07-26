package com.watering.moneyrecord.model

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.R

import java.io.*

class DBFile(val context: Context) {
    val dbFileName = "AssetLog.db"
    val app = context as MainActivity
    private var mStorage = FirebaseStorage.getInstance()
    private var mStorageRef = mStorage.reference

    fun requestDownload() {
        val file = Uri.fromFile(app.getDatabasePath(dbFileName))
        val fileRef = mStorageRef.child("${app.mUser?.uid}/database/${file.lastPathSegment}")

        fileRef.getFile(file).addOnSuccessListener {
            Toast.makeText(context, R.string.toast_db_download_ok, Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, R.string.toast_db_download_error, Toast.LENGTH_SHORT).show()
        }
    }

    fun requestUpload() {
        val file = Uri.fromFile(app.getDatabasePath(dbFileName))
        val fileRef = mStorageRef.child("${app.mUser?.uid}/database/${file.lastPathSegment}")

        fileRef.putFile(file).addOnFailureListener {
            Toast.makeText(context, R.string.toast_db_upload_error, Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(context, R.string.toast_db_upload_ok, Toast.LENGTH_SHORT).show()
        }
    }

    fun requestDelete() = try {
        context.getDatabasePath(dbFileName).delete()
        Toast.makeText(context, R.string.toast_db_del_ok, Toast.LENGTH_SHORT).show()
    } catch (e: FileNotFoundException) {
        Toast.makeText(context, R.string.toast_db_del_error, Toast.LENGTH_SHORT).show()
    }
}