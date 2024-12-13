package dev.tekofx.biblioteques.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

enum class IntentType {
    MAIL,
    PHONE,
    WEB,
    LOCATION
}

fun openApp(context: Context, intentType: IntentType, data: String) {

    val uri = when (intentType) {
        IntentType.PHONE -> Uri.parse("tel:${Uri.encode("+34$data")}")
        IntentType.MAIL -> Uri.parse("mailto:${Uri.encode(data)}")
        IntentType.WEB -> Uri.parse(data)
        IntentType.LOCATION -> Uri.parse("geo:0,0?q=${Uri.encode(data)}")
    }

    val intent = Intent(
        Intent.ACTION_VIEW,
        uri
    )

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("Intents", e.toString())
        Toast.makeText(context, "No app installed to manage $intentType", Toast.LENGTH_SHORT)
            .show()
    }

}