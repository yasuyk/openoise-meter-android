package it.piemonte.arpa.openoise;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;



public class MainActivity extends AppCompatActivity {

//    public static final String DISCLAIMER = "disclaimer";
//
    private NotificationManager mNotificationManager;
    private String NotificationText= "";
    private String NotificationSubText= "";
    private String icon_notification;

    private LinearLayout LayoutSimple;
    private LinearLayout LayoutAdvance;
    private LinearLayout LayoutLMax;
    private LinearLayout LayoutLAeqTimeDisplay;
    private LinearLayout LayoutLMin;
    private LinearLayout LayoutLAeqRunning;
    private LinearLayout LayoutLevel;
    private LinearLayout LayoutLog;
    private LinearLayout LayoutPlot;
    private LinearLayout LayoutLevelNoPlot;

    private TextView LayoutSimpleLine1;
    private TextView LayoutSimpleLine2;
    private TextView LayoutSimpleLevel;

    private TextView LAeqTimeDisplayLabel;
    private TextView LMax;
    private TextView LAeqTimeDisplay;
    private TextView LMin;
    private TextView level;
    private TextView levelLabel;
    private TextView LevelNoPlot;
    private TextView LevelNoPlotLabel;
    private TextView LAeqRunning;
    private TextView TimeDisplayLabel;
    private TextView startingTimeRunning;
    private TextView durationTimeRunning;
    private Button LayoutSimpleButtonRunning;
    private Button buttonRunning;
    private Button buttonLog;
    private TextView startingTimeLog;
    private TextView durationTimeLog;
    private TextView plotLabel;
    private PlotFFT plotFFT;
    private PlotSLM plotSLM;
    private PlotSLMHistory plotSLMHistory;
    private PlotThirdOctave plotThirdOctave;
//    private PlotSonogram plotSonogram;

    private String dialogDisclaimerTitle;
    private String dialogDisplayTitle;
    private CheckBox  checkbox_dialogDisplay1;
    private CheckBox  checkbox_dialogDisplay2;
    private CheckBox  checkbox_dialogDisplay3;
    //private String first_time;

    //private String disclaimerText;
    //private String disclaimerCheckboxText;
    private TextView disclaimerText;

    private String LMinText;
    private String LMaxText;
    private String LAeqTimeDisplayText;
    private String LAeqTimeDisplayTextFinal;
    private String TimeDisplayText;
    private String LAeqRunningText;
    private String startingTimeLogText;
    private String durationTimeLogText;
    private String buttonLogTextStart;
    private String buttonLogTextStop;

    private String days;
    private String start;
    private String duration;
    private String durationLogFile;

    private String spectrumLog;
    private String display_layout;
//    private String  last_layout;

    private FileOutputStream fos;
    private FileOutputStream fosC;

    private AudioRecord recorder;
//    // verifica gain
//    private AutomaticGainControl AGC;
//    private boolean  agcEnable0,agcEnable1,agcEnable2,agcEnable3,agcEnable4,agcEnable5,agcEnable6;


    //16 bit per campione in mono
    private final static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private final static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private final static int RECORDER_SAMPLERATE = 44100;
    private final static int BYTES_PER_ELEMENT = 2;
    private final static int BLOCK_SIZE = AudioRecord.getMinBufferSize(
            RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING)
            / BYTES_PER_ELEMENT;
    private final static int BLOCK_SIZE_FFT = 1764;
    private final static int NUMBER_OF_FFT_PER_SECOND = RECORDER_SAMPLERATE
            / BLOCK_SIZE_FFT;
    private final static double FREQRESOLUTION = ((double) RECORDER_SAMPLERATE)
            / BLOCK_SIZE_FFT;


    private Thread recordingThread = null;
    private boolean isRecording = false;

    private DoubleFFT_1D fft = null;

    private double filter = 0;

    private double[] weightedA = new double[BLOCK_SIZE_FFT];
    private double actualFreq;
    private float gain;



    // Terzi d'ottava
    private float [] THIRD_OCTAVE = {16, 20, 25, 31.5f, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500,
            630, 800, 1000, 1250, 1600, 2000, 2500, 3150, 4000, 5000, 6300, 8000, 10000, 12500, 16000, 20000};
    String [] THIRD_OCTAVE_LABEL = {"16", "20", "25", "31.5", "40", "50", "63", "80", "100", "125", "160", "200", "250", "315", "400", "500",
            "630", "800", "1000", "1250", "1600", "2000", "2500", "3150", "4000", "5000", "6300", "8000", "10000", "12500", "16000", "20000"};

    // check for level
    String levelToShow;

    // Running Leq
    double linearFftAGlobalRunning = 0;
    private long fftCount = 0;
    private double dbFftAGlobalRunning;
    private float[] dbBandRunning = new float[THIRD_OCTAVE.length];
    private float[] linearBandRunning = new float[THIRD_OCTAVE.length];

    // variabili finali per time display
    private double dbFftAGlobalMax;
    private double dbFftAGlobalMin;
    private double dbATimeDisplay;
    private float dbFftTimeDisplay[] = new float[BLOCK_SIZE_FFT / 2];
    private float dbFftATimeDisplay[] = new float[BLOCK_SIZE_FFT / 2];
    private float[] dbBandTimeDisplay = new float[THIRD_OCTAVE.length];
    private float[] linearBandTimeDisplay = new float[THIRD_OCTAVE.length];

    // SLM min e max
    double dbFftAGlobalMinTemp = 0;
    double dbFftAGlobalMaxTemp = 0;
    int dbFftAGlobalMinFirst = 0;
    int dbFftAGlobalMaxFirst = 0;

    private Date dateLogStart;

    float[] dbBandMax = new float[THIRD_OCTAVE.length];
    float[] dbBandMin = new float[THIRD_OCTAVE.length];
    int kkk = 0; // controllo per bontà leq bande: solo se kkk > 10 misurano bene

    // grafico SLMHIstory
    private float[] dbAHistoryTimeDisplay = new float[750];
    private float[] dbFftAGlobalRunningHistory = new float[750];

    // grafico SOnogram
//    private float[][] dbHistorySonogram = new float[750][THIRD_OCTAVE.length];


    private int timeLog;
    private String timeLogStringMinSec;
//    private int timeDisplay;
    private double timeDisplay;



