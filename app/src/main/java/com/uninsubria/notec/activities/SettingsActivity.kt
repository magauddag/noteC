package com.uninsubria.notec.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.uninsubria.notec.R
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.myToolbarSettings

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_settings)

        setUpToolbar()

        val appSettingsPrefs : SharedPreferences = getSharedPreferences("ThemePref", 0)
        val editor: SharedPreferences.Editor = appSettingsPrefs.edit()
        val themeMode = appSettingsPrefs.getInt("themeValue", 2)

        val darkBtn = findViewById<MaterialButton>(R.id.darkBtn)
        val lightBtn = findViewById<MaterialButton>(R.id.lightBtn)
        val defaultBtn = findViewById<MaterialButton>(R.id.defaultBtn)

        when(themeMode) {
            0 -> {
                darkBtn.isChecked = true
                darkBtn.setStrokeColorResource(R.color.card_color)
                descriptionThemeTextView.text = getString(R.string.dark_description)
            }
            1 -> {
                lightBtn.isChecked = true
                lightBtn.setStrokeColorResource(R.color.card_color)
                descriptionThemeTextView.text = getString(R.string.light_description)
            }
            2 -> {
                defaultBtn.isChecked = true
                defaultBtn.setStrokeColorResource(R.color.card_color)
                descriptionThemeTextView.text = getString(R.string.default_description)
            }
        }

        themeTogglgeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->

            val theme: Int

            when(checkedId) {
                R.id.darkBtn -> {
                    descriptionThemeTextView.text = getString(R.string.dark_description)
                    darkBtn.setStrokeColorResource(R.color.card_color)
                    theme = AppCompatDelegate.MODE_NIGHT_YES
                    editor.putInt("themeValue", 0)
                    editor.commit()
                }
                R.id.lightBtn -> {
                    descriptionThemeTextView.text = getString(R.string.light_description)
                    lightBtn.setStrokeColorResource(R.color.card_color)
                    theme =  AppCompatDelegate.MODE_NIGHT_NO
                    editor.putInt("themeValue", 1)
                    editor.commit()
                }
                else -> {
                    descriptionThemeTextView.text = getString(R.string.default_description)
                    defaultBtn.setStrokeColorResource(R.color.card_color)
                    theme =  AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    editor.putInt("themeValue", 2)
                    editor.commit()
                }
            }

            AppCompatDelegate.setDefaultNightMode(theme)
        }
    }

    private fun setUpToolbar() {
        myToolbarSettings.title = getString(R.string.settings)
        setSupportActionBar(myToolbarSettings)

        myToolbarSettings.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}