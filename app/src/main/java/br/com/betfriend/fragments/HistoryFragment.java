package br.com.betfriend.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.betfriend.R;
import retrofit.RestAdapter;

public class HistoryFragment extends Fragment {

    private ListView historyListView;

    private ProgressBar spinner;

    static class ViewHolder {
        public TextView date;
    }

    public HistoryFragment() {
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onResume() {

        super.onResume();

        historyListView = (ListView) getView().findViewById(R.id.history_list);

        spinner = (ProgressBar) getView().findViewById(R.id.main_progressbar);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://10.10.202.62").build();

//        ServerApi api = restAdapter.create(ServerApi.class);
//
//        api.getHistory("application/json", new Callback<ArrayList<Checkin>>() {
//
//            @Override
//            public void success(ArrayList<Checkin> checkins, Response response) {
//                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
//
//                historyListView.setVisibility(View.VISIBLE);
//                spinner.setVisibility(View.GONE);
//
//                CheckinArrayAdapter episodesAdapter = new CheckinArrayAdapter(getActivity(), checkins);
//                historyListView.setAdapter(episodesAdapter);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//            }
//        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

//    class CheckinArrayAdapter extends ArrayAdapter<Checkin> {
//
//        private final Activity context;
//
//        private ArrayList<Checkin> checkins;
//
//        public CheckinArrayAdapter(Activity context, ArrayList<Checkin> checkins) {
//            super(context, R.layout.history_list_item, checkins);
//            this.context = context;
//            this.checkins = checkins;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            View rowView = convertView;
//            // reuse views
//            if (rowView == null) {
//                LayoutInflater inflater = context.getLayoutInflater();
//                rowView = inflater.inflate(R.layout.history_list_item, null);
//                // configure view holder
//                ViewHolder viewHolder = new ViewHolder();
//                viewHolder.date = (TextView) rowView.findViewById(R.id.checkin_date);
//
//                rowView.setTag(viewHolder);
//            }
//
//            // fill data
//            ViewHolder holder = (ViewHolder) rowView.getTag();
//
//            String title = checkins.get(position).getDate();
//            holder.date.setText(title);
//
//            return rowView;
//        }
//    }

}
