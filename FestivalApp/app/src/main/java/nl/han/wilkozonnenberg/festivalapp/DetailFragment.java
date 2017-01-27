package nl.han.wilkozonnenberg.festivalapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import java.util.List;

import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;
import nl.han.wilkozonnenberg.festivalapp.data.Performance;
import nl.han.wilkozonnenberg.festivalapp.database.FestivalProvider;

import static android.content.ContentValues.TAG;

public class DetailFragment extends Fragment {
    private List<Performance> performances;
    private FestivalEvent festivalEvent;
    private View rootView;
    boolean mDualPane;


    public void setFestivalEvent(FestivalEvent event){
        festivalEvent = event;
        updateUI();
    }

    public void initDetailFragment(FestivalEvent event){
        festivalEvent = event;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        festivalEvent = null;
        performances = null;
        rootView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a support ActionBar corresponding to this toolbar

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_Festival);
//        if(festivalArray != null) {
//            adapter = new
//                    FestivalEventArrayAdapter(getActivity(), festivalArray.size(), festivalArray);
//            listView.setAdapter(adapter);
//        }

        if(festivalEvent != null) {
            updateUI();

        }
        setLongPressListener();


        return rootView;
    }



    private void setLongPressListener(){
        ListView listView = (ListView) rootView.findViewById(R.id.listview_Festival);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                ContentValues values = new ContentValues(1);
                String performance_id = performances.get(pos).id;
                values.put("performance_id", performance_id);
                int i = getContext().getContentResolver()
                        .query(FestivalProvider.USER_PROGRAM_ID_CONTENT_URI,null,
                                "performance_id = '" + performance_id + "'"
                                ,null,null).getCount();
                if(i == 0) {

                    getContext().getContentResolver()
                            .insert(FestivalProvider.USER_PROGRAM_CONTENT_URI, values);
                    Toast toast = Toast.makeText(getContext(),
                            "Performance is added to your personal list.", Toast.LENGTH_SHORT);
                    toast.show();
                    if(mDualPane){
                        refreshMyProgram();
                    }

                }else{
                    Toast toast = Toast.makeText(getContext(),
                            "Performance is already added to your personal list.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
            }
        });
    }

    private void refreshMyProgram(){
    if(mDualPane) {
//        new MyAsyncTask().execute();
        Context context = getContext();
        ProgramFragment programFragment = (ProgramFragment) getFragmentManager().findFragmentById(R.id.program_fragment);
        if (programFragment == null) {
            // Make new fragment to show this selection.
            programFragment = new ProgramFragment();
            // Execute a transaction, replacing any existing
            // fragment with this one inside the frame.
            FragmentTransaction ft
                    = getFragmentManager().beginTransaction();
            ft.replace(R.id.detail_fragment, programFragment);
            ft.setTransition(
                    FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

        programFragment.refresh();
    }

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            refreshMyProgram();
            return null;
        }
        ProgramFragment programFragment;
        private void refreshMyProgram(){
            Context context = getContext();
             programFragment = (ProgramFragment) getFragmentManager().findFragmentById(R.id.program_fragment);
            if (programFragment == null) {
                // Make new fragment to show this selection.
                programFragment = new ProgramFragment();
                // Execute a transaction, replacing any existing
                // fragment with this one inside the frame.
                FragmentTransaction ft
                        = getFragmentManager().beginTransaction();
                ft.replace(R.id.detail_fragment, programFragment);
                ft.setTransition(
                        FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }


        }
        @Override
        protected void onPostExecute(Void result) {
            programFragment.refresh();

        }
    }

    private void setImageView() {
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);

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
        RequestQueueSingleton.getInstance(getContext()).getImageLoader()
                .get("http:" + festivalEvent.getLargeUrl(), imageListener);
    }

    private void setTextViews() {
        TextView titleView = (TextView) rootView.findViewById(R.id.title_view);
        TextView descriptionView = (TextView) rootView.findViewById(R.id.description);

        titleView.setText(festivalEvent.title);
        this.performances = festivalEvent.performances;
        descriptionView.setText(festivalEvent.description);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareFestival();
            return true;
        }else{
            NavUtils.navigateUpFromSameTask(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    private int getRating(){
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rate_bar);
        return (int) ratingBar.getRating();
    }

    public void shareFestival(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ik geef het festival \"" + festivalEvent.title + "\" een " + getRating() + "/5." );
        shareIntent.setType("text/plain");
//        setShareIntent(shareIntent);
        startActivity(shareIntent);
    }

//    private void setShareIntent(Intent shareIntent) {
//        if (mShareActionProvider != null) {
//            mShareActionProvider.set(shareIntent);
//        }
//    }

    private void updateUI(){

        setTextViews();
        setImageView();

        FestivalPerformanceArrayAdapter adapter = new
                FestivalPerformanceArrayAdapter(getActivity(), performances.size(), performances);


        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_Festival);
        listView.setAdapter(adapter);
    }

}
