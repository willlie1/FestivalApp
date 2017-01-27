package nl.han.wilkozonnenberg.festivalapp;

import android.app.Activity;
import android.support.annotation.NonNull;
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

import static android.content.ContentValues.TAG;

/**
 * Created by Wilko on 28-09-16.
 */

class FestivalEventArrayAdapter extends ArrayAdapter {
    private List<FestivalEvent> festivalEvents;
    private Activity context;


    FestivalEventArrayAdapter(Activity context, int resource, List<FestivalEvent> objects) {
        super(context, resource, objects);
        festivalEvents = objects;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        FestivalEvent festivalEvent = festivalEvents.get(position);
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.festival_event_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);

        final ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        txtTitle.setText(festivalEvent.title);

        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                imageView.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        };
        RequestQueueSingleton.getInstance(getContext()).getImageLoader().get("http:" + festivalEvent.getThumbUrl(), imageListener);

        return rowView;
    }
}
