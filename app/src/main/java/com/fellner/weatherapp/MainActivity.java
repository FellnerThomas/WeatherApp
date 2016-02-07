package com.fellner.weatherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fellner.fellner.weatherapp.R;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private ArrayList<String> values;
    private SimpleArrayAdapter itemAdapter;

    public ArrayList<String> getValues(){
        return values;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //this is so u can write to files
        StrictMode.setThreadPolicy(policy);

        values = new ArrayList<String>();

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("cities",Context.MODE_APPEND); //open or create file
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        itemAdapter = new SimpleArrayAdapter(this,R.layout.listview_item,values);
        final ListView lv = (ListView)this.findViewById(R.id.listview);
        lv.setAdapter(itemAdapter);

        FileInputStream fIn = null;

        try {
            fIn = openFileInput("cities");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(fIn);
        char[] output = new char[256];
        try {
            isr.read(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add items to from file if
        if(new String(output).contains(";")) {
            values.addAll(Arrays.asList(new String(output).substring(0, new String(output).length()).split(";")));
            values.remove(values.size() - 1); //remove last item because it would be empty
        }

        itemAdapter.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //ItemClickListener hinzufuegen
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newActivity = new Intent(MainActivity.this, ChartActivity.class);
                newActivity.putExtra("city", (String) lv.getItemAtPosition(i));
                startActivity(newActivity);
            }
        });

        //long press equals a Deletion
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Delete " + lv.getItemAtPosition(pos) + "?");
                adb.setMessage("Are you sure you want to delete " + lv.getItemAtPosition(pos) + "?");
                final int positionToRemove = pos;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        values.remove(positionToRemove);
                        itemAdapter.notifyDataSetChanged();
                    }
                });
                adb.show();

                return true;
            }
        });

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(lv),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                values.remove(position);
                                itemAdapter.notifyDataSetChanged();
                            }
                        });
        lv.setOnTouchListener(touchListener);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        mainToolbar.setTitle("Cities");
        setSupportActionBar(mainToolbar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //onstop writes the values array to a file so it can be read on next startup
        String out = "";
        for (String s : values) {
            out += s + ";";
        }
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("cities", Context.MODE_PRIVATE);//File Erstellen
            outputStream.write(out.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        switch (item.getItemId()) {
            case R.id.action_addcity:
                Intent newActivity = new Intent(MainActivity.this, CityActivity.class);
                startActivity(newActivity);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
