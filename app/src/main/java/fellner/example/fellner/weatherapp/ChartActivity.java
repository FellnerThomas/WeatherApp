package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import WeatherParser.DailyWeather;
import WeatherParser.FetchWeatherData;
import WeatherParser.ThreeHourlyWeather;

/**
 * Created by Fellner on 3/5/2015.
 */
public class ChartActivity extends Activity{
    public static String city;

    public static String[] uhrzeit;
    public static float[] temperaturen;

    float abstand;
    float minHeight;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //TEST
        DailyWeather dw = null;
        ArrayList<ThreeHourlyWeather> thw = null;
        try {
            dw = FetchWeatherData.fetchIt("http://api.openweathermap.org/data/2.5/forecast?q="+city+"&mode=xml");
            thw = dw.getThreeHourlyWeatherData();
        }catch(Exception e) {
        }
        if(thw != null) {
            temperaturen = new float[8];
            uhrzeit = new String[8];
            for (int i = 0; i < thw.size(); i++) {
                temperaturen[i] = Float.parseFloat(thw.get(i).getTemperature_celsius());
                uhrzeit[i] = thw.get(i).getStarting_hour().substring(0, 5);
            }
        }
        RelativeLayout wetterChart = (RelativeLayout)findViewById(R.id.chartView);
        wetterChart.addView(new iniView(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void loadMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
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
            text.setTypeface(Typeface.create("Calibri",Typeface.NORMAL));
            text.setStyle(Paint.Style.STROKE);
            text.setAntiAlias(true);
            text.setColor(Color.BLACK);
            text.setTextSize(30);

            Paint anfangsText = new Paint(text); //text klonen und BOLD Typeface setzen mit einer etwas groesseren textgroesse
            anfangsText.setTypeface(Typeface.create("Calibri",Typeface.BOLD));
            //anfangsText.setStyle(Paint.Style.FILL);
            anfangsText.setTextSize(32);

            Paint achsen = new Paint();
            achsen.setAntiAlias(true);
            achsen.setColor(Color.BLACK);
            achsen.setStrokeWidth(3);

            abstand = 60-Math.abs(this.getMax())+Math.abs(this.getMin());// Je kleiner die Zahl, desto groesser der Abstand (wegen / abstand)

            minHeight = 0;
            if(this.getMin() < 0){
                minHeight = this.getMin()*height/abstand;
            }

            canvas.drawLine(70, 9*height/10 + 25, width * 7/8 + 85 , 9*height / 10 + 25,achsen); //x Achse
            canvas.drawLine(70, 9*height/10 + 25, 70,getPointHeight((float) Math.ceil(this.getMax() / 5)*5),achsen); //y Achse bis zum Maximum aufgerundet auf den nächste 5
            canvas.drawText("C°", 60, getPointHeight((float) Math.ceil(this.getMax() / 5)*5) - 50, text); // Achsenbeschriftung in C°

            canvas.drawText((int)this.getMin()+"", 20, getPointHeight(this.getMin())+25, anfangsText); //Kleinste Grad Anzahl Beschriftung
            canvas.drawText((int)this.getMax()+"", 20, getPointHeight(this.getMax())+25, anfangsText); //Groesste Grad Anzahl Beschriftung

            for(double i = Math.ceil(minHeight/height*abstand / 5)*5; i <= Math.ceil(this.getMax() / 5)*5; i+=5){ //Also entweder 0 oder minheight/height*abstand unter 0 zur nächsten 5
                if((this.getMax() - i > 1 || this.getMax() - i < -1) && (this.getMin() - i > 1 || this.getMin() - i < -1)) { //Wenn Maximum oder Minimum 1 oder 0 entfernt von dem geradigen Wert i ist, wird es nicht gezeichnet
                    canvas.drawText((int) i + "", 20, getPointHeight((float)i) + 25, text);
                }
            }
            float lastX = 0;
            float lastY = 0;
            for (int i = 0; i < temperaturen.length; i++){
                float left = (width/8)*i+72.5f;
                float top = getPointHeight(temperaturen[i]);
                float right = left+25;
                float bottom = top+25;
                canvas.drawOval(new RectF(left,top,right,bottom),paintOval); //Punkt zeichnen
                if(i!=0){
                    canvas.drawLine(lastX,lastY,left+15,top+15,paintLine); //Linie zwischen letzten und jetzigem Punkt, aber nicht bei dem 1. Punkt
                }
                lastX = left+15;
                lastY = top+15;
                Rect bounds = new Rect();
                text.getTextBounds(uhrzeit[i], 0, 1, bounds); //Groesse von text bekommen

                canvas.drawText(uhrzeit[i], (width / 8) * i + 40 - bounds.width()/2, height - height/10 + 80, text); //x Achsenbeschriftung mit der Uhrzeit
            }
        }

        public float getMax(){ //Maximalen Wert der Temperaturen bekommen
            float max = temperaturen[0];
            for(int i = 1; i < temperaturen.length; i++){
                if(temperaturen[i] > max){
                    max = temperaturen[i];
                }
            }
            return max;
        }

        public float getMin(){ //Minimalen Wert der Temperaturen bekommen
            float min = temperaturen[0];
            for(int i = 1; i < temperaturen.length; i++){
                if(temperaturen[i] < min){
                    min = temperaturen[i];
                }
            }
            return min;
        }

        private float getPointHeight(float point){
            return 9*height/10 - point * height / abstand + minHeight;
        }
    }
}
