package br.com.betfriend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.com.betfriend.api.ServerApi;
import br.com.betfriend.fragments.HistoryFragment;
import br.com.betfriend.fragments.HomeFragment;
import br.com.betfriend.fragments.PrizesFragment;
import br.com.betfriend.fragments.RankingFragment;
import br.com.betfriend.fragments.SettingsFragment;
import br.com.betfriend.fragments.StoreFragment;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private UserDataDTO mUserData;

    private NavigationView mNavigationView;

    private ProgressBar mProgressBar;

    private FrameLayout mFrameLayout;

    private String mPersonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mPersonId = sharedPref.getString("PERSON_ID", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.main_progressbar);

        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);

    }

    @Override
    protected void onResume() {

        super.onResume();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.server_uri)).build();

        ServerApi api = restAdapter.create(ServerApi.class);

        api.getUserData("application/json", mPersonId, new Callback<UserDataDTO>() {

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
                getSupportActionBar().setTitle(getString(R.string.drawer_home));
                mNavigationView.getMenu().getItem(0).setChecked(true);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplication(), "retorfit erro user", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Close application when pressing back
        moveTaskToBack(true);
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

        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", mUserData);
                fragment.setArguments(bundle);
                title = getString(R.string.drawer_home);
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                title = getString(R.string.drawer_history);
                break;
            case R.id.nav_rankings:
                fragment = new RankingFragment();
                title = getString(R.string.drawer_rankings);
                break;
            case R.id.nav_prizes:
                fragment = new PrizesFragment();
                title = getString(R.string.drawer_prizes);
                break;
            case R.id.nav_store:
                fragment = new StoreFragment();
                title = getString(R.string.drawer_store);
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                title = getString(R.string.drawer_settings);
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        toolbar.setTitle(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
