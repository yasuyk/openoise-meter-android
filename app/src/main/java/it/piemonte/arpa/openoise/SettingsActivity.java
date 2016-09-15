package it.piemonte.arpa.openoise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String display_color = preferences.getString("display_color", "2");
        String display_orientation = preferences.getString("display_orientation", "1");

        // display color
        if (display_color.equals("1")){
            setTheme(R.style.AppThemeLight);
        } else if (display_color.equals("2")){
            setTheme(R.style.AppThemeDark);
        } else if (display_color.equals("3")){
            setTheme(R.style.AppThemeDarkHighContrast);
        }
        // display orientation
        if (display_orientation.equals("1")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }



        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    // parte per poter aggiornare main activity dopo cambiamenti nei setting (in realtà solo per parte di colore display!)
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onBackPressed();
    }

//    public static class SettingsFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
//            // Load the preferences from an XML resource
//            addPreferencesFromResource(R.xml.pref_general_advance_save);
//        }
//    }
    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String display_layout = preferences.getString("display_layout", "0");



            if (display_layout.equals("1")) {
                addPreferencesFromResource(R.xml.pref_general_simple);
            } else if (display_layout.equals("2")) {
                addPreferencesFromResource(it.piemonte.arpa.openoise.R.xml.pref_general_advance);
            } else if (display_layout.equals("3")) {
                addPreferencesFromResource(it.piemonte.arpa.openoise.R.xml.pref_general_advance_save);
            }
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


            if (Build.VERSION.SDK_INT < 20 ){
                ListPreference ListNotification = (ListPreference) findPreference("icon_notification");
                PreferenceCategory mCategory = (PreferenceCategory) findPreference("category_display");
                mCategory.removePreference(ListNotification);
            }



//            ListPreference a;
//            a = (ListPreference)findPreference("display_orientation");
////            a.setEnabled(false);
////            a.setTitle("ciao");
//            a.setDialogMessage("Non selezionabile in questa modalità");
////            a.setSelectable(false);
////            a.setShouldDisableView(true);

        }

//        public static final String key_time_display = "time_display";
//
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//                                              String key) {
//            if (key.equals(key_time_display)) {
//                Preference connectionPref = findPreference(key);
//                // Set summary to be the user-description for the selected value
//                //connectionPref.setSummary(sharedPreferences.getString(key, ""));
//                connectionPref.setSummary("ciaociao");
//            }
//        }


        // forse questa parte serve per aggiornare il summary
        @Override
        public void onResume() {
            super.onResume();
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        updatePreference(preferenceGroup.getPreference(j));
                    }
                } else {
                    updatePreference(preference);
                }
            }
        }


        // forse questa parte serve per aggiornare il summary
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key));

        }

        private void updatePreference(Preference preference) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
//                String new_Summary = listPreference.getEntry().toString();
//                CharSequence new_Summary_cs = new_Summary;
//                listPreference.setSummary(new_Summary_cs);
                listPreference.setSummary(listPreference.getEntry());
            }

            if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) preference;
                preference.setSummary(editTextPref.getText() + " dB");
            }
        }



    }

}
