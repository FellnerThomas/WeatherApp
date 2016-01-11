package fellner.example.fellner.weatherapp;

import android.app.Activity;
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
    public static String city;

    public static String[] timeOfDay;
    public static float[] temperatures;

    float abstand;
    float minHeight;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        DailyWeather dw;
        ArrayList<ThreeHourlyWeather> thw = null;

        String appid = "";
        try {
            //Saves Document using Jsoup from URL and saves all a tags containing href
            Document doc = Jsoup.connect("http://openweathermap.org/current").get();
            Elements aTags = doc.select("a[href]");

            String wholeappid = "";

            //loops through the a tags and stops when the href contains an appid
            for (Element aTag : aTags) {
                String aTagString = aTag.attr("abs:href");

                if(aTagString.contains("&appid=")){
                    wholeappid = aTagString;
                    break;
                }
            }

            appid = wholeappid.split("&appid=")[1];
            String url = "http://api.openweathermap.org/data/2.5/forecast?q="+city+"&mode=xml&appid="+appid;


            dw = FetchWeatherData.fetchIt(url);
            thw = dw.getThreeHourlyWeatherData();
        }catch(Exception e) {
            e.printStackTrace();
            Intent newActivity = new Intent(this, MainActivity.class);
            Toast.makeText(getApplicationContext(),"City non existent", Toast.LENGTH_LONG).show();
            startActivity(newActivity);
        }
        if (thw != null) {
            temperatures = new float[thw.size()];
            timeOfDay = new String[thw.size()];
            for (int i = 0; i < 8; i++) {
                temperatures[i] = Float.parseFloat(thw.get(i).getTemperature_celsius());
                timeOfDay[i] = thw.get(i).getStarting_hour().substring(0, 5);
            }

        }


        TextView cityText = (TextView)this.findViewById(R.id.city);
        cityText.setText(city);
        TextView temperature = (TextView)this.findViewById(R.id.temperature);
        TextView climate = (TextView)this.findViewById(R.id.climate);
        ImageView climateIcon = (ImageView)this.findViewById(R.id.climateIcon);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            org.w3c.dom.Document document = db.parse("http://api.openweathermap.org/data/2.5/weather?q="+city+"&mode=xml&appid="+appid);

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            String currentTemperatureText = Double.toString(Math.ceil((Double.parseDouble(nodeList.item(1).getAttributes().item(0).getNodeValue()) - 273.15) * 100) / 100) + "C°";
            String currentClimateText = nodeList.item(8).getAttributes().item(1).getNodeValue();
            temperature.setText(currentTemperatureText);
            climate.setText(currentClimateText);

            climate.measure(0, 0);
            cityText.measure(0, 0);

            Toast.makeText(getApplicationContext(),""+climate.getMeasuredWidth(), Toast.LENGTH_LONG).show();

            if(climate.getMeasuredWidth() > cityText.getMeasuredWidth()){
                climateIcon.setX((int) (climate.getMeasuredWidth() * 1.2));
            } else {
                climateIcon.setX((int) (cityText.getMeasuredWidth() * 1.2));
            }
            int id = getResources().getIdentifier("i"+nodeList.item(8).getAttributes().item(2).getNodeValue(), "drawable", getPackageName());
            climateIcon.setImageResource(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RelativeLayout wetterChart = (RelativeLayout)findViewById(R.id.chartView);
        wetterChart.addView(new iniView(getApplicationContext()));
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
            //anfangsText.setStyle(Paint.Style.FILL);
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

            float currentTempY = temperatures[(int)currentTime] * (currentTime-(int)currentTime) + temperatures[(int)currentTime+1] * (1-(currentTime-(int)currentTime)); // average between nearest temperatures weighted by how near the temperatures are

            float currentTimeX = (width/8)*currentTime+72.5f;

            Paint triangle = new Paint();
            triangle.setStyle(Paint.Style.FILL);
            triangle.setStrokeWidth(10);
            triangle.setColor(Color.BLACK);
            triangle.setStyle(Paint.Style.FILL_AND_STROKE);
            triangle.setAntiAlias(true);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo((width / 8) * currentTime + 72.5f, getPointHeight(currentTempY));//7 * height / 10 + 40);
            path.lineTo((width / 8) * currentTime + 77.5f, getPointHeight(currentTempY) - 11);//7 * height / 10 + 51);
            path.lineTo((width / 8) * currentTime + 67.5f, getPointHeight(currentTempY)-11); //7 * height / 10 + 51);
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
