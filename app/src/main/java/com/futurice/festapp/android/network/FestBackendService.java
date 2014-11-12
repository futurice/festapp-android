package com.futurice.festapp.android.network;

import java.util.List;

import com.futurice.festapp.android.models.pojo.Event;

import com.futurice.festapp.android.models.pojo.Info;
import retrofit.http.GET;

import rx.Observable;

/**
 * Endpoint for the Search Engine REST API.
 * Using Retrofit annotations.
 */
public interface FestBackendService {

    @GET("/events")
    public Observable<List<Event>> getAllEvents();

    @GET("/info")
    public Observable<List<Info>> getAllInfo();
}
