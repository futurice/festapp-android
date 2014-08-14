package de.serviceexperiencecamp.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.serviceexperiencecamp.android.fragments.MenuFragment;
import de.serviceexperiencecamp.android.fragments.ScheduleFragment;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class MainActivity extends Activity {

    public BehaviorSubject<Fragment> fragment$ = BehaviorSubject.create((Fragment) new MenuFragment());

    public MenuFragment menuFragment;
    public ScheduleFragment scheduleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                .replace(R.id.container, new MenuFragment())
//                .commit();
//        }
        menuFragment = new MenuFragment();
        scheduleFragment = new ScheduleFragment();

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
        getFragmentManager().popBackStack();
        //super.onBackPressed();
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
