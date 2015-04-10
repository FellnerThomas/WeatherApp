package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        RelativeLayout wetterChart = (RelativeLayout)findViewById(R.id.chartView);
        wetterChart.addView(new iniView(this));
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
            int width;
            int height;
            width = size.x;
            height = size.y;
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
            text.setTextSize(30);

            Paint achsen = new Paint();
            achsen.setAntiAlias(true);
            achsen.setColor(Color.BLACK);
            achsen.setStrokeWidth(3);

            float minHeight = this.getMin()*height/100;

            canvas.drawLine(70, 9*height/10+25, width*7/8 + 85 , 9*height/10+25,achsen); //x Achse
            canvas.drawLine(70, 9*height/10+25, 70, 9*height/10 - this.getMax()*height/100 + minHeight,achsen); //y Achse

            for(float i = this.getMin(); i <= this.getMax(); i+=10){
                canvas.drawText((int)i+"", 20, 9*height/10 - i * height/100 + 25 + minHeight, text);
            }
            canvas.drawText("C°", 60, 9*height/10 - (this.getMax()+2.5f) * height/100 + 25 + minHeight, text);
            float lastX = 0;
            float lastY = 0;
            for (int i = 0; i < temperaturen.length; i++){
                float left = (width/8)*i+72.5f;
                float top = 9*height/10 - temperaturen[i] * height/100 + minHeight;
                float right = left+25;
                float bottom = top+25;
                canvas.drawOval(new RectF(left,top,right,bottom),paintOval);
                if(i!=0){
                    canvas.drawLine(lastX,lastY,left+15,top+15,paintLine);
                }
                lastX = left+15;
                lastY = top+15;
                Rect bounds = new Rect();
                text.getTextBounds(uhrzeit[i], 0, 1, bounds);

                canvas.drawText(uhrzeit[i], (width / 8) * i + 40 - bounds.width()/2, height - height/10 + 80, text); //x Achsenbeschriftung mit der Uhrzeit

            }
        }

        public float getMax(){
            float max = temperaturen[0];
            for(int i = 1; i < temperaturen.length; i++){
                if(temperaturen[i] > max){
                    max = temperaturen[i];
                }
            }
            return max;
        }

        public float getMin(){
            float min = temperaturen[0];
            for(int i = 1; i < temperaturen.length; i++){
                if(temperaturen[i] < min){
                    min = temperaturen[i];
                }
            }
            return min;
        }
    }
}
