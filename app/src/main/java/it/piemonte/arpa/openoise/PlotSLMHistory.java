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

public class PlotSLMHistory extends View {


    private Paint paintLines1, paintLines2, paintLabelsY, paintLabelsX, paintLabelsRectFill,paintLabelsRectStroke,paintLinear1,paintLinear2,paintLinear1Label,paintLinear2Label;
    private Path path;
    private float[] dbHistory1 = new float[750];
    private float[] dbHistory2 = new float[750];
    private String LAeqTimeDisplayText,LAeqRunningText;
    private double timeDisplay;

    private float fontSize;

    public PlotSLMHistory(Context context) {
        this(context, null, 0);
    }

    public PlotSLMHistory(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlotSLMHistory(Context context, AttributeSet attrs, int defStyleAttr) {
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

        paintLinear1 = new Paint();
        paintLinear1.setColor(getResources().getColor(R.color.plot_red));
        paintLinear1.setStyle(Paint.Style.STROKE);
        paintLinear1.setStrokeWidth(3.0f);
        paintLinear1.setTextAlign(Paint.Align.RIGHT);

        paintLinear2 = new Paint();
        paintLinear2.setColor(getResources().getColor(R.color.plot_violet));
        paintLinear2.setStyle(Paint.Style.STROKE);
        paintLinear2.setStrokeWidth(3.0f);
        paintLinear2.setTextAlign(Paint.Align.RIGHT);

        paintLinear1Label = new Paint();
        paintLinear1Label.setColor(getResources().getColor(R.color.plot_red));
        paintLinear1Label.setTextSize(20);
        paintLinear1Label.setTextAlign(Paint.Align.RIGHT);

        paintLinear2Label = new Paint();
        paintLinear2Label.setColor(getResources().getColor(R.color.plot_violet));
        paintLinear2Label.setTextSize(20);
        paintLinear2Label.setTextAlign(Paint.Align.RIGHT);

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

        } else  if (display_color.equals("2")){
            paintLabelsRectFill.setColor(getResources().getColor(R.color.app_grey_dark_dark));
            paintLabelsRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsX.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsY.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_dark));

        } else  if (display_color.equals("3")){
            paintLabelsRectFill.setColor(getResources().getColor(R.color.app_black));
            paintLabelsRectStroke.setColor(getResources().getColor(R.color.app_grey_light));
            paintLabelsX.setColor(getResources().getColor(R.color.app_white));
            paintLabelsY.setColor(getResources().getColor(R.color.app_white));
            paintLines2.setColor(getResources().getColor(R.color.app_grey_light));
            paintLines1.setColor(getResources().getColor(R.color.app_grey_dark));
        }


        String timeDisplayString = preferences.getString("timeDisplay", "1");
        if  (timeDisplayString.equals("0.5")) {
            LAeqTimeDisplayText = this.getContext().getResources().getString(R.string.LAeqTimeDisplay_label) + "(0.5 s)";
        } else if (timeDisplayString.equals("1")) {
            LAeqTimeDisplayText = this.getContext().getResources().getString(R.string.LAeqTimeDisplay_label) + "(1 s)";
        } else if (timeDisplayString.equals("2")) {
            LAeqTimeDisplayText = this.getContext().getResources().getString(R.string.LAeqTimeDisplay_label) + "(2 s)";
        }
        LAeqRunningText =  this.getContext().getResources().getString(R.string.LAeqRunning_label);



        path = new Path();

        for (int i=0; i<dbHistory1.length; i++)
        {
            dbHistory1[i] = 0;
            dbHistory2[i] = 0;
        }



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        float w = getWidth();

        fontSize = w * 0.04f;
        paintLabelsX.setTextSize(fontSize);
        paintLabelsY.setTextSize(fontSize);
        paintLinear1Label.setTextSize(fontSize);
        paintLinear2Label.setTextSize(fontSize);

        float h = getHeight();
        float deltaLabelY = (paintLabelsY.descent() - paintLabelsY.ascent());
        float deltaLabelY2 = (paintLinear1Label.descent() - paintLinear1Label.ascent());
        float w_plot = getWidth() - paintLabelsY.measureText(Float.toString(paintLabelsY.getTextSize()));
        float h_plot = getHeight() - deltaLabelY - paintLabelsY.descent() - deltaLabelY;
        float yMaxAxis = 110f;
        float barWeight = w_plot / (float) (dbHistory1.length);
        float x_ini = w - w_plot;


        // grafico live
        if (dbHistory1 != null) {

            path.rewind();
            path.moveTo(w - w_plot, deltaLabelY + h_plot - dbHistory1[0] * h_plot / yMaxAxis);
            for (int i = 0; i < dbHistory1.length; i++) {
                float x = w - w_plot + i*barWeight;
                float y = deltaLabelY + h_plot - dbHistory1[i] * h_plot / yMaxAxis;
                if (Float.isInfinite(y) || Float.isNaN(y)){
                    y = 0;
                }
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paintLinear1);
        }

        // grafico running
        if (dbHistory2 != null) {

            path.rewind();
            path.moveTo(w - w_plot, deltaLabelY + h_plot - dbHistory2[0] * h_plot / yMaxAxis);
            for (int i = 0; i < dbHistory2.length; i++) {
                float x = w - w_plot + i*barWeight;
                float y = deltaLabelY + h_plot - dbHistory2[i] * h_plot / yMaxAxis;
                if (Float.isInfinite(y) || Float.isNaN(y)){
                    y = 0;
                }
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paintLinear2);

        }

        String [] vertical_lines_label = {"-30", "-25", "-20", "-15", "-10", "-5 sec", ""};
        float[] vertical_lines = {0, 5, 10, 15, 20, 25, 30};
        for (int i = 0; i < vertical_lines.length; i++) {
            vertical_lines[i] = (float) (dbHistory1.length * vertical_lines[i] /30 );
            canvas.drawLine(w - w_plot + vertical_lines[i]*barWeight, deltaLabelY, w - w_plot + vertical_lines[i]*barWeight, deltaLabelY + h_plot, paintLines2);
            canvas.drawText("" + vertical_lines_label[i],  w - w_plot + vertical_lines[i]*barWeight, deltaLabelY + h_plot - paintLabelsX.ascent(), paintLabelsX);
        }

        
        
        // test
