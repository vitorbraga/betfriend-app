package br.com.betfriend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.betfriend.adapters.AnimatedExpandableListView;
import br.com.betfriend.adapters.InvitesExpandableListAdapter;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class BetInvitationsActivity extends AppCompatActivity {

    private AnimatedExpandableListView mInvitesListView;

    private ProgressBar mProgressBar;

    private LinearLayout mNoInvitesFound;

    private InvitesExpandableListAdapter mAdapter;

    private UserDataDTO userData;

    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bet_invitations);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bet_invites));
    }

    @Override
    protected void onResume() {

        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());

        userData = new UserDataDTO();

        userData.setPersonId(sharedPref.getString("PERSON_ID", ""));
        userData.setPersonPhoto(sharedPref.getString("PERSON_PHOTO", ""));
        userData.setPersonName(sharedPref.getString("PERSON_NAME", ""));

        mInvitesListView = (AnimatedExpandableListView) findViewById(R.id.invites_list);

        mInvitesListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mInvitesListView.isGroupExpanded(groupPosition)) {
                    mInvitesListView.collapseGroupWithAnimation(groupPosition);
                    v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
                } else {
                    mInvitesListView.expandGroupWithAnimation(groupPosition);
                    v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.very_light_grey));
                }
                return true;
            }

        });

        mProgressBar = (ProgressBar) findViewById(R.id.invites_progressbar);

        mNoInvitesFound = (LinearLayout) findViewById(R.id.no_invites_container);

        mRetryButton = (Button) findViewById(R.id.retry_button);

        getBetInvites();
    }

    private void getBetInvites() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getBetInvites("application/json", personId, new Callback<ArrayList<Bet>>() {

            @Override
            public void success(ArrayList<Bet> bets, Response response) {

                mProgressBar.setVisibility(View.GONE);

                if (bets.size() > 0) {
                    mInvitesListView.setVisibility(View.VISIBLE);
                    mNoInvitesFound.setVisibility(View.GONE);

                    mAdapter = new InvitesExpandableListAdapter(getApplicationContext(), bets, userData);
                    mInvitesListView.setAdapter(mAdapter);

                } else {
                    mNoInvitesFound.setVisibility(View.VISIBLE);

                    mRetryButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            getBetInvites();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }


}
