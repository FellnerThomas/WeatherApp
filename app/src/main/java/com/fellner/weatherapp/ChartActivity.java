package com.fellner.weatherapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellner.fellner.weatherapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Fellner on 3/5/2015.
 */
public class ChartActivity extends AppCompatActivity {
    String city;
    String currentTemperatureText;
    String currentClimateText;
    int climateIconID;

    int width;
    int height;

    String[] timeOfDay;
    float[] temperatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Intent msgIntent = new Intent(this, LoadChartData.class);
        LoadChartData.ca = this;
        startService(msgIntent);

        Intent i = getIntent();
        city = i.getStringExtra("city");

        Toolbar chartToolbar = (Toolbar) findViewById(R.id.chartToolbar);
        chartToolbar.setTitle(city);
        setSupportActionBar(chartToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void loadContent(){
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
                            temperature.setText(currentTemperatureText);
                            climate.setText(currentClimateText);

                            climate.measure(0, 0);
                            //Tests if Image would be to far to the right
                            if (climate.getMeasuredWidth() < width/2) {
                                climateIcon.setX((int) (climate.getMeasuredWidth() * 1.2));
                                climateIcon.setY(climate.getTop() * 1.1f);
                            } else {
                                final float scale = getResources().getDisplayMetrics().density;
                                climateIcon.setY((110 * scale + 0.5f));
                                climateIcon.setX((20 * scale + 0.5f));
                            }
                            climateIcon.setImageResource(climateIconID);
                            findViewById(R.id.chartLoad).setVisibility(View.GONE);
                            wetterChart.addView(new iniView(getBaseContext()));
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public class iniView extends View {

        float distance;
        float minHeight;

        public iniView(Context context) {
            super(context);
            metrics = getContext().getResources().getDisplayMetrics();

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
            text.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
            text.setStyle(Paint.Style.STROKE);
            text.setAntiAlias(true);
            text.setColor(Color.BLACK);
            text.setTextSize(30);

            Paint minMaxText = new Paint(text); //clone the paint text with a bold Typeface and a little
            minMaxText.setTypeface(Typeface.create("Arial", Typeface.BOLD));
            minMaxText.setTextSize(32);

            Paint axis = new Paint();
            axis.setAntiAlias(true);
            axis.setColor(Color.BLACK);
            axis.setStrokeWidth(3);

            if(this.getMin() > 0){
                distance = (float) Math.ceil(this.getMax() / 5) * 5 * 3.5f; // the bigger the distance between maximum and minimum the bigger the distance constant
            }else{
                distance = (float) Math.ceil(this.getMax() / 5) * 5 + Math.abs(this.getMin()) * 3.5f;
            }

            minHeight = 0;
            if(this.getMin() < 0){
                minHeight = dp_to_pixels(this.getMin()*height/ distance)/3;
            }

            canvas.drawLine(dp_to_pixels(24), 7 * height / 10 + dp_to_pixels(8.5f), width * 7/8 + dp_to_pixels(28.5f) , 7 * height / 10 + dp_to_pixels(8.5f),axis); //x Achse
            canvas.drawLine(dp_to_pixels(24), 7 * height / 10 + dp_to_pixels(8.5f), dp_to_pixels(24), getPointHeight(this.getMax()  > 3 ? (float) Math.ceil(this.getMax() / 5) * 5 : this.getMax()), axis); //y Axis rounded up to the next 5

            //this deals with the y Axis text
            canvas.drawText("\u00b0C", dp_to_pixels(20), getPointHeight(this.getMax()  > 3 ? (float) Math.ceil(this.getMax() / 5) * 5 : this.getMax()) - dp_to_pixels(17), text); // Axis as Degree Celsius

            canvas.drawText((int) this.getMin() + "", dp_to_pixels(7), getPointHeight(this.getMin()) + dp_to_pixels(8.5f), minMaxText); //Minimum and Maximum as a bigger number
            canvas.drawText((int) this.getMax() + "", dp_to_pixels(7), getPointHeight(this.getMax()) + dp_to_pixels(8.5f), minMaxText);

            for(double i = Math.ceil(minHeight / height* distance / 5)*5; i <= (this.getMax()  > 3 ? (float) Math.ceil(this.getMax() / 5) * 5 : this.getMax()); i+=5){ //So either 0 or minheight/height*distance under 0 and then to the next 5
                if((this.getMax() - i > 2 || this.getMax() - i < -2) && (this.getMin() - i > 2 || this.getMin() - i < - 2)) { //If the number would be 1 or 0 away from maximum or minimum the number isn't drawn
                    canvas.drawText((int) i + "", dp_to_pixels(7), getPointHeight((float)i) + dp_to_pixels(8.5f), text);
                }
            }

            Calendar calendar = GregorianCalendar.getInstance();
            float currentTime = (float)8/(float)24 * (calendar.get(Calendar.HOUR_OF_DAY) - Integer.parseInt(timeOfDay[0].substring(0, 2)) + (float)calendar.get(Calendar.MINUTE)/(float)60);

            Paint triangle = new Paint();
            triangle.setStyle(Paint.Style.FILL);
            triangle.setStrokeWidth(10);
            triangle.setColor(Color.BLACK);
            triangle.setStyle(Paint.Style.FILL_AND_STROKE);
            triangle.setAntiAlias(true);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo((width / 8) * currentTime + dp_to_pixels(24), 7*height/10 + dp_to_pixels(13.5f));
            path.lineTo((width / 8) * currentTime + dp_to_pixels(26), 7*height/10 + dp_to_pixels(17));
            path.lineTo((width / 8) * currentTime + dp_to_pixels(22f), 7*height/10 + dp_to_pixels(17));
            path.close();

            canvas.drawPath(path, triangle);

            float lastX = 0;
            float lastY = 0;

            for (int i = 0; i < 8; i++){
                float left = (width/8)*i+dp_to_pixels(24);
                float top = getPointHeight(temperatures[i]);
                float right = left + dp_to_pixels(8.5f);
                float bottom = top + dp_to_pixels(8.5f);
                canvas.drawOval(new RectF(left,top,right,bottom),paintOval); //paint point

                if(i!=0){
                    canvas.drawLine(lastX, lastY, left + dp_to_pixels(5), top + dp_to_pixels(5), paintLine); //draw line from last point to current point except the first loop
                }

                lastX = left + dp_to_pixels(5);
                lastY = top + dp_to_pixels(5);
                Rect bounds = new Rect();
                text.getTextBounds(timeOfDay[i], 0, 1, bounds); //Groesse von text bekommen

                canvas.drawText(timeOfDay[i], (getWidth() / 8) * i + dp_to_pixels(13.5f) - bounds.width()/2, 7*height/10 + dp_to_pixels(27f), text); //x Achsenbeschriftung mit der Uhrzeit
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
            return 7*height/10 - dp_to_pixels(point)/3 * height / distance + minHeight;
        }

        DisplayMetrics metrics;
        private float dp_to_pixels(float dp){
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        }
    }
}
