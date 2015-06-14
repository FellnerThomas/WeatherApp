package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Fellner on 6/11/2015.
 */
public class CityActivity extends Activity{
    ArrayList<String> values = new ArrayList<String>();
    SimpleArrayAdapter adapter;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        final ListView lv = (ListView)this.findViewById(R.id.cities);
        values.add("Vienna,at");
        values.add("2");
        values.add("3");
        values.add("4");
        values.add("5");
        adapter = new SimpleArrayAdapter(this,R.layout.listview_item,values);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //ItemClickListener hinzufuegen
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput("cities", Context.MODE_APPEND);//File Erstellen
                    String s = (String) lv.getItemAtPosition(i);
                    outputStream.write((s + "|").getBytes());
                    Intent newActivity = new Intent(CityActivity.this, MainActivity.class);
                    startActivity(newActivity);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
