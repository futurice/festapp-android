package fi.ruisrock.android.domain.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fi.ruisrock.android.domain.Gig;

/**
 * DaySchedule transfer-object.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DaySchedule {
	
	private FestivalDay festivalDay;
	private Map<String, List<Gig>> stageGigs = new TreeMap<String, List<Gig>>();
	private Date earliestTime;
	private Date latestTime;
	
	public DaySchedule(FestivalDay festivalDay, TreeMap<String, List<Gig>> stageGigs) {
		this.festivalDay = festivalDay;
		this.stageGigs = stageGigs;
		setEarliestAndLatestTimes();
	}
	
	public FestivalDay getFestivalDay() {
		return festivalDay;
	}
	
	public Map<String, List<Gig>> getStageGigs() {
		return stageGigs;
	}
	
	public Date getEarliestTime() {
		return earliestTime;
	}
	
	public Date getLatestTime() {
		return latestTime;
	}
	
	public List<String> getStages() {
		return new ArrayList<String>(stageGigs.keySet());
	}
	
	private void setEarliestAndLatestTimes() {
		for (Map.Entry<String, List<Gig>> entry : stageGigs.entrySet()) {
			for (Gig gig : entry.getValue()) {
				if (earliestTime == null || latestTime == null) {
					earliestTime = gig.getOnlyStartTime();
					latestTime = gig.getOnlyEndTime();
				} else {
					if (gig.getOnlyStartTime().before(earliestTime)) {
						earliestTime = gig.getOnlyStartTime();
					}
					if (gig.getOnlyEndTime().after(latestTime)) {
						latestTime = gig.getOnlyEndTime();
					}
				}
			}
		}
	}
	
}
