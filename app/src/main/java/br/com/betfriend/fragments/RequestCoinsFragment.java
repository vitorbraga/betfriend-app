package br.com.betfriend.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.com.betfriend.R;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.CoinRequest;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class RequestCoinsFragment extends Fragment {

    private TextView countDownTimer, countDownFinished;

    private Button requestButton;

    private ProgressBar mProgressBar;

    private static final long REQUEST_MINIMUN_INTERVAL = 24 * 60 * 60 * 1000;

    private static final String FORMAT = "%02d:%02d:%02d";

    public RequestCoinsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_request_coins, container, false);

        countDownTimer = (TextView) view.findViewById(R.id.countdown_timer);

        countDownFinished = (TextView) view.findViewById(R.id.countdown_finished);

        requestButton = (Button) view.findViewById(R.id.request_button);

        requestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String personId = sharedPref.getString("PERSON_ID", "");

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                ServerApi api = restAdapter.create(ServerApi.class);

                api.newRequest(Constants.SERVER_KEY, personId, new Callback<JsonResponse>() {

                    @Override
                    public void success(JsonResponse json, Response response) {

                        if(json.getCode() == 0) {
                            Toast.makeText(getActivity(), "DEU CERTO", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "DEU CERTO Mas errado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getActivity(), "DEU ERRADO", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_progressbar);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {

        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);


        api.checkRequestCountdown(Constants.SERVER_KEY, "application/json", personId, new Callback<CoinRequest>() {

            @Override
            public void success(CoinRequest coinRequest, Response response) {

                mProgressBar.setVisibility(View.GONE);

                Date updatedDate = new Date(coinRequest.getUpdated().getTime()
                        - TimeUnit.HOURS.toMillis(Constants.SERVER_TIMEZONE_OFFSET));
                long lastTime = updatedDate.getTime();
                long now = (new Date()).getTime();

                long countDownTime = now - lastTime;

                if(countDownTime < REQUEST_MINIMUN_INTERVAL) {

                    countDownTimer.setVisibility(View.VISIBLE);

                    new CountDownTimer(REQUEST_MINIMUN_INTERVAL - countDownTime, 1000) { // adjust the milli seconds here

                        public void onTick(long millisUntilFinished) {

                            countDownTimer.setText("" + String.format(FORMAT,
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                        }

                        public void onFinish() {

                            countDownTimer.setVisibility(View.GONE);
                            countDownFinished.setVisibility(View.VISIBLE);
                            requestButton.setVisibility(View.VISIBLE);
                        }
                    }.start();

                } else {
                    countDownFinished.setVisibility(View.VISIBLE);
                    requestButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
            }

        });


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
