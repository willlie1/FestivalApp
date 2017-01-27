package nl.han.wilkozonnenberg.festivalapp;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import static nl.han.wilkozonnenberg.festivalapp.R.id.container;

public class ProgramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        if (savedInstanceState == null) {
            ProgramFragment fragment = new ProgramFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(container, fragment)
                    .commit();
        }

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
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
