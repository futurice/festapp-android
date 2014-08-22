package de.serviceexperiencecamp.android.models;

import java.util.List;

import de.serviceexperiencecamp.android.models.pojo.Info;
import de.serviceexperiencecamp.android.network.SecApi;
import rx.Observable;

public class InfoModel {
    static private InfoModel instance;

    static public InfoModel getInstance() {
        if (instance == null) {
            instance = new InfoModel();
        }
        return instance;
    }

    private InfoModel() { }

    public Observable<List<Info>> getInfoList$() {
        return SecApi.getInstance().getAllInfo();
    }

}
