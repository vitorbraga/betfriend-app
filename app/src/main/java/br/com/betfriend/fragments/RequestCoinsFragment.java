package br.com.betfriend.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import br.com.betfriend.utils.ConnectionUtils;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class RequestCoinsFragment extends Fragment {

    private TextView countDownTimer;

    private Button requestButton, mRetryButton;

    private LinearLayout countdownContainer, requestContainer, noConnectionContainer;

    private ProgressBar mProgressBar;

    private Fragment mFragment;

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

        mFragment = this;

        mProgressBar = (ProgressBar) view.findViewById(R.id.request_progressbar);

        countDownTimer = (TextView) view.findViewById(R.id.countdown_timer);

        noConnectionContainer = (LinearLayout) view.findViewById(R.id.no_connection_container);
        noConnectionContainer.setVisibility(View.GONE);

        countdownContainer = (LinearLayout) view.findViewById(R.id.countdown_container);

        requestContainer = (LinearLayout) view.findViewById(R.id.request_container);

        mRetryButton = (Button) view.findViewById(R.id.retry_button);

        mRetryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);
                checkRequestCountdown();
            }
        });

        requestButton = (Button) view.findViewById(R.id.request_button);

        requestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);

                if(!ConnectionUtils.isOnline(getActivity())) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String personId = sharedPref.getString("PERSON_ID", "");

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                ServerApi api = restAdapter.create(ServerApi.class);

                api.newRequest(Constants.SERVER_KEY, personId, new Callback<JsonResponse>() {

                    @Override
                    public void success(JsonResponse json, Response response) {

                        mProgressBar.setVisibility(View.GONE);
                        if(json.getCode() == 0) {
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(mFragment);
                            ft.attach(mFragment);
                            ft.commit();
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.coin_request_not_available),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getActivity().getString(R.string.unexpected_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });

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

        checkRequestCountdown();
    }

    private void checkRequestCountdown() {

        mProgressBar.setVisibility(View.VISIBLE);

        if(!ConnectionUtils.isOnline(getActivity())) {

            mProgressBar.setVisibility(View.GONE);

            hideContents();

            noConnectionContainer.setVisibility(View.VISIBLE);
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

        api.checkRequestCountdown(Constants.SERVER_KEY, "application/json", personId, new Callback<CoinRequest>() {

            @Override
            public void success(CoinRequest coinRequest, Response response) {

                noConnectionContainer.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);

                Date updatedDate = new Date(coinRequest.getUpdated().getTime()
                        - TimeUnit.HOURS.toMillis(Constants.SERVER_TIMEZONE_OFFSET));
                long lastTime = updatedDate.getTime();
                long now = (new Date()).getTime();

                long countDownTime = now - lastTime;

                if (countDownTime < REQUEST_MINIMUN_INTERVAL) {

                    countdownContainer.setVisibility(View.VISIBLE);

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

                            countdownContainer.setVisibility(View.GONE);
                            requestContainer.setVisibility(View.VISIBLE);
                        }
                    }.start();

                } else {
                    requestContainer.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getActivity().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void hideContents() {
        countdownContainer.setVisibility(View.GONE);
        requestContainer.setVisibility(View.GONE);
    }

    private void showContents() {
        countdownContainer.setVisibility(View.VISIBLE);
        requestContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
