package br.com.betfriend;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.betfriend.databinding.ActivityMainBinding;
import br.com.betfriend.fragments.HistoryFragment;
import br.com.betfriend.fragments.HomeFragment;
import br.com.betfriend.fragments.PrizesFragment;
import br.com.betfriend.fragments.RankingFragment;
import br.com.betfriend.fragments.SettingsFragment;
import br.com.betfriend.fragments.StoreFragment;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private UserDataDTO userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        userData = (UserDataDTO) getIntent().getSerializableExtra("USER_DATA_EXTRA");

        binding.setUser(userData);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView nameTextView = (TextView) headerView.findViewById(R.id.header_user_name);
        nameTextView.setText(userData.getPersonName());

        TextView pointsTextView = (TextView) headerView.findViewById(R.id.header_user_points);
        pointsTextView.setText(userData.getPoints().toString());

        ImageView personPhotoImageView = (ImageView) headerView.findViewById(R.id.header_user_photo);
        Picasso.with(this)
                .load(userData.getPersonPhoto())
                .transform(new CircleTransformation())
                .into(personPhotoImageView);

        Bundle bundle = new Bundle();
        bundle.putSerializable("USER_DATA_EXTRA", userData);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        getSupportActionBar().setTitle(getString(R.string.drawer_home));
    }

    @Override
    protected void onResume() {
        super.onResume();

//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(Constants.SERVER_API_BASE_URI).build();
//
//        ServerApi api = restAdapter.create(ServerApi.class);
//
//        api.getUserData("application/json", userData.getEmail(), new Callback<UserDataDTO>() {
//
//            @Override
//            public void success(UserDataDTO userDataDTO, Response response) {
//
//                Toast.makeText(getApplication(), "userdata Sucess", Toast.LENGTH_SHORT).show();
//                userData = userDataDTO;
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Toast.makeText(getApplication(), "Fail", Toast.LENGTH_SHORT).show();
//            }
//        });
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
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = null;
        String title = "";

        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("USER_DATA_EXTRA", userData);
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