//        canvas.drawText("" + x_ini + " " +  (w - w_plot) + " " +  w  + " " +  x_ini + barWeight, w, h_plot/2, paintLabelsY);

        // Linea verticale 0 e fine plot
        canvas.drawLine(w - w_plot, deltaLabelY, w - w_plot, deltaLabelY + h_plot, paintLines2);
//        canvas.drawLine(w, deltaLabelY, w, deltaLabelY + h_plot, paintLines2);

        // Linee verticali labels
//        for (int i = 5; i <= yMaxAxis; i += 10) {
//            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines1);
//        }
        
        // Linee orizzontali
        for (int i = 5; i <= yMaxAxis; i += 10) {
            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines1);
        }
        for (int i = 0; i <= yMaxAxis; i += 10) {
            canvas.drawLine(w - w_plot, deltaLabelY + h_plot - i * h_plot / yMaxAxis, w, deltaLabelY + h_plot - i * h_plot / yMaxAxis, paintLines2);
            canvas.drawText("" + i, w - w_plot - 5, deltaLabelY + h_plot - i * h_plot / yMaxAxis + paintLabelsY.descent(), paintLabelsY);
        }

        // scrivi dato
//        canvas.drawRect(w - 10 - paintLinear1Label.measureText("  " + LAeqTimeDisplayText + "  "), deltaLabelY2, w - 10, 3 * deltaLabelY2 + paintLinear1Label.descent(), paintLabelsRectFill);
//        canvas.drawRect(w - 10 - paintLinear1Label.measureText("  " + LAeqTimeDisplayText + "  "), deltaLabelY2, w - 10, 3 * deltaLabelY2  + paintLinear1Label.descent(), paintLabelsRectStroke);
//        canvas.drawText( "  " + LAeqTimeDisplayText + "  ", w - 10, 2 * deltaLabelY2, paintLinear1Label);
//        canvas.drawText( "  " + LAeqRunningText + "  ", w - 10, 3 * deltaLabelY2, paintLinear2Label);

        // scrivi dato
        float text_width = paintLinear1Label.measureText("   " + LAeqRunningText + "  " + LAeqTimeDisplayText + "  ");
        canvas.drawRect(w - 10 - text_width, deltaLabelY2, w - 10, 2 * deltaLabelY2 + 2*paintLinear1Label.descent(), paintLabelsRectFill);
        canvas.drawRect(w - 10 - text_width, deltaLabelY2, w - 10, 2 * deltaLabelY2  + 2*paintLinear1Label.descent(), paintLabelsRectStroke);
        canvas.drawText( "  " + LAeqTimeDisplayText + "  ", w - 10, 2 * deltaLabelY2, paintLinear1Label);
        canvas.drawText( "  " + LAeqRunningText + "  ", w - 10 - paintLinear1Label.measureText(" " + LAeqTimeDisplayText + "  "), 2 * deltaLabelY2, paintLinear2Label);



    }

    public void setDataPlot(float[] data1, float[] data2, int length, double timeDisplay_input) {

        dbHistory1 = new float[length];
        dbHistory2 = new float[length];
        timeDisplay = timeDisplay_input;

        if (dbHistory1 == null || dbHistory1.length != data1.length){
            dbHistory1 = new float[data1.length];

        }
        System.arraycopy(data1, 0, dbHistory1, 0, data2.length);

        if (dbHistory2 == null || dbHistory2.length != data2.length){
            dbHistory2 = new float[data2.length];

        }
        System.arraycopy(data2, 0, dbHistory2, 0, data2.length);

        invalidate();
    }




}