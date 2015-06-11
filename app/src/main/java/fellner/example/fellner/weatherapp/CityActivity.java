package fellner.example.fellner.weatherapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Fellner on 6/11/2015.
 */
public class CityActivity extends Activity{
    ArrayList<String> values = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
        SimpleArrayAdapter adapter = new SimpleArrayAdapter(this,R.layout.listview_item,values);
    // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.activity_list_item);
    // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
}
