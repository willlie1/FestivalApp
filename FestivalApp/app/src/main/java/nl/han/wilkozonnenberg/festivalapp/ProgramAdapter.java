package nl.han.wilkozonnenberg.festivalapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import nl.han.wilkozonnenberg.festivalapp.database.FestivalProvider;
import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;

/**
 * Created by Wilko on 10-11-16.
 */

public class ProgramAdapter extends CursorAdapter {


    private Cursor performances;

    private Activity activity;

    public ProgramAdapter(Activity activity, Cursor c, boolean autoRequery) {
        super(activity, c, autoRequery);

        performances = c;
        performances.moveToFirst();
        this.activity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.program_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        TextView txtstart = (TextView) rowView.findViewById(R.id.start_date_text);
        TextView txtend = (TextView) rowView.findViewById(R.id.end_date_text);
        TextView txtprice = (TextView) rowView.findViewById(R.id.price_text);

        txtTitle.setText(performances.getString(performances.getColumnIndex("title")));
        txtstart.setText(performances.getString(performances.getColumnIndex("start")));
        txtend.setText(performances.getString(performances.getColumnIndex("end")));
        txtprice.setText(performances.getString(performances.getColumnIndex("price")));

        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }


}
