package br.com.betfriend.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.adapters.AnimatedExpandableListView;
import br.com.betfriend.adapters.ExpandableListAdapter;
import br.com.betfriend.api.SoccerApi;
import br.com.betfriend.model.SoccerMatch;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeFragment extends android.app.Fragment {

    private AnimatedExpandableListView matchesListView;

    private ProgressBar mSpinner;

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
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (matchesListView.isGroupExpanded(groupPosition)) {
                    matchesListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    matchesListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        mSpinner = (ProgressBar) getView().findViewById(R.id.main_progressbar);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SOCCER_API_BASE_URI).build();

        SoccerApi api = restAdapter.create(SoccerApi.class);

        api.getMatches("application/json", Constants.SOCCER_API_KEY, "34", new Callback<ArrayList<SoccerMatch>>() {

            @Override
            public void success(ArrayList<SoccerMatch> matches, Response response) {

                matchesListView.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.GONE);

                ExpandableListAdapter matchesAdapter = new ExpandableListAdapter(getActivity(), matches, userData);
                matchesListView.setAdapter(matchesAdapter);
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(getActivity(), "retorfit error getMatches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
