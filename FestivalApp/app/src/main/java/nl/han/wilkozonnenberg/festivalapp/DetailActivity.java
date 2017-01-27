package nl.han.wilkozonnenberg.festivalapp;

import android.content.Context;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;

import static nl.han.wilkozonnenberg.festivalapp.R.id.container;

public class DetailActivity extends AppCompatActivity {

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        mContext = getApplicationContext();
//        String[] arrayList =  mContext.databaseList();
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            DetailFragment fragment = new DetailFragment();
            FestivalEvent festivalEvent = (FestivalEvent) getIntent().getSerializableExtra("FestivalEvent");

            fragment.initDetailFragment(festivalEvent);
            getSupportFragmentManager().beginTransaction()
                    .add(container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailfragment, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        NavUtils.navigateUpFromSameTask(this);
        return super.onOptionsItemSelected(item);
    }

}
