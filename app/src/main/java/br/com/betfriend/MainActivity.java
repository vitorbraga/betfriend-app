package br.com.betfriend;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.fragments.AccomplishmentsFragment;
import br.com.betfriend.fragments.HistoryFragment;
import br.com.betfriend.fragments.HomeFragment;
import br.com.betfriend.fragments.InvitesFragment;
import br.com.betfriend.fragments.RankingFragment;
import br.com.betfriend.fragments.RequestCoinsFragment;
import br.com.betfriend.fragments.SettingsFragment;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.Constants;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private UserDataDTO mUserData;

    private DrawerLayout mDrawerLayout;

    private NavigationView mNavigationView;

    private ProgressBar mProgressBar;

    private FrameLayout mFrameLayout;

    private String mPersonId;

    final Handler mHandler = new Handler();

    private static final long INTERVAL = 1 * 60 * 1000;

    Runnable mUserDataRunnable = new Runnable() {

        @Override
        public void run() {

            try {
                //do your code here
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                final String personId = sharedPref.getString("PERSON_ID", "");

                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .create();

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(Constants.SERVER_API_BASE_URI)
                        .setConverter(new GsonConverter(gson)).build();

                ServerApi api = restAdapter.create(ServerApi.class);

                api.getUserData(Constants.SERVER_KEY, "application/json", personId, new Callback<UserDataDTO>() {

                    @Override
                    public void success(UserDataDTO user, Response response) {

                        if (mNavigationView != null) {

                            View headerView = mNavigationView.getHeaderView(0);

                            TextView pointsTextView = (TextView) headerView.findViewById(R.id.header_user_points);
                            pointsTextView.setText(getString(R.string.user_points, user.getPoints().toString()));

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("PERSON_ID", user.getPersonId());
                            editor.putString("PERSON_PHOTO", user.getPersonPhoto());
                            editor.putString("PERSON_NAME", user.getPersonName());
                            editor.putInt("POINTS", user.getPoints());
                            editor.putBoolean("key_visible", user.isVisible());
                            editor.commit();

                            mUserData = user;
                            android.app.FragmentManager fragmentManager = getFragmentManager();
                            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
                            if (currentFragment instanceof HomeFragment) {
                                ((HomeFragment) currentFragment).setUserData(mUserData);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //also call the same runnable to call it at regular interval
                mHandler.postDelayed(this, INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPersonId = sharedPref.getString("PERSON_ID", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        View parentLayout = findViewById(android.R.id.content);
        boolean betFinished = getIntent().getBooleanExtra("BET_COMPLETED", false);
        if (betFinished) {
            Snackbar snack = Snackbar.make(parentLayout, "Aposta realizada com sucesso!", Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.close), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).setActionTextColor(ContextCompat.getColor(getApplication(), R.color.app_yellow));
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            view.setLayoutParams(params);
            view.setBackground(getDrawable(R.color.app_green_start));
            snack.show();
        }

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.main_progressbar);

        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);

        // Start bet invitation checker service
        Intent i = new Intent(this, BetInvitationService.class);
        startService(i);

        // Start timer interval to check user data
        mHandler.postDelayed(mUserDataRunnable, INTERVAL);
    }

    @Override
    protected void onResume() {

        super.onResume();

        mFrameLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_API_BASE_URI)
                .setConverter(new GsonConverter(gson)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getUserData(Constants.SERVER_KEY, "application/json", mPersonId, new Callback<UserDataDTO>() {

            @Override
            public void success(UserDataDTO user, Response response) {

                mUserData = user;

                View headerView = mNavigationView.getHeaderView(0);

                TextView nameTextView = (TextView) headerView.findViewById(R.id.header_user_name);
                nameTextView.setText(mUserData.getPersonName());

                TextView pointsTextView = (TextView) headerView.findViewById(R.id.header_user_points);
                pointsTextView.setText(getString(R.string.user_points, mUserData.getPoints().toString()));

                ImageView personPhotoImageView = (ImageView) headerView.findViewById(R.id.header_user_photo);
                Picasso.with(getApplicationContext())
                        .load(mUserData.getPersonPhoto())
                        .transform(new CircleTransformation())
                        .into(personPhotoImageView);

                Bundle bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", mUserData);

                mProgressBar.setVisibility(View.GONE);
                mFrameLayout.setVisibility(View.VISIBLE);

                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.Fragment fragment = new HomeFragment();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                if(getSupportActionBar() != null){
                    getSupportActionBar().setTitle(getString(R.string.drawer_home));
                }
                mNavigationView.getMenu().getItem(0).setChecked(true);

            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Close application when pressing back

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if(!mNavigationView.getMenu().getItem(0).isChecked()) {
                onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
                mNavigationView.getMenu().getItem(0).setChecked(true);
            } else {
                moveTaskToBack(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        android.app.FragmentManager fragmentManager = getFragmentManager();

        android.app.Fragment fragment = null;
        String title = "";
        Bundle bundle = null;
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", mUserData);
                fragment.setArguments(bundle);
                title = getString(R.string.drawer_home);
                break;
            case R.id.nav_invites:
                fragment = new InvitesFragment();
                bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", mUserData);
                fragment.setArguments(bundle);
                title = getString(R.string.drawer_invites);
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", mUserData);
                fragment.setArguments(bundle);
                title = getString(R.string.drawer_history);
                break;
            case R.id.nav_rankings:
                fragment = new RankingFragment();
                title = getString(R.string.drawer_rankings);
                break;
            case R.id.nav_prizes:
                fragment = new AccomplishmentsFragment();
                bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", mUserData);
                fragment.setArguments(bundle);
                title = getString(R.string.drawer_prizes);
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                title = getString(R.string.drawer_settings);
                break;
            case R.id.request_coins:
                fragment = new RequestCoinsFragment();
                title = getString(R.string.drawer_request_coins);
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        toolbar.setTitle(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
