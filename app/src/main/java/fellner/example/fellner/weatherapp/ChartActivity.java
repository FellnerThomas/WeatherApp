package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import WeatherParser.DailyWeather;
import WeatherParser.FetchWeatherData;
import WeatherParser.ThreeHourlyWeather;

/**
 * Created by Fellner on 3/5/2015.
 */
public class ChartActivity extends Activity{
    String city;
    String currentTemperatureText;
    String currentClimateText;
    int climateIconID;

    String[] timeOfDay;
    float[] temperatures;

    float abstand;
    float minHeight;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent i = getIntent();
        city = i.getStringExtra("city");

        Intent msgIntent = new Intent(this, IntentHandler.class);
        IntentHandler.ca = this;
        startService(msgIntent);
    }

    public void loadContent(){
        final TextView cityText = (TextView)this.findViewById(R.id.city);
        final TextView temperature = (TextView)this.findViewById(R.id.temperature);
        final TextView climate = (TextView)this.findViewById(R.id.climate);
        final ImageView climateIcon = (ImageView)this.findViewById(R.id.climateIcon);




        final RelativeLayout wetterChart = (RelativeLayout)this.findViewById(R.id.chartView);

        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            cityText.setText(city);
                            temperature.setText(currentTemperatureText);
                            climate.setText(currentClimateText);

                            climate.measure(0, 0);
                            cityText.measure(0, 0);
                            //Tests if Image would be to far to the right
                            if (climate.getMeasuredWidth() < width/2 &&  cityText.getMeasuredWidth() < width/2) {
                                if (climate.getMeasuredWidth() > cityText.getMeasuredWidth()) {
                                    climateIcon.setX((int) (climate.getMeasuredWidth() * 1.2));
                                    climateIcon.setY(20);
                                } else {
                                    climateIcon.setX((int) (cityText.getMeasuredWidth() * 1.2));
                                    climateIcon.setY(20);
                                }
                            } else {
                                final float scale = getResources().getDisplayMetrics().density;
                                climateIcon.setY((110 * scale + 0.5f));
                                climateIcon.setX((20 * scale + 0.5f));
                            }
                            climateIcon.setImageResource(climateIconID);
                            findViewById(R.id.chartLoad).setVisibility(View.GONE);
                            wetterChart.addView(new iniView(getApplicationContext()));
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void loadMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    public class iniView extends View {

        public iniView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paintLine = new Paint();
            paintLine.setStyle(Paint.Style.STROKE);
            paintLine.setAntiAlias(true);
            paintLine.setStrokeWidth(8);
            paintLine.setColor(Color.RED);

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
            anfangsText.setTextSize(32);

            Paint axis = new Paint();
            axis.setAntiAlias(true);
            axis.setColor(Color.BLACK);
            axis.setStrokeWidth(3);

            abstand = 60-Math.abs(this.getMax())+Math.abs(this.getMin());// Je kleiner die Zahl, desto groesser der Abstand (wegen / abstand)

            minHeight = 0;
            if(this.getMin() < 0){
                minHeight = this.getMin()*height/abstand;
            }

            canvas.drawLine(70, 7 * height / 10 + 25, width * 7/8 + 85 , 7 * height / 10 + 25,axis); //x Achse
            canvas.drawLine(70, 7 * height / 10 + 25, 70, getPointHeight((float) Math.ceil(this.getMax() / 5) * 5), axis); //y Achse bis zum Maximum aufgerundet auf den nächste 5
            canvas.drawText("C°", 60, getPointHeight((float) Math.ceil(this.getMax() / 5)*5) - 50, text); // Achsenbeschriftung in C°

            canvas.drawText((int)this.getMin()+"", 20, getPointHeight(this.getMin())+25, anfangsText); //Kleinste Grad Anzahl Beschriftung
            canvas.drawText((int) this.getMax() + "", 20, getPointHeight(this.getMax()) + 25, anfangsText); //Groesste Grad Anzahl Beschriftung

            for(double i = Math.ceil(minHeight / height*abstand / 5)*5; i <= Math.ceil(this.getMax() / 5)*5; i+=5){ //Also entweder 0 oder minheight/height*abstand unter 0 zur nächsten 5
                if((this.getMax() - i > 1 || this.getMax() - i < -1) && (this.getMin() - i > 1 || this.getMin() - i < -1)) { //Wenn Maximum oder Minimum 1 oder 0 entfernt von dem geradigen Wert i ist, wird es nicht gezeichnet
                    canvas.drawText((int) i + "", 20, getPointHeight((float)i) + 25, text);
                }
            }


            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            int minutes = calendar.get(Calendar.MINUTE);
            float currentTime = (float)8/(float)24 * (calendar.get(Calendar.HOUR_OF_DAY) - Integer.parseInt(timeOfDay[0].substring(0, 2)) + (float)calendar.get(Calendar.MINUTE)/(float)60);

            float currentTimeX = (width/8)*currentTime+72.5f;

            Paint triangle = new Paint();
            triangle.setStyle(Paint.Style.FILL);
            triangle.setStrokeWidth(10);
            triangle.setColor(Color.BLACK);
            triangle.setStyle(Paint.Style.FILL_AND_STROKE);
            triangle.setAntiAlias(true);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo((width / 8) * currentTime + 72.5f, 7*height/10 + 40);
            path.lineTo((width / 8) * currentTime + 77.5f, 7*height/10 + 51);
            path.lineTo((width / 8) * currentTime + 67.5f, 7*height/10 + 51);
            path.close();

            canvas.drawPath(path, triangle);

            float lastX = 0;
            float lastY = 0;

            for (int i = 0; i < 8; i++){
                float left = (width/8)*i+72.5f;
                float top = getPointHeight(temperatures[i]);
                float right = left+25;
                float bottom = top+25;
                canvas.drawOval(new RectF(left,top,right,bottom),paintOval); //paint point

                if(i!=0){
                    canvas.drawLine(lastX,lastY,left+15,top+15,paintLine); //draw line from last point to current point except the first loop
                }

                lastX = left+15;
                lastY = top+15;
                Rect bounds = new Rect();
                text.getTextBounds(timeOfDay[i], 0, 1, bounds); //Groesse von text bekommen

                canvas.drawText(timeOfDay[i], (getWidth() / 8) * i + 40 - bounds.width()/2, 7*height/10 + 80, text); //x Achsenbeschriftung mit der Uhrzeit
            }
        }

        public float getMax(){ //Maximalen Wert der Temperaturen bekommen
            float max = temperatures[0];
            for(int i = 1; i < 8; i++){
                if(temperatures[i] > max){
                    max = temperatures[i];
                }
            }
            return max;
        }

        public float getMin(){ //Minimalen Wert der Temperaturen bekommen
            float min = temperatures[0];
            for(int i = 1; i < 8; i++){
                if(temperatures[i] < min){
                    min = temperatures[i];
                }
            }
            return min;
        }

        private float getPointHeight(float point){
            return 7*height/10 - point * height / abstand + minHeight;
        }
    }
}
