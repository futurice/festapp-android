package com.futurice.festapp.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futurice.festapp.android.MainActivity;
import com.futurice.festapp.android.R;
import com.futurice.festapp.android.models.pojo.Gig;
import com.futurice.festapp.android.network.FestAppApi;
import com.squareup.picasso.Picasso;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Func1;

public class MenuFragment extends Fragment {

    private ImageView gigImageView;
    private TextView gigTitle;
    private TextView gigStage;
    private View agendaView;
    private View keyTalksView;
    private View venue;
    private View info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        gigImageView = (ImageView) view.findViewById(R.id.gig_image);
        gigTitle = (TextView) view.findViewById(R.id.gig_title);
        gigStage = (TextView) view.findViewById(R.id.gig_stage);
        agendaView = view.findViewById(R.id.agenda);
        keyTalksView = view.findViewById(R.id.keytalks);
        venue = view.findViewById(R.id.venue);
        info = view.findViewById(R.id.info);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final MainActivity activity = (MainActivity) getActivity();
        agendaView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            activity.fragment$.onNext(activity.scheduleFragment);
        }});
        keyTalksView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            activity.fragment$.onNext(activity.eventListFragment);
        }});
        venue.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            activity.fragment$.onNext(activity.venueFragment);
        }});
        info.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            activity.fragment$.onNext(activity.infoListFragment);
        }});

        FestAppApi.getInstance().getAllGigs()
            .map(new Func1<List<Gig>, Gig>() {
                @Override
                public Gig call(List<Gig> gigs) {
                    return gigs.get(0);
                }
            })
            .subscribe(new Action1<Gig>() {
                @Override
                public void call(Gig gig) {
                    gigTitle.setText(gig.name);
                    gigStage.setText(gig.stage);
                    Picasso.with(getActivity())
                        .load(FestAppApi.getInstance().getImageFullUrl(gig.artist.imageUrl))
                        .error(R.drawable.event_placeholder)
                        .into(gigImageView);
                }
            });
    }

    @Override
    public void onPause() {
        super.onPause();
        agendaView.setOnClickListener(null);
        keyTalksView.setOnClickListener(null);
    }
}
