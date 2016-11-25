package br.com.betfriend.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import br.com.betfriend.model.League;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.ConnectionUtils;
import br.com.betfriend.utils.Constants;
import br.com.betfriend.utils.LeaguesEnum;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class HomeFragment extends Fragment {

    private AnimatedExpandableListView mMatchesListView;

    private ProgressBar mProgressBar;

    private LinearLayout mNoMatchesContainer;

    private Button mRetryButton;

    private UserDataDTO userData;

    private MatchesExpandableListAdapter mAdapter;

    private Context mContext;

    private ArrayList<League> mSelectedLeagues;

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

        setHasOptionsMenu(true);

        mContext = getActivity();

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        mProgressBar = (ProgressBar) view.findViewById(R.id.main_progressbar);

        mMatchesListView = (AnimatedExpandableListView) view.findViewById(R.id.matches_list);

        mMatchesListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mMatchesListView.isGroupExpanded(groupPosition)) {
                    mMatchesListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mMatchesListView.expandGroupWithAnimation(groupPosition);
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

        mSelectedLeagues = new ArrayList<>();
        mSelectedLeagues.add(new League(LeaguesEnum.BRAZILIAN_SERIE_A, true));
        mSelectedLeagues.add(new League(LeaguesEnum.PREMIER_LEAGUE, true));
        mSelectedLeagues.add(new League(LeaguesEnum.ITALY_SERIE_A, true));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.drawer_home));
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean init[] = {true, true, true};
        for (int i = 0; i < mSelectedLeagues.size(); i++) {
            init[i] = mSelectedLeagues.get(i).isSelected();
        }

        switch (id) {

            case R.id.menu_filter:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Set the dialog title
                builder.setTitle(R.string.matches_filter_dialog_title)

                        .setMultiChoiceItems(R.array.leagues, init,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {

                                        mSelectedLeagues.get(which).setSelected(isChecked);
                                    }
                                })

                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mProgressBar.setVisibility(View.VISIBLE);
                                getMatches();
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                builder.create().show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

        if (!ConnectionUtils.isOnline(mContext)) {
            mProgressBar.setVisibility(View.GONE);
            mNoMatchesContainer.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
            return;
        }

        int leagueArray[] = new int[mSelectedLeagues.size()];
        for (int i = 0; i < mSelectedLeagues.size(); i++) {

            if(mSelectedLeagues.get(i).isSelected()) {
                leagueArray[i] = mSelectedLeagues.get(i).getLeague().id();
            }
        }

        StringBuilder leagues = new StringBuilder();
        for (int n : leagueArray) {
            if (leagues.length() > 0) {
                leagues.append(',');
            }

            leagues.append(n);
        }

        api.getMatches(Constants.SERVER_KEY, "application/json", leagues.toString(), new Callback<ArrayList<Match>>() {

            @Override
            public void success(ArrayList<Match> matches, Response response) {

                mProgressBar.setVisibility(View.GONE);

                if (matches.size() > 0) {

                    mNoMatchesContainer.setVisibility(View.GONE);
                    mMatchesListView.setVisibility(View.VISIBLE);

                    mAdapter = new MatchesExpandableListAdapter(getActivity(), matches, userData);
                    mMatchesListView.setAdapter(mAdapter);

                } else {
                    mNoMatchesContainer.setVisibility(View.VISIBLE);
                    mMatchesListView.setVisibility(View.GONE);
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
