package com.example.hallisanthe.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LanguageHelper {

    private const val PREF_NAME     = "language_pref"
    private const val KEY_LANGUAGE  = "selected_language"
    const val LANG_ENGLISH  = "en"
    const val LANG_KANNADA  = "kn"
    const val LANG_HINDI    = "hi"

    // ✅ Save selected language
    fun saveLanguage(context: Context, langCode: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    // ✅ Get saved language
    fun getSavedLanguage(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, LANG_ENGLISH) ?: LANG_ENGLISH
    }

    // ✅ Apply language to context
    fun applyLanguage(context: Context): Context {
        val langCode = getSavedLanguage(context)
        val locale   = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    // ✅ Set language and return new context
    fun setLanguage(context: Context, langCode: String): Context {
        saveLanguage(context, langCode)
        return applyLanguage(context)
    }
}