    private void precalculateWeightedA() {
        for (int i = 0; i < BLOCK_SIZE_FFT; i++) {
            double actualFreq = FREQRESOLUTION * i;
            double actualFreqSQ = actualFreq * actualFreq;
            double actualFreqFour = actualFreqSQ * actualFreqSQ;
            double actualFreqEight = actualFreqFour * actualFreqFour;

            double t1 = 20.598997 * 20.598997 + actualFreqSQ;
            t1 = t1 * t1;
            double t2 = 107.65265 * 107.65265 + actualFreqSQ;
            double t3 = 737.86223 * 737.86223 + actualFreqSQ;
            double t4 = 12194.217 * 12194.217 + actualFreqSQ;
            t4 = t4 * t4;

            double weightFormula = (3.5041384e16 * actualFreqEight)
                    / (t1 * t2 * t3 * t4);

            weightedA[i] = weightFormula;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // lettura preference
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        display_layout = preferences.getString("display_layout", "0");

        String display_color = preferences.getString("display_color", "2");

        // parte notifiche
        // notification service per togliere la notifiche quando chiudo tutte le app da android
        if (Build.VERSION.SDK_INT > 20 ) {
            startService(new Intent(this, NotificationService.class));
        }

        // display color
        if (display_color.equals("1")){
            setTheme(R.style.AppThemeLight);
        } else if (display_color.equals("2")){
            setTheme(R.style.AppThemeDark);
        } else if (display_color.equals("3")){
            setTheme(R.style.AppThemeDarkHighContrast);
        }

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        plotSLMHistory = (PlotSLMHistory) findViewById(R.id.PlotSLMHistory);
        plotSLM = (PlotSLM) findViewById(R.id.PlotSLM);
        plotFFT = (PlotFFT) findViewById(R.id.PlotFFT);
        plotThirdOctave = (PlotThirdOctave) findViewById(R.id.PlotThirdOctave);
//        plotSonogram = (PlotSonogram) findViewById(R.id.PlotSonogram);

        LayoutSimple = (LinearLayout) findViewById(R.id.LayoutSimple);
        LayoutAdvance = (LinearLayout) findViewById(R.id.LayoutAdvance);
        LayoutLMax = (LinearLayout) findViewById(R.id.LayoutLMax);
        LayoutLAeqTimeDisplay = (LinearLayout) findViewById(R.id.LayoutLAeqTimeDisplay);
        LayoutLMin = (LinearLayout) findViewById(R.id.LayoutLMin);
        LayoutLAeqRunning = (LinearLayout) findViewById(R.id.LayoutLAeqRunning);
        LayoutLevel = (LinearLayout) findViewById(R.id.LayoutLevel);
        LayoutLog = (LinearLayout) findViewById(R.id.LayoutLog);
        LayoutPlot = (LinearLayout) findViewById(R.id.LayoutPlot);
        LayoutLevelNoPlot = (LinearLayout) findViewById(R.id.LayoutLevelNoPlot);

        LayoutSimpleLine1 = (TextView) findViewById(R.id.LayoutSimpleLine1);
        LayoutSimpleLine2 = (TextView) findViewById(R.id.LayoutSimpleLine2);
        LayoutSimpleLevel = (TextView) findViewById(R.id.LayoutSimpleLevel);

        LAeqTimeDisplayLabel = (TextView) findViewById(R.id.LAeqTimeDisplay_label);
        LMax = (TextView) findViewById(R.id.LMax);
        LAeqTimeDisplay = (TextView) findViewById(R.id.LAeqTimeDisplay);
        LMin = (TextView) findViewById(R.id.LMin);
        level = (TextView) findViewById(R.id.level);
        levelLabel = (TextView) findViewById(R.id.level_label);
        LevelNoPlot = (TextView) findViewById(R.id.LevelNoPlot);
        LevelNoPlotLabel = (TextView) findViewById(R.id.LevelNoPlotLabel);
        LAeqRunning = (TextView) findViewById(R.id.LAeqRunning);
        startingTimeRunning = (TextView) findViewById(R.id.StartingTimeRunning);
        durationTimeRunning = (TextView) findViewById(R.id.DurationTimeRunning);
        TimeDisplayLabel = (TextView) findViewById(R.id.time_display_label);
        LayoutSimpleButtonRunning = (Button) findViewById(R.id.LayoutSimpleButtonRunning);
        buttonRunning = (Button) findViewById(R.id.ButtonRunning);
        buttonLog = (Button) findViewById(R.id.ButtonLog);
        startingTimeLog = (TextView) findViewById(R.id.StartingTimeLog);
        durationTimeLog = (TextView) findViewById(R.id.DurationTimeLog);
        plotLabel = (TextView) findViewById(R.id.plot_label);



        LayoutSimpleButtonRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutSimpleLevel.setText(getResources().getString(R.string.LayoutSimpleLevel));
                fftCount = 0;
                linearFftAGlobalRunning = 0;
                dbFftAGlobalMin = 0;
                dbFftAGlobalMax = 0;
                dbFftAGlobalMinFirst = 0;
                dbFftAGlobalMaxFirst = 0;
                for (int i = 0; i < dbBandMin.length; i++){dbBandMin[i] = 0f;dbBandMax[i] = 0f;}
                kkk = 0;

                for (int i = 0; i < dbAHistoryTimeDisplay.length; i++) {
                    dbAHistoryTimeDisplay[i] = 0;
                    dbFftAGlobalRunningHistory[i] = 0;
                }
//                for (int i=0, j=0; i< dbHistorySonogram.length; i++,j++)
//                {
//                    dbHistorySonogram[i][j] = 0f;
//                }

                DateFormat df = new SimpleDateFormat("EEE yyyy/MM/dd HH:mm:ss");
                startingTimeRunning.setText(start + " " + String.format(df.format(new Date())));
                durationTimeRunning.setText(duration + " 0 " + days + " 00:00:00");
            }
        });


        buttonRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LAeqTimeDisplay.setText(getResources().getString(R.string.LAeqTimeDisplay));
                fftCount = 0;
                linearFftAGlobalRunning = 0;
                dbFftAGlobalRunning = 0;

                for (int ki = 0; ki < THIRD_OCTAVE.length; ki++) {
                    linearBandRunning[ki] = 0;
                }
                dbFftAGlobalMin = 0;
                dbFftAGlobalMax = 0;
                dbFftAGlobalMinFirst = 0;
                dbFftAGlobalMaxFirst = 0;
                for (int i = 0; i < dbBandMin.length; i++){dbBandMin[i] = 0f;dbBandMax[i] = 0f;}
                kkk = 0;

                for (int i = 0; i < dbAHistoryTimeDisplay.length; i++) {
                    dbAHistoryTimeDisplay[i] = 0;
                    dbFftAGlobalRunningHistory[i] = 0;
                }

                for (int i = 0; i < THIRD_OCTAVE.length; i++) {
                    dbBandTimeDisplay[i] =  0;
                    dbBandRunning[i] = 0;
                }
                for (int i = 0; i < dbFftTimeDisplay.length; i++) {
                    dbFftTimeDisplay[i] =  0;
                    dbFftATimeDisplay[i] =  0;
                }
//                for (int i=0, j=0; i< dbHistorySonogram.length; i++,j++)
//                {
//                    dbHistorySonogram[i][j] = 0f;
//                }

                DateFormat df = new SimpleDateFormat("EEE yyyy/MM/dd HH:mm:ss");
                startingTimeRunning.setText(start + " " + String.format(df.format(new Date())));
                durationTimeRunning.setText(duration + " 0 " + days + " 00:00:00");
            }
        });

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonLog.getText().toString().equals(buttonLogTextStart)) {
                    startRecordingLogFile();
                    //startRecordingLogParametersFile();

                    DateFormat df = new SimpleDateFormat("EEE yyyy/MM/dd HH:mm:ss");
                    dateLogStart =  new Date();
                    startingTimeLog.setText(start + " " + String.format(df.format(dateLogStart)));
                    buttonLog.setText(buttonLogTextStop);

                } else {
                    stopRecordingLogFile();
                    //stopRecordingLogParametersFile();
                    buttonLog.setText(buttonLogTextStart);
                    //startingTimeLog.setText("You are not logging");
                    startingTimeLog.setText(startingTimeLogText);
                    //durationTimeLog.setText("Logging interval: " + timeLogStringMinSec);
                    durationTimeLog.setText(durationTimeLogText + " " + timeLogStringMinSec);
                }
            }
        });

        plotSLMHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotSLMHistory.setVisibility(View.GONE);
