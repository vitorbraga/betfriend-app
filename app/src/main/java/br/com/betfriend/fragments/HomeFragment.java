package br.com.betfriend.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.adapters.AnimatedExpandableListView;
import br.com.betfriend.adapters.MatchesExpandableListAdapter;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.ConnectionUtils;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class HomeFragment extends android.app.Fragment {

    private AnimatedExpandableListView matchesListView;

    private ProgressBar mProgressBar;

    private LinearLayout mNoMatchesContainer;

    private Button mRetryButton;

    private UserDataDTO userData;

    private MatchesExpandableListAdapter mAdapter;

    private Context mContext;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = getActivity();

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        mProgressBar = (ProgressBar) view.findViewById(R.id.main_progressbar);

        matchesListView = (AnimatedExpandableListView) view.findViewById(R.id.matches_list);

        matchesListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (matchesListView.isGroupExpanded(groupPosition)) {
                    matchesListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    matchesListView.expandGroupWithAnimation(groupPosition);
                }

                return true;
            }

        });

        mNoMatchesContainer = (LinearLayout) view.findViewById(R.id.no_matches_container);

        mRetryButton = (Button) view.findViewById(R.id.retry_button);

        mRetryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);
                getMatches();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {

        super.onResume();

        // Get matches from server
        getMatches();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getMatches() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        if(!ConnectionUtils.isOnline(mContext)) {
            mProgressBar.setVisibility(View.GONE);
            mNoMatchesContainer.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
            return;
        }

        api.getMatches(Constants.SERVER_KEY, "application/json", new Callback<ArrayList<Match>>() {

            @Override
            public void success(ArrayList<Match> matches, Response response) {

                mProgressBar.setVisibility(View.GONE);

                if (matches.size() > 0) {

                    mNoMatchesContainer.setVisibility(View.GONE);
                    matchesListView.setVisibility(View.VISIBLE);

                    mAdapter = new MatchesExpandableListAdapter(getActivity(), matches, userData);
                    matchesListView.setAdapter(mAdapter);

                } else {
                    mNoMatchesContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                mProgressBar.setVisibility(View.GONE);
                mNoMatchesContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setUserData(UserDataDTO user) {
        userData = user;
        if (mAdapter != null) {
            mAdapter.setUserData(user);
        }
    }

}
