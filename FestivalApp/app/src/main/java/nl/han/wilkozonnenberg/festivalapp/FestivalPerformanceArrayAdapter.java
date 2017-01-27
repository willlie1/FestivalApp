package nl.han.wilkozonnenberg.festivalapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;
import nl.han.wilkozonnenberg.festivalapp.data.Performance;

import static android.content.ContentValues.TAG;

/**
 * Created by Wilko on 28-09-16.
 */

public class FestivalPerformanceArrayAdapter extends ArrayAdapter {
    private List<Performance> performances;
    private Activity context;


    public FestivalPerformanceArrayAdapter(Activity context, int resource, List objects) {
        super(context, resource, objects);
        this.performances = (List<Performance>) objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Performance performance = performances.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.festival_performance_item, null, true);
        TextView startdate = (TextView) rowView.findViewById(R.id.start_date_text);
        TextView enddate = (TextView) rowView.findViewById(R.id.end_date_text);

        System.out.println(performance.price);
        TextView price = (TextView) rowView.findViewById(R.id.price_text);

        startdate.setText(performance.start.toString());
        enddate.setText(performance.end.toString());
        price.setText(Double.toString(performance.price));

        return rowView;
    }
}