//                plotSLM.setVisibility(View.VISIBLE);
//                plotLabel.setText(getApplicationContext().getResources().getString(R.string.plot_label_SLM));
                plotThirdOctave.setVisibility(View.VISIBLE);
                plotLabel.setText(getApplicationContext().getResources().getString(R.string.plot_label_ThirdOctave));
            }
        });

        plotThirdOctave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotThirdOctave.setVisibility(View.GONE);
                plotFFT.setVisibility(View.VISIBLE);
                plotLabel.setText(getApplicationContext().getResources().getString(R.string.plot_label_FFT));
            }
        });

//        plotSonogram.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                plotSonogram.setVisibility(View.GONE);
//                plotFFT.setVisibility(View.VISIBLE);
//                plotLabel.setText(getApplicationContext().getResources().getString(R.string.plot_label_FFT));
//            }
//        });

        plotFFT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plotFFT.setVisibility(View.GONE);
                plotSLMHistory.setVisibility(View.VISIBLE);
                plotLabel.setText(getApplicationContext().getResources().getString(R.string.plot_label_SLMHistory));
            }
        });


    //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        // DIALOGSSS
//        Toast.makeText(MainActivity.this, display_layout, Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this, first_time, Toast.LENGTH_SHORT).show();

//        if (!display_layout.equals("0") || first_time.equals("true")){
        if (display_layout.equals("0")){
            // DIALOG DISPLAY LAYOUT
            View dialog_display_layout = View.inflate(this, R.layout.dialog_display_layout, null);
            checkbox_dialogDisplay1 = (CheckBox) dialog_display_layout.findViewById(R.id.checkbox_dialogDisplay1);
            checkbox_dialogDisplay2 = (CheckBox) dialog_display_layout.findViewById(R.id.checkbox_dialogDisplay2);
            checkbox_dialogDisplay3 = (CheckBox) dialog_display_layout.findViewById(R.id.checkbox_dialogDisplay3);

            checkbox_dialogDisplay1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox_dialogDisplay1.setChecked(true);
                    checkbox_dialogDisplay2.setChecked(false);
                    checkbox_dialogDisplay3.setChecked(false);
                }
            });
            checkbox_dialogDisplay2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox_dialogDisplay1.setChecked(false);
                    checkbox_dialogDisplay2.setChecked(true);
                    checkbox_dialogDisplay3.setChecked(false);
                }
            });
            checkbox_dialogDisplay3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox_dialogDisplay1.setChecked(false);
                    checkbox_dialogDisplay2.setChecked(false);
                    checkbox_dialogDisplay3.setChecked(true);
                }
            });

            dialogDisplayTitle = getApplicationContext().getResources().getString(R.string.dialogDisplay_Title);
            if(display_layout.equalsIgnoreCase("0"))
            {new AlertDialog.Builder(this)

                    .setTitle(dialogDisplayTitle)
                    //.setMessage(disclaimerText)
                    .setView(dialog_display_layout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.cancel();
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putString("first_time","false");
//                            editor.apply();
                            if(checkbox_dialogDisplay1.isChecked()){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("display_layout","1");
                                editor.apply();
                            }
                            if(checkbox_dialogDisplay2.isChecked()){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("display_layout","2");
                                editor.apply();
                            }
                            if(checkbox_dialogDisplay3.isChecked()){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("display_layout","3");
                                editor.apply();
                            }
                            recreate();

                        }
                    })
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
            }

        } else {

            // DIALOG DISLAIMER
            View disclaimerCheckboxView = View.inflate(this, R.layout.dialog_disclaimer, null);
            CheckBox  disclaimerCheckbox = (CheckBox) disclaimerCheckboxView.findViewById(R.id.checkbox_disclaimer);
            disclaimerCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Save to shared preferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("disclaimer","1");
                    editor.apply();
                }
            });

            //disclaimerCheckbox.setText(disclaimerCheckboxText);
//            final String display_layout_internal = display_layout;
            final String dialog_disclaimer = preferences.getString("disclaimer", "0");

            dialogDisclaimerTitle = getApplicationContext().getResources().getString(R.string.dialogDisclaimer_Title);
            if(!dialog_disclaimer.equalsIgnoreCase("1"))
            {new AlertDialog.Builder(this)


                    .setTitle(dialogDisclaimerTitle)
                    //.setMessage(disclaimerText)
                    .setView(disclaimerCheckboxView)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.cancel();

                            if (display_layout.equals("-1")){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("display_layout","0");
//                                    editor.putString("first_time", "false");
                                editor.apply();
                                recreate();
                            }

                        }
                    })
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
            }
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_info:
                //openInfo();
                Intent intentInfoActivity = new Intent(this, InfoActivity.class);
                startActivity(intentInfoActivity);
                return true;

            case R.id.action_settings:
                //openSettings();
                Intent intentSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(intentSettingsActivity);
                return true;

            case R.id.action_log_files:
                //openLogFiles();
                Intent intentLogFilesListActivity = new Intent(this, LogFilesListActivity.class);
                startActivity(intentLogFilesListActivity);
                return true;

            case R.id.action_glossary:
                //openLogFiles();
                Intent intentGlossarytActivity = new Intent(this, GlossaryActivity.class);
                startActivity(intentGlossarytActivity);
                return true;

            case R.id.action_credits:
                //openCredits();
                Intent intentCreditsActivity = new Intent(this, CreditsActivity.class);
                startActivity(intentCreditsActivity);
                return true;

            case R.id.action_exit:
                //close app;
                this.finishAffinity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startRecording(final float gain, final int finalCountTimeDisplay, final int finalCountTimeLog) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
