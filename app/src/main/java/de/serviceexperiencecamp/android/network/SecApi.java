package de.serviceexperiencecamp.android.network;

import de.serviceexperiencecamp.android.models.pojo.Event;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import rx.Observable;

/**
 * Entry point for all requests to Service Experience Camp API.
 * Uses Retrofit library to abstract the actual REST API into a service.
 */
public class SecApi {

    private static SecApi instance;
    private ConferenceService conferenceService;

    /**
     * Returns the instance of this singleton.
     */
    public static SecApi getInstance() {
        if (instance == null) {
            instance = new SecApi();
        }
        return instance;
    }

    /**
     * Private singleton constructor.
     */
    private SecApi() {
        RestAdapter restAdapter = buildRestAdapter();
        this.conferenceService = restAdapter.create(ConferenceService.class);
    }

    /**
     * Creates the RestAdapter by setting custom HttpClient.
     */
    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
            .setEndpoint(ApiConstants.BASE_URL)
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

    public Observable<List<Event>> getAllEvents() {
        return this.conferenceService.getAllEvents();
    }
}
