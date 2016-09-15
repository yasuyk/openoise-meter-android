package it.piemonte.arpa.openoise;

/**
 * Created by stefmase on 16/04/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

public class PlotSonogram extends View {

    private Paint paintLines1, paintLines2, paintLabelsY, paintLabelsX, paintLabelsRectFill,paintLabelsRectStroke,paintSonogram,paintLinear2,paintLinear1Label,paintLinear2Label;
    private Path path;


    private float [] THIRD_OCTAVE = {16, 20, 25, 31.5f, 40, 50, 63, 80, 100, 125, 160, 200, 250, 315, 400, 500,
            630, 800, 1000, 1250, 1600, 2000, 2500, 3150, 4000, 5000, 6300, 8000, 10000, 12500, 16000, 20000};
    String [] THIRD_OCTAVE_LABEL = {"16", "20", "25", "31.5", "40", "50", "63", "80", "100", "125", "160", "200", "250", "315", "400", "500",
            "630", "800", "1000", "1250", "1600", "2000", "2500", "3150", "4000", "5000", "6300", "8000", "10000", "12500", "16000", "20000"};
    private float[] THIRD_OCTAVE_POSITION = new float[32];

    private float[][] dbHistory1 = new float[750][THIRD_OCTAVE.length];

    private String LAeqTimeDisplayText,LAeqRunningText;
    private double timeDisplay;

    private float fontSize;

    public PlotSonogram(Context context) {
        this(context, null, 0);
    }

    public PlotSonogram(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlotSonogram(Context context, AttributeSet attrs, int defStyleAttr) {
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
        paintLabelsY.setTextSize(20);
        paintLabelsY.setTextAlign(Paint.Align.RIGHT);

        paintLabelsX = new Paint();
        paintLabelsX.setTextSize(20);
        paintLabelsX.setTextAlign(Paint.Align.CENTER);

        paintLabelsRectFill = new Paint();
        paintLabelsRectFill.setStyle(Paint.Style.FILL);

        paintLabelsRectStroke = new Paint();
        paintLabelsRectStroke.setStyle(Paint.Style.STROKE);
        paintLabelsRectStroke.setStrokeWidth(1.0f);

        paintSonogram = new Paint();
        paintSonogram.setColor(getResources().getColor(R.color.plot_red));
        paintSonogram.setStyle(Paint.Style.STROKE);
        paintSonogram.setStrokeWidth(3.0f);
        paintSonogram.setTextAlign(Paint.Align.RIGHT);

        paintLinear1Label = new Paint();
        paintLinear1Label.setColor(getResources().getColor(R.color.plot_red));
        paintLinear1Label.setTextSize(20);
        paintLinear1Label.setTextAlign(Paint.Align.LEFT);

        // color with different style
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String display_color = preferences.getString("display_color", "2");

        if (display_color.equals("1")){
            paintLabelsRectFill.setColor(getResources().getColor(R.color.app_grey_light_light));
            paintLabelsRectStroke.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLabelsX.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLabelsY.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_dark));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_light));

        } else {
            paintLabelsRectFill.setColor(getResources().getColor(R.color.app_grey_dark_dark));
            paintLabelsRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsX.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsY.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_dark));
        }



        path = new Path();

//        for (int i=0, j=0; i<dbHistory1.length; i++,j++)
//        {
//            dbHistory1[i][j] = 0f;
//        }
        for (int i=0; i<THIRD_OCTAVE_POSITION.length; i++)
        {
            THIRD_OCTAVE_POSITION[i] = i + 1;
        }



        LAeqTimeDisplayText =  this.getContext().getResources().getString(R.string.LAeqTimeDisplay_label);
        LAeqRunningText =  this.getContext().getResources().getString(R.string.LAeqRunning_label);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
//        canvas.drawText("CIAO " + dbHistory1[100][10],  w/2, h/2, paintLabelsX);



//
//        fontSize = w * 0.04f;
//        paintLabelsX.setTextSize(fontSize);
//        paintLabelsY.setTextSize(fontSize);
//        paintLinear1Label.setTextSize(fontSize);
//        paintLinear2Label.setTextSize(fontSize);
//
//
//        float deltaLabelY = (paintLabelsY.descent() - paintLabelsY.ascent());
//        float deltaLabelY2 = (paintLinear1Label.descent() - paintLinear1Label.ascent());
//        float w_plot = getWidth() - paintLabelsY.measureText(Float.toString(paintLabelsY.getTextSize()));
//        float h_plot = getHeight() - deltaLabelY - paintLabelsY.descent() - deltaLabelY;
//        float yMaxAxis = THIRD_OCTAVE_POSITION.length;
//        float barWeight = w_plot / (float) (dbHistory1.length);
//        float x_ini = w - w_plot;
//
//
//        // grafico live
//        if (dbHistory1 != null) {
//
//            path.rewind();
//            path.moveTo(w - w_plot, deltaLabelY + h_plot - THIRD_OCTAVE_POSITION[0] * h_plot / yMaxAxis);
//            for (int i = 0; i < dbHistory1.length; i++) {
//                float x = w - w_plot + i*barWeight;
//                float y = 0;
//                for (int j=0; i < THIRD_OCTAVE_POSITION.length; j++) {
//                    y = deltaLabelY + h_plot - THIRD_OCTAVE_POSITION[i] * h_plot / yMaxAxis;
//
//                }
//                path.lineTo(x, y);
//            }
//            canvas.drawPath(path, paintSonogram);
//        }
//
//
//        String [] vertical_lines_label = {"-30", "-25", "-20", "-15", "-10", "-5 sec", ""};
//        float[] vertical_lines = {0, 5, 10, 15, 20, 25, 30};
//        for (int i = 0; i < vertical_lines.length; i++) {
//            vertical_lines[i] = (float) (dbHistory1.length * vertical_lines[i] /30 );
//            canvas.drawLine(w - w_plot + vertical_lines[i]*barWeight, deltaLabelY, w - w_plot + vertical_lines[i]*barWeight, deltaLabelY + h_plot, paintLines2);
//            canvas.drawText("" + vertical_lines_label[i],  w - w_plot + vertical_lines[i]*barWeight, deltaLabelY + h_plot - paintLabelsX.ascent(), paintLabelsX);
//        }
//
//
//
//        // test
////        canvas.drawText("" + x_ini + " " +  (w - w_plot) + " " +  w  + " " +  x_ini + barWeight, w, h_plot/2, paintLabelsY);
//
//        // Linea verticale 0 e fine plot
//        canvas.drawLine(w - w_plot, deltaLabelY, w - w_plot, deltaLabelY + h_plot, paintLines2);
////        canvas.drawLine(w, deltaLabelY, w, deltaLabelY + h_plot, paintLines2);
//
//        // Linee verticali labels
////        for (int i = 5; i <= yMaxAxis; i += 10) {
////            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines1);
////        }
//
//        // Linee orizzontali
//        for (int i = 5; i <= yMaxAxis; i += 10) {
//            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines1);
//        }
//        for (int i = 0; i <= yMaxAxis; i += 10) {
//            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines2);
//            canvas.drawText("" + i, w - w_plot - 5, deltaLabelY + h_plot - i * h_plot / yMaxAxis + paintLabelsY.descent(), paintLabelsY);
//        }
//
//        // scrivi dato
////        canvas.drawRect(w - 10 - paintLinear1Label.measureText("  " + LAeqRunningText + " dB(A)  "), deltaLabelY2, w - 10, 3 * deltaLabelY2 + paintLinear1Label.descent(), paintLabelsRectFill);
////        canvas.drawRect(w - 10 - paintLinear1Label.measureText("  " + LAeqRunningText + " dB(A)  "), deltaLabelY2, w - 10, 3 * deltaLabelY2  + paintLinear1Label.descent(), paintLabelsRectStroke);
////        canvas.drawText( "  " + LAeqTimeDisplayText + " dB(A) ", w - 10 - paintLinear2Label.measureText("  " + LAeqRunningText + " dB(A)  "), 2 * deltaLabelY2, paintLinear1Label);
////        canvas.drawText( "  " + LAeqRunningText + " dB(A) ", w - 10 - paintLinear2Label.measureText("  " + LAeqRunningText + " dB(A)  "), 3 * deltaLabelY2, paintLinear2Label);

    }

    public void setDataPlot(float[] data1) {

//        System.arraycopy(data1, 0, dbHistory1, 0, data1.length);

//        for (int i=1; i < dbHistory1.length; i++)
//        {
//            for (int j=0; j < dbHistory1[0].length; j++)
//            {
//                dbHistory1[i-1][j] = dbHistory1[i][j];
//
//                if (i==dbHistory1.length - 1){
//                    dbHistory1[dbHistory1.length - 1][j] = data1[j];
//                }
//            }
//        }

        invalidate();
    }


}