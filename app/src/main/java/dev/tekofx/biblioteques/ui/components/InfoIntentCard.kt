package dev.tekofx.biblioteques.ui.components


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.tekofx.biblioteques.ui.IconResource
import dev.tekofx.biblioteques.ui.theme.Typography

enum class ContactType {
    MAIL,
    PHONE,
    WEB,
    LOCATION
}

@Composable
fun InfoIntentCard(contactType: ContactType, text: String) {
    val context = LocalContext.current


    val iconResource = remember(contactType) {
        when (contactType) {
            ContactType.MAIL -> IconResource.fromImageVector(Icons.Outlined.MailOutline)
            ContactType.PHONE -> IconResource.fromImageVector(Icons.Outlined.Phone)
            ContactType.WEB -> IconResource.fromImageVector(Icons.Outlined.Info)
            ContactType.LOCATION -> IconResource.fromImageVector(Icons.Outlined.LocationOn)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openApp(context, contactType, text) },
        tonalElevation = 20.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = iconResource.asPainterResource(),
                text,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
            Text(
                text = text,
                style = Typography.bodyLarge

            )
        }
    }
}

fun openApp(context: Context, contactType: ContactType, data: String) {

    val uri = when (contactType) {
        ContactType.PHONE -> Uri.parse("tel:${Uri.encode("+34$data")}")
        ContactType.MAIL -> Uri.parse("mailto:${Uri.encode(data)}")
        ContactType.WEB -> Uri.parse(Uri.encode(data))
        ContactType.LOCATION -> Uri.parse("geo:0,0?q=${Uri.encode(data)}")
    }

    val intent = Intent(
        Intent.ACTION_VIEW,
        uri
    )

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("LibraryContactCard", e.toString())
        Toast.makeText(context, "No app installed to manage $contactType", Toast.LENGTH_SHORT)
            .show()
    }

}