//                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
//                RECORDER_AUDIO_ENCODING, BLOCK_SIZE * BYTES_PER_ELEMENT);

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BLOCK_SIZE * BYTES_PER_ELEMENT);



        if (recorder.getState() == 1)
            Log.d("nostro log", "Il recorder è pronto");
        else
            Log.d("nostro log", "Il recorder non è pronto");

        recorder.startRecording();
        isRecording = true;



        // Creo una fft da BLOCK_SIZE_FFT punti --> BLOCK_SIZE_FFT / 2 bande utili,
        // ognuna da FREQRESOLUTION Hz
        fft = new DoubleFFT_1D(BLOCK_SIZE_FFT);

        recordingThread = new Thread(new Runnable() {
            public void run() {

                // Array di raw data (tot : BLOCK_SIZE_FFT * 2 bytes)
                short rawData[] = new short[BLOCK_SIZE_FFT];

                // Array di mag non pesati (BLOCK_SIZE_FFT / 2 perchè è il numero di
                // bande utili)
                final float dbFft[] = new float[BLOCK_SIZE_FFT / 2];

                // Array di mag pesati
                final float dbFftA[] = new float[BLOCK_SIZE_FFT / 2];

                float normalizedRawData;

                // La fft lavora con double e con numeri complessi (re + im in
                // sequenza)
                double[] audioDataForFFT = new double[BLOCK_SIZE_FFT * 2];

                // Soglia di udibilita (20*10^(-6))
                float amplitudeRef = 0.00002f;


                
                // terzi ottave
                final float[] dbBand = new float[THIRD_OCTAVE.length];

                final float[] linearBand = new float[THIRD_OCTAVE.length];
                final float[] linearBandCount = new float[THIRD_OCTAVE.length];
                int n = 3;
//                float summingLinearBand = 0f;
//                int controllo_frequenze = 0;
//                int controllo_frequenze_1 = 0;

                // Variabili per calcolo medie Time Display
                int indexTimeDisplay = 1;
                double linearATimeDisplay = 0;


                // Variabili per calcolo medie Time Log
                int indexTimeLog = 0;
                double linearTimeLog = 0;
                double linearATimeLog = 0;
                final float[] linearBandTimeLog = new float[THIRD_OCTAVE.length];

                final float linearFftTimeDisplay[] = new float[BLOCK_SIZE_FFT / 2];
                final float linearFftATimeDisplay[] = new float[BLOCK_SIZE_FFT / 2];

                int initial_delay = 0;


                while (isRecording) {

                    // Leggo i dati
                    recorder.read(rawData, 0, BLOCK_SIZE_FFT);

                    // inserito un delay iniziale perché all'attivazione si avevano livelli molto alti di running leq (>100 dB) e minimi bassi (10 dB) dovuti forse all'attivazione inizale della periferica

                    initial_delay++;

                    if (initial_delay > 20) {

                        for (int i = 0, j = 0; i < BLOCK_SIZE_FFT; i++, j += 2) {

                            // Range [-1,1]
                            normalizedRawData = (float) rawData[i]
                                    / (float) Short.MAX_VALUE;

                            // filter = ((double) (fastA * normalizedRawData))
                            // + (fastB * filter);
                            filter = normalizedRawData;

                            // Finestra di Hannings
                            double x = (2 * Math.PI * i) / (BLOCK_SIZE_FFT - 1);
                            double winValue = (1 - Math.cos(x)) * 0.5d;

                            // Parte reale
                            audioDataForFFT[j] = filter * winValue;

                            // Parte immaginaria
                            audioDataForFFT[j + 1] = 0.0;
                        }

                        // FFT
                        fft.complexForward(audioDataForFFT);

                        // Magsum non pesati
                        double linearFftGlobal = 0;

                        // Magsum pesati
                        double linearFftAGlobal = 0;

                        // indice per terzi ottava
                        int k = 0;

                        for (int ki = 0; ki < THIRD_OCTAVE.length; ki++) {
                            linearBandCount[ki] = 0;
                            linearBand[ki] = 0;
                            dbBand[ki] = 0;
                        }

                        // Leggo fino a BLOCK_SIZE_FFT/2 perchè in tot ho BLOCK_SIZE_FFT/2
                        // bande utili
                        for (int i = 0, j = 0; i < BLOCK_SIZE_FFT / 2; i++, j += 2) {

                            double re = audioDataForFFT[j];
                            double im = audioDataForFFT[j + 1];

                            // Magnitudo
                            double mag = Math.sqrt((re * re) + (im * im));

                            // Ponderata A
                            // da capire: per i = 0 viene un valore non valido (forse meno infinito), ma ha senso?
                            // questo si ritrova poi nel grafico:
                            // per i=0 la non pesata ha un valore, mentre la pesata non ce l'ha...
                            double weightFormula = weightedA[i];

                            dbFft[i] = (float) (10 * Math.log10(mag * mag
                                    / amplitudeRef))
                                    + (float) gain;
                            dbFftA[i] = (float) (10 * Math.log10(mag * mag
                                    * weightFormula
                                    / amplitudeRef))
                                    + (float) gain;

                            linearFftGlobal += Math.pow(10, (float) dbFft[i] / 10f);
                            linearFftAGlobal += Math.pow(10, (float) dbFftA[i] / 10f);

                            float linearFft = (float) Math.pow(10, (float) dbFft[i] / 10f);


                            if ((0 <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 17.8f)) {
                                linearBandCount[0] += 1;
                                linearBand[0] += linearFft;
                                dbBand[0] = (float) (10 * Math.log10(linearBand[0]));
                            }
                            if ((17.8f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 22.4f)) {
                                linearBandCount[1] += 1;
                                linearBand[1] += linearFft;
                                dbBand[1] = (float) (10 * Math.log10(linearBand[1]));
                            }
                            if ((22.4f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 28.2f)) {
                                linearBandCount[2] += 1;
                                linearBand[2] += linearFft;
                                dbBand[2] = (float) (10 * Math.log10(linearBand[2]));
                            }
                            if ((28.2f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 35.5f)) {
                                linearBandCount[3] += 1;
                                linearBand[3] += linearFft;
                                dbBand[3] = (float) (10 * Math.log10(linearBand[3]));
                            }
                            if ((35.5f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 44.7f)) {
                                linearBandCount[4] += 1;
                                linearBand[4] += linearFft;
                                dbBand[4] = (float) (10 * Math.log10(linearBand[4]));
                            }
                            if ((44.7f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 56.2f)) {
                                linearBandCount[5] += 1;
                                linearBand[5] += linearFft;
                                dbBand[5] = (float) (10 * Math.log10(linearBand[5]));
                            }
                            if ((56.2f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 70.8f)) {
                                linearBandCount[6] += 1;
                                linearBand[6] += linearFft;
                                dbBand[6] = (float) (10 * Math.log10(linearBand[6]));
                            }
                            if ((70.8f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 89.1f)) {
                                linearBandCount[7] += 1;
                                linearBand[7] += linearFft;
                                dbBand[7] = (float) (10 * Math.log10(linearBand[7]));
                            }
                            if ((89.1f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 112f)) {
                                linearBandCount[8] += 1;
                                linearBand[8] += linearFft;
                                dbBand[8] = (float) (10 * Math.log10(linearBand[8]));
                            }
                            if ((112f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 141f)) {
                                linearBandCount[9] += 1;
                                linearBand[9] += linearFft;
                                dbBand[9] = (float) (10 * Math.log10(linearBand[9]));
                            }
                            if ((141f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 178f)) {
                                linearBandCount[10] += 1;
                                linearBand[10] += linearFft;
                                dbBand[10] = (float) (10 * Math.log10(linearBand[10]));
                            }
                            if ((178f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 224f)) {
                                linearBandCount[11] += 1;
                                linearBand[11] += linearFft;
                                dbBand[11] = (float) (10 * Math.log10(linearBand[11]));
                            }
                            if ((224f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 282f)) {
                                linearBandCount[12] += 1;
                                linearBand[12] += linearFft;
                                dbBand[12] = (float) (10 * Math.log10(linearBand[12]));
                            }
                            if ((282f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 355f)) {
                                linearBandCount[13] += 1;
                                linearBand[13] += linearFft;
                                dbBand[13] = (float) (10 * Math.log10(linearBand[13]));
                            }
                            if ((355f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 447f)) {
                                linearBandCount[14] += 1;
                                linearBand[14] += linearFft;
                                dbBand[14] = (float) (10 * Math.log10(linearBand[14]));
                            }
                            if ((447f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 562f)) {
                                linearBandCount[15] += 1;
                                linearBand[15] += linearFft;
                                dbBand[15] = (float) (10 * Math.log10(linearBand[15]));
                            }
                            if ((562f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 708f)) {
                                linearBandCount[16] += 1;
                                linearBand[16] += linearFft;
                                dbBand[16] = (float) (10 * Math.log10(linearBand[16]));
                            }
                            if ((708f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 891f)) {
                                linearBandCount[17] += 1;
                                linearBand[17] += linearFft;
                                dbBand[17] = (float) (10 * Math.log10(linearBand[17]));
                            }
                            if ((891f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 1122f)) {
                                linearBandCount[18] += 1;
                                linearBand[18] += linearFft;
                                dbBand[18] = (float) (10 * Math.log10(linearBand[18]));
                            }
                            if ((1122f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 1413f)) {
                                linearBandCount[19] += 1;
                                linearBand[19] += linearFft;
                                dbBand[19] = (float) (10 * Math.log10(linearBand[19]));
                            }
                            if ((1413f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 1778f)) {
                                linearBandCount[20] += 1;
                                linearBand[20] += linearFft;
                                dbBand[20] = (float) (10 * Math.log10(linearBand[20]));
                            }
                            if ((1778f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 2239f)) {
                                linearBandCount[21] += 1;
                                linearBand[21] += linearFft;
                                dbBand[21] = (float) (10 * Math.log10(linearBand[21]));
                            }
                            if ((2239f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 2818f)) {
                                linearBandCount[22] += 1;
                                linearBand[22] += linearFft;
                                dbBand[22] = (float) (10 * Math.log10(linearBand[22]));
                            }
                            if ((2818f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 3548f)) {
                                linearBandCount[23] += 1;
                                linearBand[23] += linearFft;
                                dbBand[23] = (float) (10 * Math.log10(linearBand[23]));
                            }
                            if ((3548f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 4467f)) {
                                linearBandCount[24] += 1;
                                linearBand[24] += linearFft;
                                dbBand[24] = (float) (10 * Math.log10(linearBand[24]));
                            }
                            if ((4467f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 5623f)) {
                                linearBandCount[25] += 1;
                                linearBand[25] += linearFft;
                                dbBand[25] = (float) (10 * Math.log10(linearBand[25]));
                            }
                            if ((5623f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 7079f)) {
                                linearBandCount[26] += 1;
                                linearBand[26] += linearFft;
                                dbBand[26] = (float) (10 * Math.log10(linearBand[26]));
                            }
                            if ((7079f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 8913f)) {
                                linearBandCount[27] += 1;
                                linearBand[27] += linearFft;
                                dbBand[27] = (float) (10 * Math.log10(linearBand[27]));
                            }
                            if ((8913f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 11220f)) {
                                linearBandCount[28] += 1;
                                linearBand[28] += linearFft;
                                dbBand[28] = (float) (10 * Math.log10(linearBand[28]));
                            }
                            if ((11220f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 14130f)) {
                                linearBandCount[29] += 1;
                                linearBand[29] += linearFft;
                                dbBand[29] = (float) (10 * Math.log10(linearBand[29]));
                            }
                            if ((14130f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 17780f)) {
                                linearBandCount[30] += 1;
                                linearBand[30] += linearFft;
                                dbBand[30] = (float) (10 * Math.log10(linearBand[30]));
                            }
                            if ((17780f <= i * FREQRESOLUTION) && (i * FREQRESOLUTION < 22390f)) {
                                linearBandCount[31] += 1;
                                linearBand[31] += linearFft;
                                dbBand[31] = (float) (10 * Math.log10(linearBand[31]));
                            }

                        }


                        final double dbFftAGlobal = 10 * Math.log10(linearFftAGlobal);

                        // calcolo min e max valore globale FFT pesato A
                        if (dbFftAGlobal > 0) {
                            if (dbFftAGlobalMinFirst == 0) {
                                dbFftAGlobalMinTemp = dbFftAGlobal;
                                dbFftAGlobalMinFirst = 1;
                            } else {
                                if (dbFftAGlobalMinTemp > dbFftAGlobal) {
                                    dbFftAGlobalMinTemp = dbFftAGlobal;
                                }
                            }
                            if (dbFftAGlobalMaxFirst == 0) {
                                dbFftAGlobalMaxTemp = dbFftAGlobal;
                                dbFftAGlobalMaxFirst = 1;
                            } else {
                                if (dbFftAGlobalMaxTemp < dbFftAGlobal) {
                                    dbFftAGlobalMaxTemp = dbFftAGlobal;
                                }
                            }
                        }
                        dbFftAGlobalMin = dbFftAGlobalMinTemp;
                        dbFftAGlobalMax = dbFftAGlobalMaxTemp;


                        // Running Leq
                        fftCount++;
                        linearFftAGlobalRunning += linearFftAGlobal;
                        dbFftAGlobalRunning = 10 * Math.log10(linearFftAGlobalRunning / fftCount);

                        for (int ki = 0; ki < THIRD_OCTAVE.length; ki++) {
                            linearBandRunning[ki] += linearBand[ki];
                            dbBandRunning[ki] = 10 * (float) Math.log10(linearBandRunning[ki] / fftCount);
                        }

                        final int TimeRunning = (int) fftCount / NUMBER_OF_FFT_PER_SECOND;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LMax.setText(dBformat(dbFftAGlobalMax));
                                LAeqRunning.setText(dBformat(dbFftAGlobalRunning));
                                durationTimeRunning.setText(duration + " " + String.format("%d " + days + " %02d:%02d:%02d", TimeRunning / (3600 * 24), (TimeRunning % (3600 * 24) / 3600), (TimeRunning % 3600) / 60, (TimeRunning % 60)));
                                LMin.setText(dBformat(dbFftAGlobalMin));
                                if (levelToShow == "dbFftAGlobalMin") {
                                    level.setText(dBformat(dbFftAGlobalMin));
                                    LevelNoPlot.setText(level.getText());
                                }
                                if (levelToShow == "dbFftAGlobalMax") {
                                    level.setText(dBformat(dbFftAGlobalMax));
                                    LevelNoPlot.setText(level.getText());
                                }
                                if (levelToShow == "dbFftAGlobalRunning") {
                                    level.setText(dBformat(dbFftAGlobalRunning));
                                    LevelNoPlot.setText(level.getText());
                                }
                                if (buttonLog.getText().toString().equals(buttonLogTextStop)) {
                                    int diffInSec = (int) (new Date().getTime() - dateLogStart.getTime()) / 1000;
                                    durationTimeLog.setText(durationLogFile + " " + String.format("%d " + days + " %02d:%02d:%02d", diffInSec / (3600 * 24), (diffInSec % (3600 * 24) / 3600), (diffInSec % 3600) / 60, (diffInSec % 60)) + " (" + timeLogStringMinSec + ")");
                                    //durationTimeLog.setText(String.format("%d days %02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(diffInMilliSec), TimeUnit.MILLISECONDS.toHours(diffInMilliSec), TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec), TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec)));
                                }


                            }
                        });

                        // calcolo min e max per dbBand non pesato
                        // definisco minimi e massimi per le bande
                        for (int kk = 0; kk < dbBand.length; kk++) {
                            if (dbBandMax[kk] < dbBand[kk]) {
                                dbBandMax[kk] = dbBand[kk];
                            }
                            if (kkk >= 10) { // controllo per bontà leq bande: solo se kkk > 10 misurano bene
                                if (dbBandMin[kk] == 0f) {
                                    if (dbBand[kk] > 0) {
                                        dbBandMin[kk] = dbBand[kk];
                                    }
                                } else if (dbBandMin[kk] > dbBand[kk]) {
                                    dbBandMin[kk] = dbBand[kk];
                                }
                            }
                        }
                        kkk++;



                        // SLM plot
                        if (plotSLM.getVisibility() == View.VISIBLE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    plotSLM.setDataPlot((float) dbFftAGlobal, (float) dbFftAGlobalMin, (float) dbFftAGlobalMax);
                                }
                            });
                        }

                        // ThirdOctave plot
                        if (plotThirdOctave.getVisibility() == View.VISIBLE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    plotThirdOctave.setDataPlot(dbBand, dbBandMin, dbBandMax,dbBandRunning,(float) dbFftAGlobal, (float) dbFftAGlobalMin, (float) dbFftAGlobalMax, (float) dbFftAGlobalRunning);
                                    plotThirdOctave.setDataPlot(dbBandTimeDisplay, dbBandMin, dbBandMax,dbBandRunning,(float) dbATimeDisplay, (float) dbFftAGlobalMin, (float) dbFftAGlobalMax, (float) dbFftAGlobalRunning);
                                }
                            });
                        }


                        // LAeqTimeDisplay
                        // Calcolo Medie per Time Display e aggiorno i grafici
                        linearATimeDisplay += linearFftAGlobal;
                        for (int i = 0; i < THIRD_OCTAVE.length; i++) {
                            linearBandTimeDisplay[i] += linearBand[i];
                        }

                        for (int i = 0; i < dbFftTimeDisplay.length; i++) {
                            linearFftTimeDisplay[i] +=  Math.pow(10, (float) dbFft[i] / 10f);
                            linearFftATimeDisplay[i] +=  Math.pow(10, (float) dbFftA[i] / 10f);
                        }

                        if (indexTimeDisplay < finalCountTimeDisplay) {
                            indexTimeDisplay++;
                        } else {
                            // aggiorno dati per plot terzi di ottava
                            for (int i = 0; i < THIRD_OCTAVE.length; i++) {
                                dbBandTimeDisplay[i] =  10 *  (float) Math.log10(linearBandTimeDisplay[i] / finalCountTimeDisplay);
                                linearBandTimeDisplay[i] = 0;
                            }

                            // FFT plot
                            for (int i = 0; i < dbFftTimeDisplay.length; i++) {
                                dbFftTimeDisplay[i] =  10 *  (float) Math.log10(linearFftTimeDisplay[i] / finalCountTimeDisplay);
                                dbFftATimeDisplay[i] =  10 *  (float) Math.log10(linearFftATimeDisplay[i] / finalCountTimeDisplay);
                                linearFftTimeDisplay[i] = 0;
                                linearFftATimeDisplay[i] = 0;
                            }
                            if (plotFFT.getVisibility() == View.VISIBLE) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        plotFFT.setDataPlot(BLOCK_SIZE_FFT, FREQRESOLUTION, dbFftTimeDisplay, dbFftATimeDisplay);
                                    }
                                });
                            }

                            // dati timeDisplay e icona notifiche
                            dbATimeDisplay = 10 * Math.log10(linearATimeDisplay / finalCountTimeDisplay);
                            indexTimeDisplay = 1;
                            linearATimeDisplay = 0;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LAeqTimeDisplay.setText(dBformat(dbATimeDisplay));
                                    LayoutSimpleLevel.setText(dBformat(dbATimeDisplay));
                                    if (levelToShow == "dbATimeDisplay") {
                                        level.setText(dBformat(dbATimeDisplay));
                                        LevelNoPlot.setText(level.getText());
                                    }

                                    //////////////////////////////////
                                    // notification part
                                    if (Build.VERSION.SDK_INT > 20 ) {
                                        if (icon_notification.equals("1")) {
                                            String running_label = getApplicationContext().getResources().getString(R.string.LAeqRunning_label);
                                            if (display_layout.equals("1")) {
                                                NotificationText = LayoutSimpleLine1.getText() + ": " + dBformat(dbATimeDisplay) + " dB(A)";
                                                NotificationSubText = "";
                                            } else if (display_layout.equals("2")) {
                                                NotificationText = LAeqTimeDisplayLabel.getText() + ": " + dBformat(dbATimeDisplay) + " dB(A) - " + running_label + ": " + dBformat(dbFftAGlobalRunning) + " dB(A)";
                                                NotificationSubText = "";
                                            } else if (display_layout.equals("3")) {
                                                NotificationText = LAeqTimeDisplayLabel.getText() + ": " + dBformat(dbATimeDisplay) + " dB(A) - " + running_label + ": " + dBformat(dbFftAGlobalRunning) + " dB(A)";
                                                if (buttonLog.getText().toString().equals(buttonLogTextStop)) {
                                                    NotificationSubText = getApplicationContext().getResources().getString(R.string.SavingText);
                                                } else {
                                                    NotificationSubText = getApplicationContext().getResources().getString(R.string.StartingTimeLogText);
                                                }
                                            }


                                            NotificationCompat.Builder mBuilder =
                                                    new NotificationCompat.Builder(MainActivity.this)
                                                            .setContentTitle("OpeNoise")
                                                            .setContentText(NotificationText)
                                                            .setSubText(NotificationSubText)
                                                            .setAutoCancel(true)
                                                            .setSmallIcon(R.drawable.ic_notification);

                                            // Creates an explicit intent for an Activity in your app
                                            //        Intent resultIntent = new Intent(this, MainActivity.class);
                                            //
                                            //        // The stack builder object will contain an artificial back stack for the
                                            //        // started Activity.
                                            //        // This ensures that navigating backward from the Activity leads out of
                                            //        // your application to the Home screen.
                                            //        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                                            //        // Adds the back stack for the Intent (but not the Intent itself)
                                            //        stackBuilder.addParentStack(MainActivity.class);
                                            //
                                            //        // Adds the Intent that starts the Activity to the top of the stack
                                            //        stackBuilder.addNextIntent(resultIntent);
                                            //        PendingIntent resultPendingIntent =
                                            //                stackBuilder.getPendingIntent(
                                            //                        0,
                                            //                        PendingIntent.FLAG_UPDATE_CURRENT
                                            //                );
                                            //        mBuilder.setContentIntent(resultPendingIntent);

                                            // mId allows you to update the notification later on.

                                            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            mNotificationManager.notify(1, mBuilder.build());

                                        } else if (icon_notification.equals("0")) {
                                            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            mNotificationManager.cancelAll();
                                        }
                                    }
                                    //////////////////////////////////
                                    // end notification part

                                }
                            });
                        }

                        // trucco per non lasciare dbATimeDisplay nullo all'apertura dell'app
                        if (dbATimeDisplay == 0){
                            dbATimeDisplay = dbFftAGlobalRunning;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LAeqTimeDisplay.setText(dBformat(dbATimeDisplay));
                                    if (levelToShow == "dbATimeDisplay") {
                                        level.setText(dBformat(dbATimeDisplay));
                                        LevelNoPlot.setText(level.getText());
                                    }
                                }
                            });
                        }


                        // Plot SLM History
                        // 750 = 30 (sec) / 0.04
                        for (int i = 1; i < dbAHistoryTimeDisplay.length; i++) {
                            dbAHistoryTimeDisplay[i - 1] = dbAHistoryTimeDisplay[i];
                            dbFftAGlobalRunningHistory[i - 1] = dbFftAGlobalRunningHistory[i];
                        }
                        dbAHistoryTimeDisplay[dbAHistoryTimeDisplay.length - 1] = (float) dbATimeDisplay;
                        dbFftAGlobalRunningHistory[dbAHistoryTimeDisplay.length - 1] = (float) dbFftAGlobalRunning;

                        if (plotSLMHistory.getVisibility() == View.VISIBLE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    plotSLMHistory.setDataPlot(dbAHistoryTimeDisplay, dbFftAGlobalRunningHistory, dbAHistoryTimeDisplay.length,timeDisplay);
                                }
                            });
                        }

                        ;


                        // Plot Sonogram dbHistorySonogram
