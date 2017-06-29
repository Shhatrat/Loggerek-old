package com.shhatrat.loggerek.fragments


import android.os.Bundle
import android.preference.SwitchPreference
import android.support.v4.app.Fragment
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.shhatrat.loggerek.R


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
        val mixGood  = findPreference("mix_good") as CheckBoxPreference
        val randomGood = findPreference("random_good") as CheckBoxPreference
        mixGood.setOnPreferenceChangeListener { preference, newValue -> run {
            randomGood.isChecked != newValue
            randomGood.isEnabled != newValue
            return@run true
        }}
        randomGood.setOnPreferenceChangeListener { preference, newValue -> run {
            mixGood.isChecked != newValue
            mixGood.isEnabled != newValue
            return@run true
        }}
    }

    fun prepare(): Boolean {
        val mixGood  = findPreference("mix_good") as CheckBoxPreference
        val randomGood = findPreference("random_good") as CheckBoxPreference
        mixGood.isEnabled = !randomGood.isChecked
        randomGood.isEnabled = !mixGood.isChecked
        return true
    }

    companion object{
        fun getInstance() : SettingsFragment {
            return SettingsFragment()
        }
    }
}
