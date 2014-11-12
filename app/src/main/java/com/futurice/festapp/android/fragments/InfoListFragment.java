package com.futurice.festapp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.futurice.festapp.android.MainActivity;
import com.futurice.festapp.android.R;
import com.futurice.festapp.android.models.InfoModel;
import com.futurice.festapp.android.models.pojo.Info;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class InfoListFragment extends Fragment {

    final private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private InfoModel infoModel;
    private LinearLayout list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        infoModel = InfoModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_info_list, container, false);
        list = (LinearLayout) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeSubscription.add(infoModel.getInfoList$()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Info>>() {
                @Override
                public void call(List<Info> infos) {
                    list.removeAllViews();
                    for (final Info info : infos) {
                        list.addView(makeInfoListItem(info));
                    }
                }
            })
        );
    }

    private View makeInfoListItem(final Info info) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_info_list_item, null, false);
        TextView title = (TextView) view.findViewById(R.id.title);

        title.setText(info.title);

        view.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            MainActivity activity = (MainActivity) getActivity();
            InfoFragment fragment = new InfoFragment();
            fragment.setArguments(info.getBundle());
            activity.fragment$.onNext(fragment);
        }});

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeSubscription.clear();
    }
}
