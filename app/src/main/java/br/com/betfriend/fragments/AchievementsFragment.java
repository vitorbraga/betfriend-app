package br.com.betfriend.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import br.com.betfriend.R;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.GamificationUtils;

public class AchievementsFragment extends SampleFragment {

    private DecoView mBetWon, mInvitesMade, mInvitesAccepted,
            mGoldenMedal, mPodium;

    private TextView mBetWonBounds, mInvitesMadeBounds, mInvitesAcceptedBounds,
            mGoldenMedalBounds, mPodiumBounds;

    private int mSeries1Index;

    private UserDataDTO userData;

    private float LINE_WIDTH = 6f;

    public AchievementsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        mBetWon = (DecoView) view.findViewById(R.id.bets_won);
        mBetWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedalDetailsDialog(R.id.bets_won);
            }
        });

        mInvitesMade = (DecoView) view.findViewById(R.id.invites_made);
        mInvitesMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedalDetailsDialog(R.id.invites_made);
            }
        });

        mInvitesAccepted = (DecoView) view.findViewById(R.id.invites_accepted);
        mInvitesAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedalDetailsDialog(R.id.invites_accepted);
            }
        });

        mPodium = (DecoView) view.findViewById(R.id.podium);
        mPodium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedalDetailsDialog(R.id.podium);
            }
        });

        mGoldenMedal = (DecoView) view.findViewById(R.id.golden_medal);
        mGoldenMedal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMedalDetailsDialog(R.id.golden_medal);
            }
        });

        mBetWonBounds = (TextView) view.findViewById(R.id.bets_won_bounds);
        mInvitesMadeBounds = (TextView) view.findViewById(R.id.invites_accepted_bounds);
        mInvitesAcceptedBounds = (TextView) view.findViewById(R.id.invites_made_bounds);
        mGoldenMedalBounds = (TextView) view.findViewById(R.id.golden_medal_bounds);
        mPodiumBounds = (TextView) view.findViewById(R.id.podium_bounds);

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

    private void showMedalDetailsDialog(int arcId) {


        Toast.makeText(getActivity(), "arcId: " + arcId, Toast.LENGTH_SHORT).show();
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

        int mSeriesMax = 50;
        switch (arcViewId) {
            case R.id.bets_won:
                mSeriesMax = GamificationUtils.getUpperBound(arcViewId, userData.getBetsWon());
                mBetWonBounds.setText(getString(R.string.medal_bounds, userData.getBetsWon(), mSeriesMax));
                break;
            case R.id.invites_made:
                mSeriesMax = GamificationUtils.getUpperBound(arcViewId, userData.getInvitesMade());;
                mInvitesMadeBounds.setText(getString(R.string.medal_bounds, userData.getInvitesMade(), mSeriesMax));
                break;
            case R.id.invites_accepted:
                mSeriesMax = GamificationUtils.getUpperBound(arcViewId, userData.getInvitesAccepted());;
                mInvitesAcceptedBounds.setText(getString(R.string.medal_bounds, userData.getInvitesAccepted(), mSeriesMax));
                break;
            case R.id.golden_medal:
                mSeriesMax = GamificationUtils.getUpperBound(arcViewId, userData.getGoldMedal());;
                mGoldenMedalBounds.setText(getString(R.string.medal_bounds, userData.getGoldMedal(), mSeriesMax));
                break;
            case R.id.podium:
                mSeriesMax = GamificationUtils.getUpperBound(arcViewId, userData.getPodium());;
                mPodiumBounds.setText(getString(R.string.medal_bounds, userData.getPodium(), mSeriesMax));
                break;

        }
        // Cinza por tras
        SeriesItem arcBackTrack = new SeriesItem.Builder(Color.argb(255, 228, 228, 228))
                .setRange(0, mSeriesMax, mSeriesMax)
                .setLineWidth(getDimension(LINE_WIDTH))
                .build();

        decoView.addSeries(arcBackTrack);

        SeriesItem seriesItem1 = new SeriesItem.Builder(color)
                .setRange(0, mSeriesMax, 0)
                .setInterpolator(interpolator)
                .setLineWidth(getDimension(LINE_WIDTH))
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
