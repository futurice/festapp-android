package fi.ruisrock.android.util;

import java.util.Comparator;

import fi.ruisrock.android.domain.Gig;

public class GigArtistNameComparator implements Comparator<Gig> {
	
	@Override
	public int compare(Gig gig1, Gig gig2) {
		return gig1.getArtist().compareTo(gig2.getArtist());
	}

}
