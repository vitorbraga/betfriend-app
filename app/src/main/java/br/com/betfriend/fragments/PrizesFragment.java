package br.com.betfriend.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import br.com.betfriend.R;
import br.com.betfriend.model.UserDataDTO;

public class PrizesFragment extends SampleFragment {

    private DecoView mBetWon, mInvitesMade, mInvitesAccepted,
            mGoldenMedal, mPodium;

    private int mSeries1Index;

    private UserDataDTO userData;

    public PrizesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prizes, container, false);

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");


        mBetWon = (DecoView) view.findViewById(R.id.bets_won);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void createTracks(int arcViewId, Interpolator interpolator, int color) {
        final View view = getView();
        if (view == null) {
            return;
        }

        final DecoView decoView = (DecoView) view.findViewById(arcViewId);
        if (decoView == null) {
            return;
        }

        decoView.deleteAll();
        decoView.configureAngles(320, 180);

        float mSeriesMax = 50f;
        switch (arcViewId) {
            case R.id.bets_won:
                mSeriesMax = 20;
                break;
            case R.id.invites_made:
                mSeriesMax = 100;
                break;
            case R.id.invites_accepted:
                mSeriesMax = 100;
                break;
            case R.id.golden_medal:
                mSeriesMax = 5;
                break;
            case R.id.podium:
                mSeriesMax = 10;
                break;

        }
        // Cinza por tras
        SeriesItem arcBackTrack = new SeriesItem.Builder(Color.argb(255, 228, 228, 228))
                .setRange(0, mSeriesMax, mSeriesMax)
                .setLineWidth(getDimension(4f))
                .build();

        decoView.addSeries(arcBackTrack);

        SeriesItem seriesItem1 = new SeriesItem.Builder(color)
                .setRange(0, mSeriesMax, 0)
                .setInterpolator(interpolator)
                .setLineWidth(getDimension(4f))
                .setSpinDuration(5000)
                .setSpinClockwise(true)
                .build();

        mSeries1Index = decoView.addSeries(seriesItem1);
    }

    @Override
    protected void createTracks() {
        if (getView() != null) {
            createTracks(R.id.bets_won, new LinearInterpolator(), Color.parseColor("#CC0000"));
            createTracks(R.id.invites_made, new LinearInterpolator(), Color.parseColor("#048482"));
            createTracks(R.id.invites_accepted, new LinearInterpolator(), Color.parseColor("#003366"));
            createTracks(R.id.golden_medal, new LinearInterpolator(), Color.parseColor("#66A7C5"));
            createTracks(R.id.podium, new LinearInterpolator(), Color.parseColor("#FF6000"));
            createTracks(R.id.another, new LinearInterpolator(), Color.parseColor("#6F0564"));
        }
    }

    @Override
    protected void setupEvents() {
        if (getView() != null) {
            setupEvents(R.id.bets_won);
            setupEvents(R.id.invites_made);
            setupEvents(R.id.invites_accepted);
            setupEvents(R.id.golden_medal);
            setupEvents(R.id.podium);
            setupEvents(R.id.another);
        }
    }

    private void setupEvents(final int arcId) {
        final View view = getView();
        if (view == null) {
            return;
        }
        final DecoView decoView = (DecoView) view.findViewById(arcId);
        if (decoView == null || decoView.isEmpty()) {
            throw new IllegalStateException("Unable to add events to empty View");
        }

        decoView.executeReset();

        switch (arcId) {
            case R.id.bets_won:
                decoView.addEvent(new DecoEvent.Builder(userData.getBetsWon()).setIndex(mSeries1Index).setDelay(1000).build());
                break;
            case R.id.invites_made:
                decoView.addEvent(new DecoEvent.Builder(userData.getInvitesMade()).setIndex(mSeries1Index).setDelay(1000).build());
                break;
            case R.id.invites_accepted:
                decoView.addEvent(new DecoEvent.Builder(userData.getInvitesAccepted()).setIndex(mSeries1Index).setDelay(1000).build());
                break;
            case R.id.golden_medal:
                decoView.addEvent(new DecoEvent.Builder(userData.getGoldMedal()).setIndex(mSeries1Index).setDelay(1000).build());
                break;
            case R.id.podium:
                decoView.addEvent(new DecoEvent.Builder(userData.getPodium()).setIndex(mSeries1Index).setDelay(1000).build());
                break;
        }

    }

}
