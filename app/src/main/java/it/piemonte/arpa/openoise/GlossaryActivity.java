package it.piemonte.arpa.openoise;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GlossaryActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_glossary);
    }
}