//                        for (int i=1; i < dbHistorySonogram.length; i++) {
//                            for (int j=0; j < dbHistorySonogram[0].length; j++) {
//                                dbHistorySonogram[i-1][j] = dbHistorySonogram[i][j];
//
//                                if (i == dbHistorySonogram.length - 1) {
//                                    dbHistorySonogram[dbHistorySonogram.length - 1][j] = dbBand[j];
//                                }
//                            }
//                        }

//                        if (plotSonogram.getVisibility() == View.VISIBLE) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    plotSonogram.setDataPlot(dbBand);
//                                }
//                            });
//                        }



                        // Scrittura log file
                        // Calcolo medie per Time Log
                        linearTimeLog += linearFftGlobal;
                        linearATimeLog += linearFftAGlobal;
                        for (int i = 0; i < THIRD_OCTAVE.length; i++) {
                            linearBandTimeLog[i] += linearBand[i];
                        }
                        if (indexTimeLog < finalCountTimeLog) {
                            indexTimeLog++;
                        } else {
                            final double dbTimeLog = 10 * Math.log10(linearTimeLog / finalCountTimeLog);
                            final double dbATimeLog = 10 * Math.log10(linearATimeLog / finalCountTimeLog);

                            final double[] dbBandTimeLog = new double[THIRD_OCTAVE.length];
                            for (int i = 0; i < THIRD_OCTAVE.length; i++) {
                                dbBandTimeLog[i] = 10 * Math.log10(linearBandTimeLog[i] / finalCountTimeLog);
                                linearBandTimeLog[i] = 0;
                            }
                            // parte per bande senza valori
                            dbBandTimeLog[1] = dbBandTimeLog[0];
                            dbBandTimeLog[3] = dbBandTimeLog[2];
                            dbBandTimeLog[4] = dbBandTimeLog[5];
                            dbBandTimeLog[6] = dbBandTimeLog[7];

                            indexTimeLog = 1;
                            linearTimeLog = 0;
                            linearATimeLog = 0;

                            // Scrivo sul file
                            if (buttonLog.getText().toString().equals(buttonLogTextStop)) {
                                DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                String strDate = sdf.format(new Date());
                                try {
//                                    fos.write(String.format("%s\t%.1f\t%.1f", strDate, dbTimeLog, dbATimeLog).getBytes());
                                    fos.write((strDate + "\t" + dBformat(dbATimeLog)).getBytes());
                                    if (spectrumLog.equals("1")) {
                                        for (int i = 0; i < THIRD_OCTAVE.length; i++) {
                                            fos.write(("\t" + dBformat(dbBandTimeLog[i])).getBytes());
                                        }
                                    }
                                    fos.write(("\n").getBytes());

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
                    }
                } // while
            }
        }, "AudioRecorder Thread");
        recordingThread.start();

    }

    private void stopRecording() {
        // stops the recording activity
        if (recorder != null) {
            isRecording = false;
            try {
                recordingThread.join();
                //fos.close();
            } catch (Exception e) {
                Log.d("nostro log",
                        "Il Thread principale non può attendere la chiusura del thread secondario dell'audio");
            }
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }


    private void startRecordingLogFile() {
        // start the recording log file
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
        String filename = String.format("%s.OpeNoise.txt", df.format(new Date()));
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "openoise");

        if (!path.exists()) {
            Log.d("mio", "il path non esiste. Creato? : " + path.mkdirs());
        }
        try {
            File file = new File(path, filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(("DataTime                     \tdB(A)").getBytes());
            if (spectrumLog.equals("1")) {
                for (int i = 0; i < THIRD_OCTAVE_LABEL.length; i++) {
                    fos.write(("\t" + THIRD_OCTAVE_LABEL[i]).getBytes());
                }
            }
            fos.write(("\n").getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void stopRecordingLogFile() {
        // stop the recording log file
        try {
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopRecording();
        if (Build.VERSION.SDK_INT > 20 ) {
            mNotificationManager.cancelAll();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopRecording();
        finish();
        if (Build.VERSION.SDK_INT > 20 ) {
            mNotificationManager.cancelAll();
        }
        super.onBackPressed();
    }


    @Override
    public void onResume() {
        super.onResume();
        stopRecording();
        // read the parameter in the settings

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gainString = preferences.getString("gain", "0");
        String timeLogString = preferences.getString("timeLog", "1");
        String timeDisplayString = preferences.getString("timeDisplay", "1");

        icon_notification = preferences.getString("icon_notification", "0");
        display_layout = preferences.getString("display_layout", "1");
        String display_orientation = preferences.getString("display_orientation", "1");
        String priority_level = preferences.getString("priority_level", "1");
        String plot_visualisation = preferences.getString("plot_visualisation", "1");
        spectrumLog = preferences.getString("spectrumLog", "0");


        // notifica
        if (Build.VERSION.SDK_INT > 20 ) {
            if (icon_notification.equals("0")) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
            }
        }
        /////// parte per i calcoli
        try {
            gain = Float.parseFloat(gainString);
        } catch (Exception e) {
            gain = 0.0f;
        }
        try {
//            timeDisplay = Integer.parseInt(timeDisplayString);
            timeDisplay = Double.parseDouble(timeDisplayString);
        } catch (Exception e) {
            timeDisplay = 1;
        }
        try {
            timeLog = Integer.parseInt(timeLogString);
        } catch (Exception e) {
            timeLog = 1;
        }

        final int finalCountTimeDisplay = (int) (timeDisplay * NUMBER_OF_FFT_PER_SECOND);
        final int finalCountTimeLog = (int) (timeLog * NUMBER_OF_FFT_PER_SECOND);
        //////////////////////////

        // display orientation
        if (display_orientation.equals("1")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }

        LMinText =  getApplicationContext().getResources().getString(R.string.LMin_label);
        LMaxText =  getApplicationContext().getResources().getString(R.string.LMax_label);
        LAeqTimeDisplayText =  getApplicationContext().getResources().getString(R.string.LAeqTimeDisplay_label);
        LAeqRunningText =  getApplicationContext().getResources().getString(R.string.LAeqRunning_label);

        days =  getApplicationContext().getResources().getString(R.string.Days);
        start =  getApplicationContext().getResources().getString(R.string.Start);
        duration =  getApplicationContext().getResources().getString(R.string.Duration);
        durationLogFile =  getApplicationContext().getResources().getString(R.string.DurationLogFile);

        DateFormat df = new SimpleDateFormat("EEE yyyy/MM/dd HH:mm:ss");
        startingTimeRunning.setText(start + " " + String.format(df.format(new Date())));
        durationTimeRunning.setText(duration + " 0 " + days + " 00:00:00");

        startingTimeLogText =  getApplicationContext().getResources().getString(R.string.StartingTimeLogText);
        durationTimeLogText =  getApplicationContext().getResources().getString(R.string.DurationTimeLogText);

        //startingTimeLog.setText("You are not logging");
//        startingTimeLog.setText(startingTimeLogText);
//        durationTimeLog.setText("");

        //buttonLog.setText("START LOG");
        buttonLogTextStart =  getApplicationContext().getResources().getString(R.string.ButtonLogTextStart);
        buttonLogTextStop =  getApplicationContext().getResources().getString(R.string.ButtonLogTextStop);
//        buttonLog.setText(buttonLogTextStart);


        level.setText("      ");

        // display layout
        if (display_layout.equals("0") || display_layout.equals("1")){
            LayoutSimple.setVisibility(View.VISIBLE);
            LayoutAdvance.setVisibility(View.GONE);
        } else if (display_layout.equals("2")) {
            LayoutSimple.setVisibility(View.GONE);
            LayoutAdvance.setVisibility(View.VISIBLE);
            LayoutLog.setVisibility(View.GONE);
        } else if (display_layout.equals("3")){
            LayoutSimple.setVisibility(View.GONE);
            LayoutAdvance.setVisibility(View.VISIBLE);
            LayoutLog.setVisibility(View.VISIBLE);
        }

        String TimeDisplayText = "";
        if  (timeDisplay == 0.5) {
            TimeDisplayText = dBformat(timeDisplay) + " s";
        } else if (timeDisplay == 1) {
            TimeDisplayText = "1 s";
        } else if (timeDisplay == 2) {
            TimeDisplayText = "2 s";
        }

        String LayoutSimpleLine2text = getResources().getString(R.string.LayoutSimpleLine2) + " " + TimeDisplayText;
        LayoutSimpleLine2.setText(LayoutSimpleLine2text);

        TimeDisplayLabel.setText("(" + TimeDisplayText + ")");
        LAeqTimeDisplayLabel.setText(LAeqTimeDisplayText  + TimeDisplayLabel.getText());

        if (priority_level.equals("1")){
            levelLabel.setText(LAeqTimeDisplayText);
            levelToShow = "dbATimeDisplay";
            LayoutLAeqTimeDisplay.setVisibility(View.GONE);
            TimeDisplayLabel.setVisibility(View.VISIBLE);
            LayoutLAeqRunning.setVisibility(View.VISIBLE);
            LevelNoPlotLabel.setText(LAeqTimeDisplayLabel.getText());
        } else {
            levelLabel.setText(LAeqRunningText);
            levelToShow = "dbFftAGlobalRunning";
            LayoutLAeqTimeDisplay.setVisibility(View.VISIBLE);
            TimeDisplayLabel.setVisibility(View.GONE);
            LayoutLAeqRunning.setVisibility(View.GONE);
            LevelNoPlotLabel.setText(LAeqRunningText);
        }

        LevelNoPlot.setText(level.getText());

        if (plot_visualisation.equals("1")) {
            LayoutLevel.setVisibility(View.VISIBLE);
            LayoutPlot.setVisibility(View.VISIBLE);
            LayoutLevelNoPlot.setVisibility(View.GONE);
        } else if (plot_visualisation.equals("0")) {
            LayoutLevel.setVisibility(View.GONE);
            LayoutPlot.setVisibility(View.GONE);
            LayoutLevelNoPlot.setVisibility(View.VISIBLE);
        }


        if  (timeLog == 1) {
            timeLogStringMinSec = "1 s";
        } else if (timeLog == 2) {
            timeLogStringMinSec = "2 s";
        } if (timeLog == 5) {
            timeLogStringMinSec = "5 s";
        } if (timeLog == 30) {
            timeLogStringMinSec = "30 s";
        } if (timeLog == 60) {
            timeLogStringMinSec = "1 m";
        } if (timeLog == 120) {
            timeLogStringMinSec = "2 m";
        } if (timeLog == 300) {
            timeLogStringMinSec = "5 m";
        } if (timeLog == 600) {
            timeLogStringMinSec = "10 m";
        } if (timeLog == 3600) {
            timeLogStringMinSec = "1 h";
        }

        durationTimeLog.setText(durationTimeLogText + " " + timeLogStringMinSec);

        precalculateWeightedA();

        startRecording((Float) gain, (Integer) finalCountTimeDisplay, (Integer) finalCountTimeLog);




    }


    private void startRecordingLogParametersFile() {
        // start the recording log file
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
        String filename = String.format("%s.OpeNoiseParameters.txt", df.format(new Date()));
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "openoise");

        if (!path.exists()) {
            Log.d("mio", "il path non esiste. Creato? : " + path.mkdirs());
        }
        try {
            File file = new File(path, filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            fosC = new FileOutputStream(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try{
            fosC.write("getMinBufferSize: ".getBytes());
            fosC.write(Integer.toString(AudioRecord.getMinBufferSize(
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING)).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("BLOCK_SIZE_FFT: ".getBytes());
            fosC.write(Integer.toString(BLOCK_SIZE_FFT).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("NUMBER_OF_FFT_PER_SECOND: ".getBytes());
            fosC.write(Integer.toString(NUMBER_OF_FFT_PER_SECOND).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("FREQRESOLUTION: ".getBytes());
            fosC.write(Double.toString(FREQRESOLUTION).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("Short.MAX_VALUE: ".getBytes());
            fosC.write(Double.toString(Short.MAX_VALUE).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("Time Display: ".getBytes());
            fosC.write(Double.toString(timeDisplay).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("Time Log: ".getBytes());
            fosC.write(Double.toString(timeLog).getBytes());
            fosC.write("\n".getBytes());
            fosC.write("Time Log string minsec: ".getBytes());
            fosC.write(timeLogStringMinSec.getBytes());
            fosC.write("\n".getBytes());

            fosC.write("\n\n\n\n\n\n\n".getBytes());


        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    private void stopRecordingLogParametersFile() {
        // stop the recording log file
        try {
            fosC.close();
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    private String dBformat(double dB) {
        // stop the recording log file
        return String.format(Locale.ENGLISH, "%.1f", dB);
    }




}
