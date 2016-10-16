package br.com.betfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.SoccerMatch;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class SearchFriendActivity extends AppCompatActivity {

    private String mBetOption;

    private Match mMatch;

    private UserDataDTO mUserData;

    private int mAmount;

    private UserDataDTO mFriend;

    private EditText mSearchField;

    private ListView mSearchListView;

    private TextView mNoResultFound;

    private Button mFinishButton;

    private ServerApi api;

    private Activity mContext;

    static class ViewHolder {
        public TextView friendId;
        public TextView personName;
        public ImageView personPhoto;
        public TextView winRate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_search_friend));

        Intent intent = getIntent();
        mBetOption = intent.getStringExtra("BET_OPTION_EXTRA");
        mAmount = intent.getIntExtra("AMOUNT", -1);
        mMatch = (Match) intent.getSerializableExtra("MATCH_EXTRA");
        mUserData = (UserDataDTO) intent.getSerializableExtra("USER_DATA_EXTRA");

        mContext = this;

        mSearchListView = (ListView) findViewById(R.id.history_list);

        mNoResultFound = (TextView) findViewById(R.id.no_results);

        mSearchField = (EditText) findViewById(R.id.search_field);

        mFinishButton = (Button) findViewById(R.id.finish_button);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        api = restAdapter.create(ServerApi.class);

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() >= 3) {

                    api.searchFriend(Constants.SERVER_KEY, "application/json", s.toString(), mUserData.getPersonId(), new Callback<ArrayList<UserDataDTO>>() {

                        @Override
                        public void success(ArrayList<UserDataDTO> users, Response response) {

                            if (users.size() > 0) {
                                mNoResultFound.setVisibility(View.GONE);
                                mSearchListView.setVisibility(View.VISIBLE);

                                FriendArrayAdapter friendsAdapter = new FriendArrayAdapter(mContext, users);
                                mSearchListView.setAdapter(friendsAdapter);
                            } else {
                                mNoResultFound.setVisibility(View.VISIBLE);
                                mSearchListView.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                view.setSelected(true);
                mFriend = (UserDataDTO) parent.getItemAtPosition(position);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(mFriend != null) {

                    Intent intent = new Intent(getApplicationContext(), StartBetActivity.class);
                    intent.putExtra("BET_OPTION_EXTRA", mBetOption);
                    intent.putExtra("MATCH_ID_EXTRA", mMatch.getMatchId());
                    intent.putExtra("MATCH_EXTRA", mMatch);
                    intent.putExtra("AMOUNT", mAmount);
                    intent.putExtra("FRIEND_EXTRA", mFriend);
                    intent.putExtra("USER_DATA_EXTRA", mUserData);

                    startActivity(intent);

                } else {
                    Toast.makeText(SearchFriendActivity.this, "Selecione um amigo", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    class FriendArrayAdapter extends ArrayAdapter<UserDataDTO> {

        private final Activity context;

        private ArrayList<UserDataDTO> users;

        public FriendArrayAdapter(Activity context, ArrayList<UserDataDTO> users) {
            super(context, R.layout.friend_list_item, users);
            this.context = context;
            this.users = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.friend_list_item, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.friendId = (TextView) rowView.findViewById(R.id.friend_id);
                viewHolder.personName = (TextView) rowView.findViewById(R.id.person_name);
                viewHolder.personPhoto = (ImageView) rowView.findViewById(R.id.person_photo);
                viewHolder.winRate = (TextView) rowView.findViewById(R.id.win_rate);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();

            holder.friendId.setText(users.get(position).getPersonId());

            UserDataDTO user = users.get(position);
            String title = user.getPersonName();
            String personPhoto = user.getPersonPhoto();

            Integer betsFinished = user.getBetsFinished();
            Integer betsWon = user.getBetsWon();
            String winRate = "0%";
            if(betsFinished != 0) {
                float winRateFloat = ((float) betsWon) / ((float) betsFinished);
                float percentage = winRateFloat * 100;
                holder.winRate.setText(String.format("%.0f%%", percentage));
            } else {
                holder.winRate.setText(winRate);
            }

            holder.personName.setText(title);

            Picasso.with(context)
                    .load(personPhoto)
                    .transform(new CircleTransformation())
                    .into(holder.personPhoto);

            return rowView;
        }
    }

}
