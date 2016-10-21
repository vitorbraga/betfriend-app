package br.com.betfriend.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.adapters.AnimatedExpandableListView;
import br.com.betfriend.adapters.BetsExpandableListAdapter;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.ConnectionUtils;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class FinishedTabFragment extends Fragment {

    private AnimatedExpandableListView mBetsListView;

    private BetsExpandableListAdapter mAdapter;

    private ProgressBar mProgressBar;

    private LinearLayout mNoBetsFound;

    private UserDataDTO userData;

    private Button mRetryButton;

    private Context mContext;


    public FinishedTabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_finished, container, false);

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        mContext = getActivity();

        mBetsListView = (AnimatedExpandableListView) view.findViewById(R.id.bet_list);

        mBetsListView.setOnGroupClickListener(new AnimatedExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (mBetsListView.isGroupExpanded(groupPosition)) {
                    mBetsListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mBetsListView.expandGroupWithAnimation(groupPosition);
                }

                return true;
            }

        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.bets_progressbar);

        mNoBetsFound = (LinearLayout) view.findViewById(R.id.no_bets_container);

        mRetryButton = (Button) view.findViewById(R.id.retry_button);

        mRetryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                getFinishedBets();
            }
        });

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        getFinishedBets();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getFinishedBets() {

        if(!ConnectionUtils.isOnline(getActivity())) {
            mProgressBar.setVisibility(View.GONE);
            mBetsListView.setVisibility(View.GONE);
            mNoBetsFound.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getFinishedBets(Constants.SERVER_KEY, "application/json", personId, new Callback<ArrayList<Bet>>() {

            @Override
            public void success(ArrayList<Bet> bets, Response response) {

                mProgressBar.setVisibility(View.GONE);

                if (bets.size() > 0) {
                    mBetsListView.setVisibility(View.VISIBLE);
                    mNoBetsFound.setVisibility(View.GONE);

                    mAdapter = new BetsExpandableListAdapter(getActivity(), bets, userData);
                    mBetsListView.setAdapter(mAdapter);

                } else {
                    mBetsListView.setVisibility(View.GONE);
                    mNoBetsFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getActivity().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
