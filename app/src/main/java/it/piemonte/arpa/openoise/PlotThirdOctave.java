package it.piemonte.arpa.openoise;

/**
 * Created by stefmase on 16/04/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class PlotThirdOctave extends View {

    private Paint paintLines2, paintLabelsY, paintLabelsX, paintLabelsMax,paintLabelsMaxRectFill,paintLabelsMaxRectStroke, paintLinear, paintLines1, paintLinearMin, paintLinearMax,paintLinearRunning;
    private Paint paintLegend,paintLegendRectFill,paintLegendRectStroke;
    private int n = 3;
    private float [] THIRD_OCTAVE = {16, 20, 25, 31.5f, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500,
                                     630, 800, 1000, 1250, 1600, 2000, 2500, 3150, 4000, 5000, 6300, 8000, 10000, 12500, 16000, 20000};
    String [] THIRD_OCTAVE_LABEL = {"16", "20", "25", "31.5", "40", "50", "63", "80", "100", "125", "160", "200", "250", "315", "400", "500",
                "630", "800", "1000", "1250", "1600", "2000", "2500", "3150", "4000", "5000", "6300", "8000", "10000", "12500", "16000", "20000"};
    private float[] dbBand;
    private float[] dbBandMin = new float[THIRD_OCTAVE.length];
    private float[] dbBandMax = new float[THIRD_OCTAVE.length];
    private float[] dbBandRunning = new float[THIRD_OCTAVE.length];

    private double dbGlobalRunning, dbGlobalMax, dbGlobal, dbGlobalMin;
    private String LeqTimeDisplay;

    private float fontSize;

    private float test, test0, test1, test2;

    public PlotThirdOctave(Context context) {
        this(context, null, 0);
    }

    public PlotThirdOctave(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlotThirdOctave(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paintLines1 = new Paint();
//        paintLines1.setColor(getResources().getColor(R.color.plot_grey_light));
        paintLines1.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLines1.setStrokeWidth(1.0f);

        paintLines2 = new Paint();
//        paintLines2.setColor(getResources().getColor(R.color.plot_grey_dark));
        paintLines2.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLines2.setStrokeWidth(1.0f);


        paintLabelsY = new Paint();
//        paintLabelsY.setColor(getResources().getColor(R.color.plot_text));
        paintLabelsY.setTextSize(20);
        paintLabelsY.setTextAlign(Paint.Align.RIGHT);

        paintLabelsX = new Paint();
//        paintLabelsX.setColor(getResources().getColor(R.color.plot_text));
        paintLabelsX.setTextSize(20);
        paintLabelsX.setTextAlign(Paint.Align.CENTER);

        paintLabelsMax = new Paint();
        paintLabelsMax.setColor(getResources().getColor(R.color.plot_red));
        paintLabelsMax.setTextSize(20);
        paintLabelsMax.setTextAlign(Paint.Align.RIGHT);

        paintLabelsMaxRectFill = new Paint();
        paintLabelsMaxRectFill.setStyle(Paint.Style.FILL);

        paintLabelsMaxRectStroke = new Paint();
        paintLabelsMaxRectStroke.setStyle(Paint.Style.STROKE);
        paintLabelsMaxRectStroke.setStrokeWidth(2.0f);

        paintLegendRectFill = new Paint();
        paintLegendRectFill.setStyle(Paint.Style.FILL);

        paintLegendRectStroke = new Paint();
        paintLegendRectStroke.setStyle(Paint.Style.STROKE);
        paintLegendRectStroke.setStrokeWidth(2.0f);

        paintLinearMax = new Paint();
        paintLinearMax.setColor(getResources().getColor(R.color.plot_blue));
        paintLinearMax.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLinearMax.setStrokeWidth(2.0f);

        paintLinear = new Paint();
        paintLinear.setColor(getResources().getColor(R.color.plot_red));
        paintLinear.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLinear.setStrokeWidth(2.0f);

        paintLinearMin = new Paint();
        paintLinearMin.setColor(getResources().getColor(R.color.plot_green));
        paintLinearMin.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLinearMin.setStrokeWidth(2.0f);

        paintLinearRunning = new Paint();
        paintLinearRunning.setColor(getResources().getColor(R.color.plot_violet));
        paintLinearRunning.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLinearRunning.setStrokeWidth(2.0f);


        // color with different style
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String display_color = preferences.getString("display_color", "2");

        if (display_color.equals("1")){
            paintLabelsMaxRectFill.setColor(getResources().getColor(R.color.app_grey_light_light));
            paintLabelsMaxRectStroke.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLegendRectFill.setColor(getResources().getColor(R.color.app_grey_light_light));
            paintLegendRectStroke.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLabelsX.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLabelsY.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_light));

        } else if (display_color.equals("2")) {
            paintLabelsMaxRectFill.setColor(getResources().getColor(R.color.app_grey_dark_dark));
            paintLabelsMaxRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLegendRectFill.setColor(getResources().getColor(R.color.app_grey_dark_dark));
            paintLegendRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsX.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsY.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_dark));

        } else if (display_color.equals("3")) {
            paintLabelsMaxRectFill.setColor(getResources().getColor(R.color.app_black));
            paintLabelsMaxRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLegendRectFill.setColor(getResources().getColor(R.color.app_black));
            paintLegendRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsX.setColor(getResources().getColor(R.color.app_white));
            paintLabelsY.setColor(getResources().getColor(R.color.app_white));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_dark));
        }

        String timeDisplayString = preferences.getString("timeDisplay", "1");
        if  (timeDisplayString.equals("0.5")) {
            LeqTimeDisplay = this.getContext().getResources().getString(R.string.LeqTimeDisplay_label) + "(0.5 s)";
        } else if (timeDisplayString.equals("1")) {
            LeqTimeDisplay = this.getContext().getResources().getString(R.string.LeqTimeDisplay_label) + "(1 s)";
        } else if (timeDisplayString.equals("2")) {
            LeqTimeDisplay = this.getContext().getResources().getString(R.string.LeqTimeDisplay_label) + "(2 s)";
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();

        fontSize = w * 0.04f;
        paintLabelsX.setTextSize(fontSize);
        paintLabelsY.setTextSize(fontSize);
        paintLabelsMax.setTextSize(fontSize);

        float h = getHeight();
        float w_plot = getWidth() - paintLabelsX.measureText(Float.toString(paintLabelsY.getTextSize())); //* 0.925f;
        float deltaLabelX = (paintLabelsX.descent() - paintLabelsX.ascent());
        float deltaLabelY = (paintLabelsY.descent() - paintLabelsY.ascent());
        float deltaLabelMax = (paintLabelsMax.descent() - paintLabelsMax.ascent());
        float h_plot = getHeight() - deltaLabelX - deltaLabelY;
        float yMaxAxis = 110f;
        float xMaxIst = 0;
        float yMaxIst = 0;
        float barWeight = w_plot / (float) (THIRD_OCTAVE.length + 2);

        // grafico FFT non pesato
        String [] vertical_lines_label = {"16", "", "", "31.5", "", "", "63", "", "", "125", "", "", "250", "", "", "500",
                "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", ""};




        if (dbBand != null) {
            float dbyMaxIst = dbBand[0];
            int iMaxIst = 0;

            for (int i = 0; i < dbBand.length; i++) {
                float x_ini = w - w_plot + i * barWeight;
                float xGlobal = w - w_plot + (THIRD_OCTAVE.length + 1) * barWeight;

                //// max
                float yMax = dbBandMax[i] * h_plot / yMaxAxis;
                yMax = h_plot - yMax;
                canvas.drawRect(x_ini, deltaLabelY + yMax, x_ini + barWeight, deltaLabelY + h_plot, paintLinearMax);
                /// max global
                float ydbGlobalMax = (float) dbGlobalMax * h_plot / yMaxAxis;
                ydbGlobalMax = h_plot - ydbGlobalMax;
                canvas.drawRect(xGlobal, deltaLabelY + ydbGlobalMax, xGlobal + barWeight, deltaLabelY + h_plot, paintLinearMax);

                /// running
                float yRunning = dbBandRunning[i] * h_plot / yMaxAxis;
                yRunning = h_plot - yRunning;
                canvas.drawRect(x_ini, deltaLabelY + yRunning, x_ini + barWeight, deltaLabelY + h_plot, paintLinearRunning);
                /// running global
                float ydbGlobalRunning = (float) dbGlobalRunning * h_plot / yMaxAxis;
                ydbGlobalRunning = h_plot - ydbGlobalRunning;
                canvas.drawRect(xGlobal, deltaLabelY + ydbGlobalRunning, xGlobal + barWeight, deltaLabelY + h_plot, paintLinearRunning);

                /// istantaneo
                float y = dbBand[i] * h_plot / yMaxAxis;
                y = h_plot - y;
                canvas.drawRect(x_ini, deltaLabelY + y, x_ini + barWeight, deltaLabelY + h_plot, paintLinear);
                /// istantaneo global
                float ydbGlobal = (float) dbGlobal * h_plot / yMaxAxis;
                ydbGlobal = h_plot - ydbGlobal;
                canvas.drawRect(xGlobal, deltaLabelY + ydbGlobal, xGlobal + barWeight, deltaLabelY + h_plot, paintLinear);

                if(dbyMaxIst < dbBand[i]){
                    yMaxIst = deltaLabelY + y;
                    dbyMaxIst = dbBand[i];
                    xMaxIst = x_ini + barWeight/2;
                    iMaxIst = i;
                }

                /// min
                float yMin = dbBandMin[i] * h_plot / yMaxAxis;
                yMin = h_plot - yMin;
                canvas.drawRect(x_ini, deltaLabelY + yMin, x_ini + barWeight, deltaLabelY + h_plot, paintLinearMin);
                /// min global
                float ydbGlobalMin = (float) dbGlobalMin * h_plot / yMaxAxis;
                ydbGlobalMin = h_plot - ydbGlobalMin;
                canvas.drawRect(xGlobal, deltaLabelY + ydbGlobalMin, xGlobal + barWeight, deltaLabelY + h_plot, paintLinearMin);

                canvas.drawLine(x_ini + barWeight / 2, deltaLabelY + h_plot, x_ini + barWeight / 2, deltaLabelY + h_plot + paintLabelsX.ascent() / 3, paintLines2);
                if (vertical_lines_label[i] != "") {
                    canvas.drawText("" + vertical_lines_label[i], x_ini + barWeight / 2, deltaLabelY + h_plot - paintLabelsX.ascent(), paintLabelsX);
                    canvas.drawLine(x_ini + barWeight / 2, deltaLabelY + h_plot, x_ini + barWeight / 2, deltaLabelY + h_plot + paintLabelsX.ascent()*0.75f, paintLines2);
                }
                canvas.drawText("A", xGlobal + barWeight / 2, deltaLabelY + h_plot - paintLabelsX.ascent(), paintLabelsX);
                canvas.drawLine(xGlobal + barWeight / 2, deltaLabelY + h_plot, xGlobal + barWeight / 2, deltaLabelY + h_plot + paintLabelsX.ascent()*0.75f, paintLines2);



            }
            // Linea verticale 0
            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - yMaxAxis * h_plot / yMaxAxis, w - w_plot, deltaLabelY + h_plot, paintLines2);

            // Linee orizzontali
            for (int i = 5; i <= yMaxAxis; i += 10) {
                canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines1);
            }
            for (int i = 0; i <= yMaxAxis; i += 10) {
                canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines2);
                canvas.drawText("" + i, w - w_plot - 5, deltaLabelY + h_plot - i * h_plot / yMaxAxis + paintLabelsX.descent(), paintLabelsY);
            }

//            // scrivi massimo
//            canvas.drawRect(w - paintLabelsMax.measureText(" 20000 Hz ") - 10, deltaLabelMax, w - 10, 3 * deltaLabelMax + paintLabelsMax.descent(), paintLabelsMaxRectStroke);
//            canvas.drawRect(w - paintLabelsMax.measureText(" 20000 Hz ") - 10, deltaLabelMax, w - 10, 3 * deltaLabelMax  + paintLabelsMax.descent(), paintLabelsMaxRectFill);
//            canvas.drawText(dBformat(dbyMaxIst) + " dB ", w - 10, 2 * deltaLabelMax, paintLabelsMax);
//            canvas.drawText(Bandformat(THIRD_OCTAVE[iMaxIst]) + " Hz ", w - 10, 3 * deltaLabelMax, paintLabelsMax);

            // scrivi massimo
//            float text_width = paintLabelsMax.measureText("  " + LeqTimeDisplay + " 999.9 dB 20000 Hz ");
//            canvas.drawRect(w - text_width - 10, deltaLabelMax, w - 10, 3 * deltaLabelMax + paintLabelsMax.descent(), paintLabelsMaxRectStroke);
//            canvas.drawRect(w - text_width - 10, deltaLabelMax, w - 10, 3 * deltaLabelMax  + paintLabelsMax.descent(), paintLabelsMaxRectFill);
//            paintLabelsMax.setColor(getResources().getColor(R.color.plot_red));
//            canvas.drawText("  " + LeqTimeDisplay + " ", w - 10  - paintLabelsMax.measureText(" 999.9 dB 20000 Hz "), 2 * deltaLabelMax, paintLabelsMax);
//            canvas.drawText(dBformat(dbyMaxIst) + " dB ", w - 10 - paintLabelsMax.measureText(" 20000 Hz "), 2 * deltaLabelMax, paintLabelsMax);
//            canvas.drawText(Bandformat(THIRD_OCTAVE[iMaxIst]) + " Hz ", w - 10, 2 * deltaLabelMax, paintLabelsMax);
//            paintLabelsMax.setColor(getResources().getColor(R.color.plot_blue));
//            canvas.drawText("  LMax  ", w - 10 - paintLabelsMax.measureText(" 999.9 dB 20000 Hz "), 3 * deltaLabelMax, paintLabelsMax);
//            paintLabelsMax.setColor(getResources().getColor(R.color.plot_violet));
//            canvas.drawText("  Leq(t)  ", w - 10 - paintLabelsMax.measureText("20000 Hz "), 3 * deltaLabelMax, paintLabelsMax);
//            paintLabelsMax.setColor(getResources().getColor(R.color.plot_green));
//            canvas.drawText("  LMin  ", w - 10, 3 * deltaLabelMax, paintLabelsMax);

            // scrivi massimo
            float text_width = paintLabelsMax.measureText("   LMin  " + LeqTimeDisplay + "  LAeq(t)  LMax  ");
            canvas.drawRect(w - text_width - 10, deltaLabelMax, w - 10, 2 * deltaLabelMax + 10 + paintLabelsMax.descent(), paintLegendRectStroke);
            canvas.drawRect(w - text_width - 10, deltaLabelMax, w - 10, 2 * deltaLabelMax  + 10 + paintLabelsMax.descent(), paintLegendRectFill);
            paintLabelsMax.setColor(getResources().getColor(R.color.plot_blue));
            canvas.drawText("  LMax  ", w - 10, 2 * deltaLabelMax, paintLabelsMax);
            paintLabelsMax.setColor(getResources().getColor(R.color.plot_violet));
            canvas.drawText("  Leq(t)  ", w - 10 - paintLabelsMax.measureText("  LMax  "), 2 * deltaLabelMax, paintLabelsMax);
            paintLabelsMax.setColor(getResources().getColor(R.color.plot_red));
            canvas.drawText("  " + LeqTimeDisplay + " ", w - 10  - paintLabelsMax.measureText("  LAeq(t)  LMax  "), 2 * deltaLabelMax, paintLabelsMax);
            paintLabelsMax.setColor(getResources().getColor(R.color.plot_green));
            canvas.drawText("  LMin  ", w - 10 - paintLabelsMax.measureText("  " + LeqTimeDisplay + "  LAeq(t)  LMax  "), 2 * deltaLabelMax, paintLabelsMax);

            // non lo disegna non so perché... quindi lo commento
//            Path lineA = new Path();
//            lineA.moveTo(xMaxIst-10, yMaxIst-10);
//            lineA.lineTo(xMaxIst-30, yMaxIst-30);
//            canvas.drawPath(lineA, paintLabelsMax);

            fontSize = w * 0.03f;
            paintLabelsMax.setTextSize(fontSize);
            paintLabelsMax.setColor(getResources().getColor(R.color.plot_red));
            deltaLabelMax = (paintLabelsMax.descent() - paintLabelsMax.ascent());
            text_width = paintLabelsMax.measureText("  20000 Hz  ");
            paintLabelsMaxRectStroke.setColor(getResources().getColor(R.color.plot_red));
            canvas.drawRect(xMaxIst - 20 - text_width, yMaxIst - 10 - (float) 2.5 * deltaLabelMax, xMaxIst  - 20, yMaxIst - 10, paintLabelsMaxRectStroke);
            canvas.drawRect(xMaxIst - 20 - text_width, yMaxIst - 10 - (float) 2.5 * deltaLabelMax, xMaxIst  - 20, yMaxIst - 10, paintLabelsMaxRectFill);
            canvas.drawText(Bandformat(THIRD_OCTAVE[iMaxIst]) + " Hz  ", xMaxIst - 20, yMaxIst - 10 - (float) 0.5 * deltaLabelMax, paintLabelsMax);
            canvas.drawText(dBformat(dbyMaxIst) + " dB  ", xMaxIst - 20, yMaxIst - 10 - (float) 1.5 * deltaLabelMax, paintLabelsMax);

        }

        // test
//        canvas.drawText("" + dbBandMin[0] + "  " + dbBandMin[10] + "  " + dbBandMin[20] + "  " + dbBandMin[dbBandMin.length - 1], w / 2, h / 2, paintLabelsX);
    }

    public void setDataPlot(float[] data1, float[] data2, float[] data3, float[] data4, float dbFftAGlobal, float dbFftAGlobalMin, float dbFftAGlobalMax, float dbFftAGlobalRunning) {

        if (dbBand == null || dbBand.length != data1.length)
            dbBand = new float[data1.length];
        System.arraycopy(data1, 0, dbBand, 0, data1.length);

        if (dbBandMin == null || dbBandMin.length != data2.length)
            dbBandMin = new float[data2.length];
        System.arraycopy(data2, 0, dbBandMin, 0, data2.length);

        if (dbBandMax == null || dbBandMax.length != data3.length)
            dbBandMax = new float[data3.length];
        System.arraycopy(data3, 0, dbBandMax, 0, data3.length);

        if (dbBandRunning == null || dbBandRunning.length != data4.length)
            dbBandRunning = new float[data4.length];
        System.arraycopy(data4, 0, dbBandRunning, 0, data4.length);

        // parte per bande senza valori
        dbBand[1] = dbBand[0];
        dbBand[3] = dbBand[2];
        dbBand[4] = dbBand[5];
        dbBand[6] = dbBand[7];

        dbBandMin[1] = dbBandMin[0];
        dbBandMin[3] = dbBandMin[2];
        dbBandMin[4] = dbBandMin[5];
        dbBandMin[6] = dbBandMin[7];

        dbBandMax[1] = dbBandMax[0];
        dbBandMax[3] = dbBandMax[2];
        dbBandMax[4] = dbBandMax[5];
        dbBandMax[6] = dbBandMax[7];

        dbBandRunning[1] = dbBandRunning[0];
        dbBandRunning[3] = dbBandRunning[2];
        dbBandRunning[4] = dbBandRunning[5];
        dbBandRunning[6] = dbBandRunning[7];


        dbGlobal = dbFftAGlobal;
        dbGlobalMin = dbFftAGlobalMin;
        dbGlobalMax = dbFftAGlobalMax;
        dbGlobalRunning = dbFftAGlobalRunning;


        invalidate();
    }

    private String dBformat(double dB) {
        // stop the recording log file
        return String.format(Locale.ENGLISH, "%.1f", dB);

    }

    private String Bandformat(double dB) {
        // stop the recording log file
        return String.format(Locale.ENGLISH, "%.0f", dB);

    }

}