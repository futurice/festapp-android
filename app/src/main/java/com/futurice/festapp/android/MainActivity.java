package com.futurice.festapp.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.danlew.android.joda.JodaTimeAndroid;

import com.futurice.festapp.android.fragments.EventListFragment;
import com.futurice.festapp.android.fragments.InfoListFragment;
import com.futurice.festapp.android.fragments.MenuFragment;
import com.futurice.festapp.android.fragments.ScheduleFragment;
import com.futurice.festapp.android.fragments.VenueFragment;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class MainActivity extends Activity {

    public BehaviorSubject<Fragment> fragment$ = BehaviorSubject.create((Fragment) new MenuFragment());

    public MenuFragment menuFragment;
    public ScheduleFragment scheduleFragment;
    public EventListFragment eventListFragment;
    public VenueFragment venueFragment;
    public InfoListFragment infoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        menuFragment = new MenuFragment();
        scheduleFragment = new ScheduleFragment();
        eventListFragment = new EventListFragment();
        venueFragment = new VenueFragment();
        infoListFragment = new InfoListFragment();

        fragment$.subscribe(new Action1<Fragment>() { @Override public void call(Fragment frag) {
            MainActivity.this.getFragmentManager().beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(null)
                .commit();
        }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStackImmediate();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
