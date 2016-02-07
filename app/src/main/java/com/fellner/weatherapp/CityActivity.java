package com.fellner.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.fellner.fellner.weatherapp.R;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Fellner on 6/11/2015.
 */
public class CityActivity extends AppCompatActivity {
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
                    outputStream.write((s + ";").getBytes());
                    Intent newActivity = new Intent(CityActivity.this, MainActivity.class);
                    startActivity(newActivity);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Toolbar cityToolbar = (Toolbar) findViewById(R.id.cityToolbar);
        cityToolbar.setTitle("Add City");
        setSupportActionBar(cityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        getMenuInflater().inflate(R.menu.menu_city, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                ListView lv = (ListView) findViewById(R.id.cities);
                lv.setSelectionAfterHeaderView();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
}