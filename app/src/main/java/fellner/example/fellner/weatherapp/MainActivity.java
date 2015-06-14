package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends Activity {
    private ArrayList<String> values = new ArrayList<String>();
    private SimpleArrayAdapter itemAdapter;

    public ArrayList<String> getValues(){
        return values;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("cities",Context.MODE_APPEND);//File Erstellen
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        itemAdapter = new SimpleArrayAdapter(this,R.layout.listview_item,values); //Initialisieren des SimpleArrayAdapters
        final ListView listView = (ListView)this.findViewById(R.id.listview); //ListView bekommen

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

        if(new String(output).contains("|")) {
            values.addAll(Arrays.asList(new String(output).substring(0, new String(output).length()).split("\\|")));
            values.remove(values.size()-1);
        }

        Toast.makeText(this,this.getFilesDir().toString(), Toast.LENGTH_LONG).show();

        listView.setOnTouchListener(new AdapterView.OnTouchListener() {
            int index;
            View child;
            float x1;
            float x2;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getRawX();
                        Rect rect = new Rect();
                        int childCount = listView.getChildCount();
                        int[] listViewCoords = new int[2];
                        listView.getLocationOnScreen(listViewCoords);
                        int x = (int) event.getRawX() - listViewCoords[0];
                        int y = (int) event.getRawY() - listViewCoords[1];
                        for (int i = 0; i < childCount; i++) {
                            child = listView.getChildAt(i);
                            child.getHitRect(rect);
                            if (rect.contains(x, y)) {
                                index = i;
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > 500) {
                            values.remove(index);
                            itemAdapter.notifyDataSetChanged();
                            break;
                        } else if (Math.abs(deltaX) < 100) {
                            Intent newActivity = new Intent(MainActivity.this, ChartActivity.class);
                            ChartActivity.city = (String) listView.getItemAtPosition(index);
                            startActivity(newActivity);
                        }else {
                            child.setX(0);
                        }
                        break;
                case MotionEvent.ACTION_MOVE:
                child.setX(event.getRawX() - x1);
            }

            return true;
        }
    });

        listView.setAdapter(itemAdapter); //setzen des adapters
    }

    @Override
    protected void onStop() {
        super.onStop();
        String out = "";
        for(String s : values){
            out += s+"|";
        }
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("cities",Context.MODE_PRIVATE);//File Erstellen
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newCity(final View v){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add City with [City,Countrycode]");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                values.add(input.getText().toString());
                // catches IOException below
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput("cities",Context.MODE_APPEND);
                    outputStream.write((input.getText().toString()+"|").getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();*/
        Intent newActivity = new Intent(MainActivity.this, CityActivity.class);
        startActivity(newActivity);
    }
    public void delItems(final View v){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("cities",Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ListView listView = (ListView)this.findViewById(R.id.listview);
        itemAdapter.notifyDataSetChanged();
        values.clear();
    }
}
