package it.piemonte.arpa.openoise;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogFilesListActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_log_files_list);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_log_files_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
