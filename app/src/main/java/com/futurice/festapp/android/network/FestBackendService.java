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

    @GET("/api/artists")
    public Observable<List<Artist>> getAllArtists();

    @GET("/api/gigs")
    public Observable<List<Gig>> getAllGigs();

    @GET("/api/news")
    public Observable<List<News>> getAllNews();

    @GET("/api/info")
    public Observable<List<Info>> getAllInfo();

    @GET("/api/festival")
    public Observable<Festival> getFestival();
}
