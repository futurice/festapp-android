package com.futurice.festapp.android.network;

import java.util.List;

import com.futurice.festapp.android.fragments.News;
import com.futurice.festapp.android.models.pojo.Artist;
import com.futurice.festapp.android.models.pojo.Gig;

import com.futurice.festapp.android.models.pojo.Festival;
import com.futurice.festapp.android.models.pojo.Info;
import retrofit.http.GET;

import rx.Observable;

/**
 * Endpoint for the Search Engine REST API.
 * Using Retrofit annotations.
 */
public interface FestBackendService {

    @GET("/artists")
    public Observable<List<Artist>> getAllArtists();

    @GET("/gigs")
    public Observable<List<Gig>> getAllGigs();

    @GET("/news")
    public Observable<List<News>> getAllNews();

    @GET("/info")
    public Observable<List<Info>> getAllInfo();

    @GET("/festival")
    public Observable<Festival> getFestival();
}
