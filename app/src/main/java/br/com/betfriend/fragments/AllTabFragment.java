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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class AllTabFragment extends Fragment {

    private ListView mBetListView;

    private ProgressBar mSpinner;

    static class ViewHolder {
        public TextView matchId;
        public TextView srcPerson;
        public TextView destPerson;
    }

    private TextView mNoBetsFound;

    public AllTabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tab_all, container, false);

        mBetListView = (ListView) view.findViewById(R.id.bet_list);

        mSpinner = (ProgressBar) view.findViewById(R.id.main_progressbar);

        mNoBetsFound = (TextView) view.findViewById(R.id.no_bets_found);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getAllBets(Constants.SERVER_KEY, "application/json", personId, new Callback<ArrayList<Bet>>() {

            @Override
            public void success(ArrayList<Bet> bets, Response response) {

                mSpinner.setVisibility(View.GONE);

                if(bets.size() > 0) {
                    mBetListView.setVisibility(View.VISIBLE);

                    BetArrayAdapter betAdapter = new BetArrayAdapter(getActivity(), bets);
                    mBetListView.setAdapter(betAdapter);

                } else {
                    mNoBetsFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class BetArrayAdapter extends ArrayAdapter<Bet> {

        private final Activity context;

        private ArrayList<Bet> bets;

        public BetArrayAdapter(Activity context, ArrayList<Bet> bets) {
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
                rowView = inflater.inflate(R.layout.bet_list_item, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.matchId = (TextView) rowView.findViewById(R.id.match_id);
                viewHolder.srcPerson = (TextView) rowView.findViewById(R.id.src_person);
                viewHolder.destPerson = (TextView) rowView.findViewById(R.id.dest_person);

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

            return rowView;
        }
    }
}
