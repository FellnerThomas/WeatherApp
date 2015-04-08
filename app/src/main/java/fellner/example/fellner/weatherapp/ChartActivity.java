package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Fellner on 3/5/2015.
 */
public class ChartActivity extends Activity{
    public String[] uhrzeit = {"00:00","03:00","06:00","09:00","12:00","15:00","18:00","21:00"};
    public float[] temperaturen = {10,20,30,40,60,20,30,10};


    RelativeLayout wetterChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        wetterChart = (RelativeLayout)findViewById(R.id.chartView);
        setContentView(new iniView(this));

        /*Paint gridEffects = new Paint();
        gridEffects.setColor(Color.BLUE);
        gridEffects.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        gridEffects.setStyle(Paint.Style.STROKE);
        gridEffects.setAntiAlias(true);
        gridEffects.setStrokeWidth(Tools.fromDpToPx(0.75f));
        //wetterChart.setGrid(ChartView.GridType.HORIZONTAL, gridEffects);

        wetterChart.reset();

        LineSet ls = new LineSet();*/


        /*ls.addPoints(uhrzeit,temperaturen);
        ls.setDots(true);
        ls.setDotsColor(Color.RED);
        ls.setVisible(true);
        ls.setLineColor(Color.RED);
        //ls.setDots(true);
        //ls.setDotsColor(Color.RED);
        //ls.setDotsRadius(10);
        //ls.setDotsStrokeThickness(10);
        //ls.setDotsStrokeColor(Color.BLACK);

        wetterChart.addData(ls);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class iniView extends View {

        public iniView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            Paint paintLine = new Paint();
            paintLine.setStyle(Paint.Style.STROKE);
            paintLine.setAntiAlias(true);
            paintLine.setColor(Color.RED);
            paintLine.setStrokeWidth(8);

            Paint paintOval = new Paint();
            paintOval.setAntiAlias(true);
            paintOval.setColor(Color.RED);

            Paint text = new Paint();
            text.setStyle(Paint.Style.STROKE);
            text.setAntiAlias(true);
            text.setColor(Color.BLACK);
            text.setTextSize(50);

            for (int i = 0 ; i < 8 ; i++){
                float left = (width/8)*i+70;
                float top = height - temperaturen[i] * 20 - 15;
                float right = left+30;
                float bottom = top+30;
                canvas.drawOval(new RectF(left,top,right,bottom),paintOval);
                if(i!=0){
                    float lastX = (width/8)*(i-1)+85;
                    float lastY = height - temperaturen[(i-1)] * 20;
                    float thisX = (width/8)*i+85;
                    float thisY = height - temperaturen[i] * 20;
                    canvas.drawLine(lastX,lastY,thisX,thisY,paintLine);
                }

                Rect bounds = new Rect();
                text.getTextBounds(uhrzeit[i], 0, 1, bounds);

                canvas.drawText(uhrzeit[i], (width / 8) * i + 40 - bounds.width()/2, height - 120, text);

            }
        }

    }
}
