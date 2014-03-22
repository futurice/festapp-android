package com.futurice.festapp.util;

import java.util.Comparator;

import com.futurice.festapp.domain.Gig;


public class GigArtistNameComparator implements Comparator<Gig> {
	
	@Override
	public int compare(Gig gig1, Gig gig2) {
		return gig1.getArtist().compareTo(gig2.getArtist());
	}

}
