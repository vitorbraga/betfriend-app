package br.com.betfriend.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Ranking;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RankingFragment extends Fragment {

    private ListView mRankingListView;

    private ProgressBar mSpinner;

    static class ViewHolder {
        public TextView personName;
        public ImageView personPhoto;
        public TextView percentage;
        public TextView rate;
    }

    private TextView mNoRankingFound;

    public RankingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        mRankingListView = (ListView) view.findViewById(R.id.ranking_list);

        mSpinner = (ProgressBar) view.findViewById(R.id.main_progressbar);

        mNoRankingFound = (TextView) view.findViewById(R.id.no_ranking_found);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getLastWeekRanking("application/json", new Callback<ArrayList<Ranking>>() {

            @Override
            public void success(ArrayList<Ranking> rankings, Response response) {

                mSpinner.setVisibility(View.GONE);

                if (rankings.size() > 0) {
                    mRankingListView.setVisibility(View.VISIBLE);

                    RankingsArrayAdapter betAdapter = new RankingsArrayAdapter(getActivity(), rankings);
                    mRankingListView.setAdapter(betAdapter);

                } else {
                    mNoRankingFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "reetrofit error all bets", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class RankingsArrayAdapter extends ArrayAdapter<Ranking> {

        private final Activity context;

        private ArrayList<Ranking> rankings;

        public RankingsArrayAdapter(Activity context, ArrayList<Ranking> rankings) {
            super(context, R.layout.bet_list_item, rankings);
            this.context = context;
            this.rankings = rankings;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.ranking_list_item, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.personName = (TextView) rowView.findViewById(R.id.person_name);
                viewHolder.personPhoto = (ImageView) rowView.findViewById(R.id.person_photo);
                viewHolder.percentage = (TextView) rowView.findViewById(R.id.percentage);
                viewHolder.rate = (TextView) rowView.findViewById(R.id.rate);

                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();

            String personName = rankings.get(position).getPersonName();
            holder.personName.setText(personName);

            String personPhoto = rankings.get(position).getPersonPhoto();
            Picasso.with(context)
                    .load(personPhoto)
                    .transform(new CircleTransformation())
                    .into(holder.personPhoto);

            float percentage = rankings.get(position).getPercentage() * 100;
            holder.percentage.setText(String.format("%.0f%%", percentage));

            Integer played = rankings.get(position).getPlayed();
            Integer win = rankings.get(position).getWin();
            holder.rate.setText(getActivity().getString(R.string.rank_rate, win, played));

            return rowView;
        }
    }
}
