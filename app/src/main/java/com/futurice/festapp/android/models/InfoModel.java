package com.futurice.festapp.android.models;

import java.util.List;

import com.futurice.festapp.android.models.pojo.Info;
import com.futurice.festapp.android.network.FestAppApi;

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
        return FestAppApi.getInstance().getAllInfo();
    }

}
