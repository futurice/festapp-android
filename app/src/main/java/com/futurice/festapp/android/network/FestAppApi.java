package com.futurice.festapp.android.network;

import com.futurice.festapp.android.models.pojo.Gig;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.futurice.festapp.android.models.pojo.Info;
import com.futurice.festapp.android.utils.FileUtils;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Entry point for all requests to FestApp API.
 * Uses Retrofit library to abstract the actual REST API into a service.
 */
public class FestAppApi {

    static private final String EVENT_CACHE_FILE = "events.json";
    static private final String INFO_CACHE_FILE = "infos.json";
    private static FestAppApi instance;
    private FestBackendService festBackendService;

    /**
     * Returns the instance of this singleton.
     */
    public static FestAppApi getInstance() {
        if (instance == null) {
            instance = new FestAppApi();
        }
        return instance;
    }

    /**
     * Private singleton constructor.
     */
    private FestAppApi() {
        RestAdapter restAdapter = buildRestAdapter();
        this.festBackendService = restAdapter.create(FestBackendService.class);
    }

    /**
     * Creates the RestAdapter by setting custom HttpClient.
     */
    private RestAdapter buildRestAdapter() {
        Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();
        return new RestAdapter.Builder()
            .setEndpoint(ApiConstants.BASE_URL)
            .setConverter(new GsonConverter(gson))
            .setClient(getHttpClient())
            .build();
    }

    /**
     * Custom Http Client to define connection timeouts.
     */
    private Client getHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        return new OkClient(httpClient);
    }

    public List<Gig> getAllEventsFromCache() {
        String eventsString = FileUtils.readFromCacheFile(EVENT_CACHE_FILE);
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Gig>>(){}.getType();
        if (eventsString == null || eventsString.length() <= 0) {
            return null;
        }
        try {
            return gson.fromJson(eventsString, type);
        } catch (Exception ignore) {
            return null;
        }
    }

    public List<Info> getAllInfoFromCache() {
        String infosString = FileUtils.readFromCacheFile(INFO_CACHE_FILE);
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Info>>(){}.getType();
        if (infosString == null || infosString.length() <= 0) {
            return null;
        }
        try {
            return gson.fromJson(infosString, type);
        } catch (Exception ignore) {
            return null;
        }
    }

    public void writeEventsToCache(List<Gig> gigs) {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Gig>>(){}.getType();
        String json = gson.toJson(gigs, type);
        FileUtils.writeToCacheFile(EVENT_CACHE_FILE, json);
    }

    public void writeInfosToCache(List<Info> infos) {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Info>>(){}.getType();
        String json = gson.toJson(infos, type);
        FileUtils.writeToCacheFile(INFO_CACHE_FILE, json);
    }

    public String getImageFullUrl(String partialUrl) {
        return ApiConstants.BASE_URL + partialUrl;
    }

    public Observable<List<Gig>> getAllGigs() {
        return Observable.just(getAllEventsFromCache())
            .mergeWith(this.festBackendService.getAllGigs())
            .retry(3)
            .onErrorReturn(new Func1<Throwable, List<Gig>>() {
                @Override
                public List<Gig> call(Throwable throwable) {
                    return null;
                }
            })
            .filter(new Func1<List<Gig>, Boolean>() {
                @Override
                public Boolean call(List<Gig> gigs) {
                    return gigs != null;
                }
            })
            .doOnNext(new Action1<List<Gig>>() {
                @Override
                public void call(List<Gig> gigs) {
                    writeEventsToCache(gigs);
                }
            });
    }

    public Observable<List<Info>> getAllInfo() {
        return Observable.just(getAllInfoFromCache())
            .mergeWith(this.festBackendService.getAllInfo())
            .retry(3)
            .onErrorReturn(new Func1<Throwable, List<Info>>() {
                @Override
                public List<Info> call(Throwable throwable) {
                    return null;
                }
            })
            .filter(new Func1<List<Info>, Boolean>() {
                @Override
                public Boolean call(List<Info> infos) {
                    return (infos != null);
                }
            })
            .doOnNext(new Action1<List<Info>>() {
                @Override
                public void call(List<Info> infos) {
                    writeInfosToCache(infos);
                }
            });
    }
}
