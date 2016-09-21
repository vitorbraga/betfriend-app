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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.adapters.AnimatedExpandableListView;
import br.com.betfriend.adapters.ExpandableListAdapter;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.UserDataDTO;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeFragment extends android.app.Fragment {

    private AnimatedExpandableListView matchesListView;

    private ProgressBar mSpinner;

    private LinearLayout mNoMatchesContainer;

    private Button mRetryButton;

    private UserDataDTO userData;

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

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {

        super.onResume();

        matchesListView = (AnimatedExpandableListView) getView().findViewById(R.id.matches_list);

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

        mSpinner = (ProgressBar) getView().findViewById(R.id.main_progressbar);

        mNoMatchesContainer = (LinearLayout) getView().findViewById(R.id.no_matches_container);
        mRetryButton = (Button) getView().findViewById(R.id.retry_button);

        // Get matches from server
        getMatches();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getMatches() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.server_uri)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getMatches("application/json", new Callback<ArrayList<Match>>() {

            @Override
            public void success(ArrayList<Match> matches, Response response) {

                mSpinner.setVisibility(View.GONE);

                if (matches.size() > 0) {
                    matchesListView.setVisibility(View.VISIBLE);

                    ExpandableListAdapter matchesAdapter = new ExpandableListAdapter(getActivity(), matches, userData);
                    matchesListView.setAdapter(matchesAdapter);
                } else {
                    mNoMatchesContainer.setVisibility(View.VISIBLE);

                    mRetryButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            mSpinner.setVisibility(View.VISIBLE);
                            getMatches();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getActivity(), "retorfit error getMatches", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
