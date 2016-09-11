package br.com.betfriend.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeFragment extends Fragment {

    private AnimatedExpandableListView matchesListView;

    private ProgressBar spinner;

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
        return inflater.inflate(R.layout.fragment_home, container, false);
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

        spinner = (ProgressBar) getView().findViewById(R.id.main_progressbar);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SOCCER_API_BASE_URI).build();

        SoccerApi api = restAdapter.create(SoccerApi.class);

        api.getMatches("application/json", Constants.SOCCER_API_KEY, "34", new Callback<ArrayList<SoccerMatch>>() {

            @Override
            public void success(ArrayList<SoccerMatch> matches, Response response) {
                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();

                matchesListView.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);

                ExpandableListAdapter matchesAdapter = new ExpandableListAdapter(getActivity(), matches);
                matchesListView.setAdapter(matchesAdapter);
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

//    class MatchesArrayAdapter extends ArrayAdapter<SoccerMatch> {
//
//        private final Activity context;
//
//        private ArrayList<SoccerMatch> matches;
//
//        public MatchesArrayAdapter(Activity context, ArrayList<SoccerMatch> matches) {
//            super(context, R.layout.history_list_item, matches);
//            this.context = context;
//            this.matches = matches;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            View rowView = convertView;
//            // reuse views
//            if (rowView == null) {
//                LayoutInflater inflater = context.getLayoutInflater();
//                rowView = inflater.inflate(R.layout.match_list_item, null);
//                // configure view holder
//                ViewHolder viewHolder = new ViewHolder();
//                viewHolder.homeTeam = (TextView) rowView.findViewById(R.id.home_team);
//                viewHolder.awayTeam = (TextView) rowView.findViewById(R.id.away_team);
//                viewHolder.matchTime = (TextView) rowView.findViewById(R.id.match_time);
//
//                rowView.setTag(viewHolder);
//            }
//
//            // fill data
//            ViewHolder holder = (ViewHolder) rowView.getTag();
//
//            String homeTeam = matches.get(position).getHomeTeam();
//            String awayTeam = matches.get(position).getAwayTeam();
//            Long tsTamp = matches.get(position).getTstamp();
//
//            Date date = new Date(1000 * tsTamp);
//
//            holder.homeTeam.setText(homeTeam);
//            holder.awayTeam.setText(awayTeam);
//            holder.matchTime.setText(ConvertHelper.dateToView(date));
//
//            return rowView;
//        }
//    }

}
