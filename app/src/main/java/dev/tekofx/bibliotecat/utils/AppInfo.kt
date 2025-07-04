package dev.tekofx.bibliotecat.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat


data class AppInfo(
    val versionName: String,
    val versionNumber: Long,
    val appName: String,
    val icon: Drawable
)

fun getAppInfo(
    context: Context,
): AppInfo? {

    return try {

        // Get Version
        val packageManager = context.packageManager
        val packageName = context.packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }

        // Get Name
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        val appName = if (stringId == 0) {
            applicationInfo.nonLocalizedLabel.toString()
        } else {
            context.getString(stringId)
        }

        println(appName)

        // Icon
        val icon: Drawable = packageManager.getApplicationIcon(applicationInfo)

        AppInfo(
            versionName = packageInfo.versionName,
            versionNumber = PackageInfoCompat.getLongVersionCode(packageInfo),
            appName = appName,
            icon = icon
        )
    } catch (e: Exception) {
        Log.e("AppInfo", e.toString())
        null
    }
}