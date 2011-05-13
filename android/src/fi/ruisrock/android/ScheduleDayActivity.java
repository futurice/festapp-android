package fi.ruisrock.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import fi.ruisrock.android.dao.GigDAO;
import fi.ruisrock.android.domain.to.DaySchedule;
import fi.ruisrock.android.domain.to.FestivalDay;

public class ScheduleDayActivity extends Activity {
	
	private FestivalDay festivalDay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		setFestivalDay();
		Toast.makeText(getBaseContext(), festivalDay.name(), Toast.LENGTH_LONG).show();
	}
	
	
	
	private void setFestivalDay() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			FestivalDay festivalDay = (FestivalDay) getIntent().getExtras().get("festivalDay");
			this.festivalDay = festivalDay;
		}
	}
	
	private DaySchedule getDaySchedule() {
		return GigDAO.findDaySchedule(this, FestivalDay.FRIDAY);
	}

}
