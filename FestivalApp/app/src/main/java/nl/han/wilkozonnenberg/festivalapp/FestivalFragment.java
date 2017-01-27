package nl.han.wilkozonnenberg.festivalapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import nl.han.wilkozonnenberg.festivalapp.data.Performance;
import nl.han.wilkozonnenberg.festivalapp.database.FestivalDBHelper;
import nl.han.wilkozonnenberg.festivalapp.database.FestivalProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;

import static android.content.ContentValues.TAG;


public class FestivalFragment extends Fragment {
    private final String LOG_TAG = FestivalFragment.class.getSimpleName();

    private FestivalEventArrayAdapter adapter;
    private List <FestivalEvent> festivalArray;

    public FestivalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.festivalfragment, menu);
        initSortButton(menu);
        initFilterButton(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            executeRequest();
            return true;

        }
        else if(id == R.id.action_program) {
            showOwnProgram();
            return true;
        }
        else if(id == R.id.action_share){
            if(mDualPane){
                if(detailFragment != null){
                    detailFragment.shareFestival();
                }
            }
        }
        else {
            executeDropDownAction(id);
        }


        return super.onOptionsItemSelected(item);
    }

    private void showOwnProgram(){
        Intent intent = new Intent(getActivity(), ProgramActivity.class);
        startActivity(intent);
    }

    private void executeDropDownAction(int id) {
        switch (id){
            case R.string.date_asc:
                sortFestivalArray(new FestivalAscDateComparator());
                updateUI(festivalArray);
                break;
            case R.string.date_desc:
                sortFestivalArray(new FestivalDescDateComparator());
                updateUI(festivalArray);
                break;
            case R.string.title_asc:
                sortFestivalArray(new FestivalAscTitleComparator());
                updateUI(festivalArray);
                break;
            case R.string.title_desc:
                sortFestivalArray(new FestivalDescTitleComparator());
                updateUI(festivalArray);
                break;
            case R.string.genre_filter_film:
                filterOnGenre("Film");
                break;
            case R.string.genre_filter_event:
                filterOnGenre("Event");
                break;
            case R.string.genre_filter_none:
                updateUI(festivalArray);
                break;
        }
    }

    private void filterOnGenre(String genre) {
        ArrayList<FestivalEvent> festivalEvents = new ArrayList<>();

        if (genre != null) {
            for (FestivalEvent festivalEvent : festivalArray) {
                if (festivalEvent.genre != null && festivalEvent.genre.equals(genre)) {
                    festivalEvents.add(festivalEvent);
                }
            }
            System.out.println("Size of array :    "+festivalEvents.size());
            updateUI(festivalEvents);
        }else{
            updateUI(festivalArray);
        }
    }

    private void initFilterButton(Menu menu){
        MenuItem filterButton = menu.findItem(R.id.action_filter);
        if (filterButton != null && filterButton.hasSubMenu()) {
            filterButton.getSubMenu().add(0,R.string.genre_filter_none, Menu.NONE, R.string.genre_filter_none);
            filterButton.getSubMenu().add(0,R.string.genre_filter_event, Menu.NONE,R.string.genre_filter_event);
            filterButton.getSubMenu().add(0,R.string.genre_filter_film, Menu.NONE, R.string.genre_filter_film);
        }
    }

    private void initSortButton(Menu menu){
        MenuItem sortButton = menu.findItem(R.id.action_sort);
        if (sortButton != null && sortButton.hasSubMenu()) {
            sortButton.getSubMenu().add(0,R.string.date_asc, Menu.NONE,R.string.date_asc);
            sortButton.getSubMenu().add(0,R.string.date_desc, Menu.NONE, R.string.date_desc);
            sortButton.getSubMenu().add(0,R.string.title_asc, Menu.NONE, R.string.title_asc);
            sortButton.getSubMenu().add(0,R.string.title_desc, Menu.NONE, R.string.title_desc);

        }
    }

    private void updateUI(List<FestivalEvent> array){



         adapter = new
                FestivalEventArrayAdapter(getActivity(), array.size(), array);


        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_Festival);
        listView.setAdapter(adapter);
    }

    private void sortFestivalArray(Comparator<FestivalEvent> comparator){
        Collections.sort(festivalArray, comparator);
        updateUI(festivalArray);
    }

    public class FestivalAscDateComparator implements Comparator<FestivalEvent> {

        @Override
        public int compare(FestivalEvent o1, FestivalEvent o2) {
            return o1.performances.get(0).start.before(o2.performances.get(0).start) ? -1 : 1;
        }
    }
    public class FestivalDescDateComparator implements Comparator<FestivalEvent> {

        @Override
        public int compare(FestivalEvent o1, FestivalEvent o2) {
            return o1.performances.get(0).start.before(o2.performances.get(0).start) ? 1 : -1;
        }
    }

    public class FestivalAscTitleComparator implements Comparator<FestivalEvent> {

        @Override
        public int compare(FestivalEvent o1, FestivalEvent o2) {
            return o1.title.compareTo(o2.title);
        }
    }
    public class FestivalDescTitleComparator implements Comparator<FestivalEvent> {

        @Override
        public int compare(FestivalEvent o1, FestivalEvent o2) {
            return o2.title.compareTo(o1.title);
        }
    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_Festival);
        if(festivalArray != null) {
            adapter = new
                    FestivalEventArrayAdapter(getActivity(), festivalArray.size(), festivalArray);
            listView.setAdapter(adapter);
        }



        getFestivals();

        return rootView;
    }


    boolean mDualPane;
    DetailFragment detailFragment;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_Festival);


        View detailsFrame = getActivity().findViewById(R.id.detail_fragment);
        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;


        if(festivalArray != null) {
            adapter = new
                    FestivalEventArrayAdapter(getActivity(), festivalArray.size(), festivalArray);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                FestivalEvent event = (FestivalEvent) adapter.getItem(i);
                if(mDualPane){
                    detailFragment.setFestivalEvent(event);
                }
                else {
                    Intent intent = new Intent(getContext(), DetailActivity.class).putExtra("FestivalEvent", event);
                    startActivity(intent);

                }

            }
        });

        if(mDualPane){
            detailFragment = (DetailFragment)getFragmentManager().findFragmentById(R.id.detail_fragment);
            if (detailFragment == null) {
                // Make new fragment to show this selection.
                detailFragment = new DetailFragment();



                detailFragment.mDualPane = mDualPane;
                // Execute a transaction, replacing any existing
                // fragment with this one inside the frame.
                FragmentTransaction ft
                        = getFragmentManager().beginTransaction();
                ft.replace(R.id.detail_fragment, detailFragment);
                ft.setTransition(
                        FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                if(festivalArray != null) {
                    if (festivalArray.size() != 0) {
                        detailFragment.initDetailFragment(festivalArray.get(0));
                    }
                }
            }

            ProgramFragment programFragment = (ProgramFragment) getFragmentManager().findFragmentById(R.id.program_fragment);
            if (programFragment == null) {
                // Make new fragment to show this selection.
                programFragment = new ProgramFragment();



                // Execute a transaction, replacing any existing
                // fragment with this one inside the frame.
                FragmentTransaction ft
                        = getFragmentManager().beginTransaction();
                ft.replace(R.id.program_fragment, programFragment);
                ft.setTransition(
                        FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }
    }

    private String buildApiUrl(int fromAmount) {

        String apiKey = "xVq7GyuekHqOHWoV";
        String secretKey = "zHM8WeNLlBoCjLcCdPvRwlN8awc4HLUA";
        String signature = null;

        final String FESTIVAL_BASE_URL =
                "http://api.edinburghfestivalcity.com";

        final String BASE_PATH = "events";
        final String FESTIVAL_PARAM = "festival";
        final String KEY_PARAM = "key";
        final String SIGNATURE_PARAM = "signature";
//        final String CODE_PARAMETER = "code";
        final String PRETTY_PARAM = "pretty";
        final String SIZE = "size";
        final String FROM = "from";
//        final String GENRE = "genre";
        final String APPID_PARAM = "APPID";

        //First construct the Uri without the signature



        Uri unsignedUri = Uri.parse(FESTIVAL_BASE_URL).buildUpon()
                .appendPath(BASE_PATH)
                .appendQueryParameter(FESTIVAL_PARAM, "film")
                .appendQueryParameter(PRETTY_PARAM, "1")
                .appendQueryParameter(SIZE, "100")
                .appendQueryParameter(FROM, String.valueOf(fromAmount))
                .appendQueryParameter(KEY_PARAM, apiKey)
                .appendQueryParameter(APPID_PARAM, BuildConfig.APPLICATION_ID)
                .build();

        //Extract the part we need to generate a signature
        String unsignedQuery = unsignedUri.getPath() + "?" + unsignedUri.getQuery();

        try {
            signature = createFestivalSignature("HmacSHA1", unsignedQuery, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }

        final String signedUrl = unsignedUri.buildUpon()
                .appendQueryParameter(SIGNATURE_PARAM, signature)
                .build()
                .toString();


        Log.v(LOG_TAG, "Built URI " + signedUrl);
        return signedUrl;
    }

    private String createFestivalSignature(String cryptoAlgorithm, String unsignedQuery, String secretKey)
            throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {


        Mac mac = Mac.getInstance(cryptoAlgorithm);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        mac.init(secret);
        byte[] digest = mac.doFinal(unsignedQuery.getBytes());

        //Convert digest to a ASCII hex string (courtesy of
        //http://stackoverflow.com/questions/15429257/how-to-convert-byte-array-to-hexstring-in-java)
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private void getFestivals(){
//        if (isOnline()){
//            executeRequest();
//        }
//        else{
            getFestivalsFromDatabase();
//        }
    }

    private void getFestivalsFromDatabase(){
        Cursor festivalsCursor = getContext().getContentResolver().query(FestivalProvider.FESTIVALS_CONTENT_URI,null,null,null,null);
        if(festivalsCursor.getCount() > 0) {
            festivalsCursor.moveToFirst();
            ArrayList<FestivalEvent> festivals = new ArrayList<>();
            while (!festivalsCursor.isAfterLast()) {
                FestivalEvent festivalEvent = new FestivalEvent();
                festivalEvent.title = festivalsCursor.getString(festivalsCursor.getColumnIndex("title"));
                festivalEvent.description = festivalsCursor.getString(festivalsCursor.getColumnIndex("description"));
                festivalEvent.genre = festivalsCursor.getString(festivalsCursor.getColumnIndex("genre"));
                festivalEvent.images = new FestivalEvent.Image[1];
                festivalEvent.images[0] = new FestivalEvent.Image("thumb", getImageVersions(festivalsCursor));
                festivalEvent.latitude = Double.parseDouble(festivalsCursor.getString(festivalsCursor.getColumnIndex("latitude")));
                festivalEvent.longitude = Double.parseDouble(festivalsCursor.getString(festivalsCursor.getColumnIndex("longitude")));
                festivalEvent.performances = getPerformancesFromDatabase(festivalsCursor.getString(festivalsCursor.getColumnIndex("_id")));
                festivals.add(festivalEvent);

                festivalsCursor.moveToNext();
            }
            festivalsCursor.close();
            festivalArray = festivals;
            updateUI(festivalArray);
        }
    }

    private ArrayList<Performance> getPerformancesFromDatabase(String id){
        Cursor performancesCursor = getContext().getContentResolver().query(FestivalProvider.PERFORMANCES_CONTENT_URI,null,"festival_id = " + id,null,null);
        if(performancesCursor.getCount() > 0 ) {
            performancesCursor.moveToFirst();
            DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            ArrayList<Performance> performances = new ArrayList<>();
            while (!performancesCursor.isAfterLast()) {
                Performance performance = new Performance();
                try {
                    performance.end = dateformat.parse(performancesCursor.getString(performancesCursor.getColumnIndex("end")));
                    performance.start = dateformat.parse(performancesCursor.getString(performancesCursor.getColumnIndex("start")));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                performance.id = performancesCursor.getString(performancesCursor.getColumnIndex("_id"));
                performance.title = performancesCursor.getString(performancesCursor.getColumnIndex("title"));
                performance.price = performancesCursor.getDouble(performancesCursor.getColumnIndex("price"));
                performances.add(performance);
                performancesCursor.moveToNext();
            }
            performancesCursor.close();
            return performances;
        }
        return null;
    }

    @NonNull
    private FestivalEvent.Versions getImageVersions(Cursor festivalsCursor) {
        FestivalEvent.Versions versions = new FestivalEvent.Versions();
        FestivalEvent.ImageVersion large = new FestivalEvent.ImageVersion();
        large.url = festivalsCursor.getString(festivalsCursor.getColumnIndex("image_large"));
        versions.large = large;
        FestivalEvent.ImageVersion thumb = new FestivalEvent.ImageVersion();
        thumb.url = festivalsCursor.getString(festivalsCursor.getColumnIndex("image_thumb"));
        versions.square = thumb;
        return versions;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    int retrieveRecordsFrom = 0;
    private void executeRequest(){
        if(retrieveRecordsFrom == 0){
            FestivalDBHelper.resetDB(new FestivalDBHelper(getContext()).getWritableDatabase());
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, buildApiUrl(retrieveRecordsFrom), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, "Succesfully recieved festival data!");
                        Type listType = new TypeToken<ArrayList<FestivalEvent>>() {
                        }.getType();
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(FestivalEvent.Image[].class,
                                        new FestivalEvent.ImagesDeserializer())
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create();

                        System.out.println(response.toString());
                        List array = gson.fromJson(response.toString(), listType);
                        if (array.size() < 100) {
                            retrieveRecordsFrom = 0;
                        }
                        else{
                            retrieveRecordsFrom += 100;
                            executeRequest();
                        }

                        Log.v(TAG, "onResponse: " + retrieveRecordsFrom);
                        festivalArray = gson.fromJson(response.toString(), listType);


                        addToDatabase();
                        updateUI(festivalArray);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Something went wrong!: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params;
                params = new HashMap<>();
                super.getHeaders();
                params.put("Accept", "application/json;ver=2.0");

                return params;
            }
        };

        RequestQueueSingleton.getInstance(getContext()).getRequestQueue().add(request);

    }

    public void addToDatabase() {
        ContentValues[] cvFestivalArray = new ContentValues[festivalArray.size()];
        for (int i = 0; i < festivalArray.size(); i++) {
            FestivalEvent festivalEvent = festivalArray.get(i);
            ContentValues values = Util.objectToContentValues(festivalEvent);
            cvFestivalArray[i] = values;
            int festivalId = retrieveRecordsFrom + i;
            ContentValues[] cvPerformanceArray = new ContentValues[festivalEvent.performances.size()];
            cvFestivalArray[i].put("_id", festivalId);

            getContext().getContentResolver().insert(FestivalProvider.FESTIVALS_CONTENT_URI, cvFestivalArray[i]);
            for (int k = 0; k < festivalEvent.performances.size(); k++) {
                Performance performance = festivalArray.get(i).performances.get(k);
                ContentValues values1 = Util.objectToContentValues(performance);
                cvPerformanceArray[k] = values1;
                cvPerformanceArray[k].put("festival_id", festivalId);
                String id = festivalId + " " + k;
                performance.id = id;
                cvPerformanceArray[k].put("_id", id);
                getContext().getContentResolver().insert(FestivalProvider.PERFORMANCES_CONTENT_URI, cvPerformanceArray[k]);


            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
        detailFragment = null;
        festivalArray = null;
        adapter = null;
    }
}
