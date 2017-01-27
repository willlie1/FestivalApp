package nl.han.wilkozonnenberg.festivalapp;

import android.content.Context;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import static nl.han.wilkozonnenberg.festivalapp.R.id.container;


public class MainActivity extends AppCompatActivity {

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
//        String[] arrayList =  mContext.databaseList();

        if (savedInstanceState == null) {
            FestivalFragment fragment = new FestivalFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

//    public void onAttach(Activity activity){
//    }
}
