package fellner.example.fellner.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Fellner on 18.12.14.
 */
public class SimpleArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resource;
    private final ArrayList<String> values;

    public SimpleArrayAdapter(Context context, int resource, ArrayList<String> values) {
        super(context, resource, values);
        this.context = context;
        this.resource = resource;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);

        TextView city = (TextView)rowView.findViewById(R.id.listview_item_city);
        city.setText(values.get(position));


        return rowView;
    }
}
