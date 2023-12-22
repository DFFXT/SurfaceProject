package com.example.surfaceproject.permission

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class IOPermission(activity: AppCompatActivity) {
    private var ioPermissionCallback: ((Boolean) -> Unit)? = null
    private val ioPermission = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        ioPermissionCallback?.invoke(it)
        ioPermissionCallback = null
    }

    fun request(callback: ((Boolean) -> Unit)) {
        ioPermissionCallback = callback
        ioPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
