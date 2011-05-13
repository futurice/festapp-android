package fi.ruisrock.android.domain.to;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.ruisrock.android.domain.Gig;

/**
 * DaySchedule transfer-object.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class DaySchedule {
	
	private FestivalDay festivalDay;
	private Map<String, List<Gig>> stageGigs = new HashMap<String, List<Gig>>();
	private Date earliestTime;
	private Date latestTime;
	
	public DaySchedule(FestivalDay festivalDay, Map<String, List<Gig>> stageGigs) {
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
	
	private void setEarliestAndLatestTimes() {
		for (Map.Entry<String, List<Gig>> entry : stageGigs.entrySet()) {
			for (Gig gig : entry.getValue()) {
				if (earliestTime == null || latestTime == null) {
					earliestTime = gig.getStartTime();
					latestTime = gig.getEndTime();
				} else {
					if (gig.getStartTime().before(earliestTime)) {
						earliestTime = gig.getStartTime();
					}
					if (gig.getEndTime().after(latestTime)) {
						latestTime = gig.getEndTime();
					}
				}
			}
		}
	}
	
}
