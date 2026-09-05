package com.alpha.voiceassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val status = TextView(this).apply {
            textSize = 16f
            setPadding(48, 96, 48, 48)
        }
        setContentView(status)

        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isEmpty()) {
            status.text = "All permissions granted.\n\n" +
                "Pull down Quick Settings, tap Edit, and drag the Voice Assistant " +
                "tile into your active tiles. Tap it to give a command."
        } else {
            status.text = "Requesting permissions…"
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val allGranted = grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        // Recreate to re-check permission state and update the on-screen message.
        recreate()

        if (!allGranted) {
            // At least one permission was denied — the corresponding action
            // will fail with a Toast at execution time rather than crash.
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}