package br.com.betfriend.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import br.com.betfriend.adapters.ExpandableListAdapter;
import br.com.betfriend.adapters.InvitesExpandableListAdapter;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.UserDataDTO;
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

    static class ViewHolder {
        public TextView matchId;
        public TextView srcPerson;
        public TextView destPerson;
        public Button acceptButton;
        public Button refuseButton;
    }

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

        getMatches();
    }

    private void getMatches() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.server_uri))
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getBetInvites("application/json", personId, new Callback<ArrayList<Bet>>() {

            @Override
            public void success(ArrayList<Bet> bets, Response response) {

                mProgressBar.setVisibility(View.GONE);

                if (bets.size() > 0) {
                    mInvitesListView.setVisibility(View.VISIBLE);
                    mNoInvitesFound.setVisibility(View.GONE);

                    mAdapter = new InvitesExpandableListAdapter(getActivity(), bets, userData);
                    mInvitesListView.setAdapter(mAdapter);

                } else {
                    mNoInvitesFound.setVisibility(View.VISIBLE);

                    mRetryButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            getMatches();
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

    class InvitesArrayAdapter extends ArrayAdapter<Bet> {

        private final Activity context;

        private ArrayList<Bet> bets;

        public InvitesArrayAdapter(Activity context, ArrayList<Bet> bets) {
            super(context, R.layout.bet_list_item, bets);
            this.context = context;
            this.bets = bets;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.invite_list_item, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.matchId = (TextView) rowView.findViewById(R.id.match_id);
                viewHolder.srcPerson = (TextView) rowView.findViewById(R.id.src_person);
                viewHolder.destPerson = (TextView) rowView.findViewById(R.id.dest_person);
                viewHolder.acceptButton = (Button) rowView.findViewById(R.id.accept_button);
                viewHolder.refuseButton = (Button) rowView.findViewById(R.id.refuse_button);

                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();

            String matchId = bets.get(position).getMatchId().toString();
            holder.matchId.setText(matchId);

            String srcPerson = bets.get(position).getSrcPerson().getPersonName();
            holder.srcPerson.setText(srcPerson);

            String destPerson = bets.get(position).getDestPerson().getPersonName();
            holder.destPerson.setText(destPerson);

            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "ACCEPT", Toast.LENGTH_SHORT).show();
                    // show progress bar
                    // send request
                    // refresh screen (getMatches)
                }
            });

            holder.refuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "REFUSE", Toast.LENGTH_SHORT).show();
                    // show progress bar
                    // send request
                    // refresh screen (getMatches)
                }
            });

            return rowView;
        }
    }

}
