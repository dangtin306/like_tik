package site.freelike.twa

import android.content.Context

object Storage {

    fun save(context: Context, key: String, value: String) {
        val pref = context.getSharedPreferences("APP_DATA", Context.MODE_PRIVATE)
        pref.edit().putString(key, value).apply()
    }

    fun get(context: Context, key: String, def: String = ""): String {
        val pref = context.getSharedPreferences("APP_DATA", Context.MODE_PRIVATE)
        return pref.getString(key, def) ?: def
    }
}
