package br.com.betfriend.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.betfriend.R;
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


public class InvitesFragment extends Fragment {

    private AnimatedExpandableListView mInvitesListView;

    private ProgressBar mProgressBar;

    private LinearLayout mNoInvitesFound;

    private InvitesExpandableListAdapter mAdapter;

    private UserDataDTO userData;

    private Button mRetryButton;

    private Context mContext;

    public InvitesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_invites, container, false);

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        mContext = getActivity();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {

        super.onResume();

        mInvitesListView = (AnimatedExpandableListView) getView().findViewById(R.id.invites_list);

        mInvitesListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mInvitesListView.isGroupExpanded(groupPosition)) {
                    mInvitesListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mInvitesListView.expandGroupWithAnimation(groupPosition);
                }
                
                return true;
            }

        });

        mProgressBar = (ProgressBar) getView().findViewById(R.id.invites_progressbar);

        mNoInvitesFound = (LinearLayout) getView().findViewById(R.id.no_invites_container);

        mRetryButton = (Button) getView().findViewById(R.id.retry_button);

        getBetInvites();
    }

    private void getBetInvites() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getBetInvites(Constants.SERVER_KEY, "application/json", personId, new Callback<ArrayList<Bet>>() {

            @Override
            public void success(ArrayList<Bet> bets, Response response) {

                mProgressBar.setVisibility(View.GONE);

                if (bets.size() > 0) {
                    mInvitesListView.setVisibility(View.VISIBLE);
                    mNoInvitesFound.setVisibility(View.GONE);

                    mAdapter = new InvitesExpandableListAdapter(getActivity(), bets, userData, mProgressBar);
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
