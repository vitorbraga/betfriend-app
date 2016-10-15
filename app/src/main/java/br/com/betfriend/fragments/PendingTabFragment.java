package br.com.betfriend.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import br.com.betfriend.R;
import br.com.betfriend.adapters.AnimatedExpandableListView;
import br.com.betfriend.adapters.BetsExpandableListAdapter;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.BetStatusEnum;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class PendingTabFragment extends Fragment {

    private AnimatedExpandableListView mBetsListView;

    private BetsExpandableListAdapter mAdapter;

    private ProgressBar mProgressBar;

    private LinearLayout mNoBetsFound;

    private UserDataDTO userData;

    private Button mRetryButton;

    Bet mCurrentBet = null;

    private Context mContext;

    private ActionMode mActionMode;

    int previousGroup = -1;

    public PendingTabFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tab_pending, container, false);

        userData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        mContext = getActivity();

        mBetsListView = (AnimatedExpandableListView) getView().findViewById(R.id.bet_list);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.bets_progressbar);

        mNoBetsFound = (LinearLayout) getView().findViewById(R.id.no_bets_container);

        mRetryButton = (Button) getView().findViewById(R.id.retry_button);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

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

        mBetsListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

                if (groupPosition == previousGroup) {
                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                }
            }
        });

        mBetsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (groupPosition != previousGroup) {
                    mBetsListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
                mCurrentBet = (Bet) mBetsListView.getItemAtPosition(groupPosition);

                if (mCurrentBet.getStatus() == BetStatusEnum.AWAITING_ACCEPTANCE.id()) {
                    // User can only cancel some bet if the match will happen at least one hour after
                    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                    mActionMode = toolbar.startActionMode(mActionModeCallback);
                }
            }
        });

        getPendingBets();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        super.onDetach();
    }

    public void getPendingBets() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String personId = sharedPref.getString("PERSON_ID", "");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getPendingBets(Constants.SERVER_KEY, "application/json", personId, new Callback<ArrayList<Bet>>() {

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

                    mRetryButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            getPendingBets();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mBetsListView.setVisibility(View.GONE);
                mNoBetsFound.setVisibility(View.VISIBLE);

                mRetryButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        getPendingBets();
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        super.onPause();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_cab, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Context context = getActivity();

            switch (item.getItemId()) {
                case R.id.action_delete:

                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.cancel_dialog_title))
                            .setMessage(context.getString(R.string.cancel_dialog_description))
                            .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    mProgressBar.setVisibility(View.VISIBLE);

                                    RestAdapter restAdapter = new RestAdapter.Builder()
                                            .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                                    final ServerApi api = restAdapter.create(ServerApi.class);

                                    api.cancelBet(Constants.SERVER_KEY, mCurrentBet.get_id(), new Callback<JsonResponse>() {

                                        @Override
                                        public void success(JsonResponse jsonResponse, Response response) {
                                            mProgressBar.setVisibility(View.GONE);

                                            getPendingBets();

                                            View parentLayout = getActivity().findViewById(android.R.id.content);
                                            Snackbar snack = Snackbar.make(parentLayout, "Aposta cancelada.", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("Fechar", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                        }
                                                    }).setActionTextColor(ContextCompat.getColor(mContext, R.color.app_yellow));

                                            View snackView = snack.getView();
                                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
                                            snackView.setLayoutParams(params);
                                            snackView.setBackground(mContext.getDrawable(R.color.app_green_start));
                                            snack.show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            mProgressBar.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "Erro inesperado.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();


                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

}
