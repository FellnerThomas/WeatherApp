package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Fellner on 6/11/2015.
 */
public class CityActivity extends Activity {
    ArrayList<String> values = new ArrayList<String>();
    ArrayAdapter adapter;

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        final ListView lv = (ListView) this.findViewById(R.id.cities);

        adapter = new ArrayAdapter(this, R.layout.list_item, R.id.cityname, values);
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

        EditText search = (EditText) findViewById(R.id.search_bar);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                CityActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //this will read the contents of file city.list and save it in the arraylist
            InputStream is = getAssets().open("city.list");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            s = br.readLine();
            while (s != null) {
                values.add(s);
                s = br.readLine();
            }
            br.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        values.clear();
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

    public class CityAddThread implements Runnable {
        String s;

        public CityAddThread(String s) {
            this.s = s;
        }

        public void run() {
            values.add(s);
            adapter.notifyDataSetChanged();
        }
    }
}
