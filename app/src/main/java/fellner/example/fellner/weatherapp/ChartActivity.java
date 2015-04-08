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


    RelativeLayout wetterChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        wetterChart = (RelativeLayout)findViewById(R.id.chartView);
        setContentView(new iniView(this));
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

            canvas.drawLine(70, height - 180, width*7/8 + 70, height - 180,achsen); //x Achse
            canvas.drawLine(70, height - 180, 70, height - this.getMax()*20-180,achsen); //y Achse

            for(int i = 0; i <= this.getMax(); i+=10){
                canvas.drawText(i+"", 20, height - i * 20 - 155, text);
            }

            for (int i = 0; i < temperaturen.length; i++){
                float left = (width/8)*i+70;
                float top = height - temperaturen[i] * 20 - 180;
                float right = left+30;
                float bottom = top+30;
                canvas.drawOval(new RectF(left,top,right,bottom),paintOval);
                if(i!=0){
                    float lastX = (width/8)*(i-1)+85;
                    float lastY = height - temperaturen[(i-1)] * 20 - 165;
                    float thisX = (width/8)*i+85;
                    float thisY = height - temperaturen[i] * 20 - 165;
                    canvas.drawLine(lastX,lastY,thisX,thisY,paintLine);
                }

                Rect bounds = new Rect();
                text.getTextBounds(uhrzeit[i], 0, 1, bounds);

                canvas.drawText(uhrzeit[i], (width / 8) * i + 40 - bounds.width()/2, height - 100, text); //x Achsenbeschriftung mit der Uhrzeit

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
    }
}
