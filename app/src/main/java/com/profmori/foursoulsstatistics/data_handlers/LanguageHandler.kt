package com.profmori.foursoulsstatistics.data_handlers

import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate.getApplicationLocales
import androidx.appcompat.app.AppCompatDelegate.setApplicationLocales
import androidx.core.os.LocaleListCompat


class LanguageHandler {
    companion object {

        fun getLanguage(button: Button) {
            val currentLocale = getApplicationLocales().toLanguageTags()
            val currFlag = getFlag(currentLocale)
            button.text = currFlag
        }

        fun changeLanguage() {
            val locales = arrayOf("en", "es", "fr", "de", "it")
            locales.sort()
            val currentLocale = getApplicationLocales().toLanguageTags()
            val localeIndex = locales.indexOf(currentLocale)
            val nextIndex = (localeIndex + 1) % locales.size
            val nextLocale = locales[nextIndex]
            val newLocale = LocaleListCompat.forLanguageTags(nextLocale)
            setApplicationLocales(newLocale)
        }

        private fun getFlag(language: String): String {
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val country = when (language) {
                "en" -> "GB"
                "es" -> "ES"
                "fr" -> "FR"
                "de" -> "DE"
                "it" -> "IT"
                else -> "UN"
            }
            val firstChar: Int = Character.codePointAt(country, 0) - asciiOffset + flagOffset
            val secondChar: Int = Character.codePointAt(country, 1) - asciiOffset + flagOffset
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        }
    }
}