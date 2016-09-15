package br.com.betfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;

import br.com.betfriend.model.SoccerMatch;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.ConvertHelper;
import br.com.betfriend.utils.TeamsDataEnum;

public class StartBetActivity extends AppCompatActivity {

    private String mBetOption;

    private String mMatchId;

    private SoccerMatch mMatch;

    private UserDataDTO mUserData;

    private int mAmount;

    private TextView matchTime, homeTeamLabel, awayTeamLabel;
    private RadioButton mHomeButton, mAwayButton, mDrawButton;
    private SeekBar mSeekBar;
    private EditText mBetValue;
    private ImageView homeLogo, awayLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_bet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.confirm_bet));

        Intent intent = getIntent();
        mBetOption = intent.getStringExtra("BET_OPTION_EXTRA");
        mMatchId = intent.getStringExtra("MATCH_ID_EXTRA");
        mAmount = intent.getIntExtra("AMOUNT", -1);
        mMatch = (SoccerMatch) intent.getSerializableExtra("MATCH_EXTRA");
        mUserData = (UserDataDTO) intent.getSerializableExtra("USER_DATA_EXTRA");

        String homeTeam = mMatch.getHomeTeam().trim();
        String awayTeam = mMatch.getAwayTeam().trim();

        matchTime = (TextView) findViewById(R.id.match_time);
        Long tsTamp = mMatch.getTstamp();
        Date date = new Date(1000 * tsTamp);
        matchTime.setText(ConvertHelper.dateToView(date));

        homeTeamLabel = (TextView) findViewById(R.id.home_team);
        awayTeamLabel = (TextView) findViewById(R.id.away_team);

        homeTeamLabel.setText(TeamsDataEnum.get(homeTeam).label());
        awayTeamLabel.setText(TeamsDataEnum.get(awayTeam).label());

        homeLogo = (ImageView) findViewById(R.id.home_logo);
        awayLogo = (ImageView) findViewById(R.id.away_logo);

        Picasso.with(this)
                .load(TeamsDataEnum.get(homeTeam).logo())
                .fit()
                .into(homeLogo);

        Picasso.with(this)
                .load(TeamsDataEnum.get(awayTeam).logo())
                .fit()
                .into(awayLogo);

        mHomeButton = (RadioButton) findViewById(R.id.radio_team_1);
        mHomeButton.setText(TeamsDataEnum.get(homeTeam).correctName());

        mAwayButton = (RadioButton) findViewById(R.id.radio_team_2);
        mAwayButton.setText(TeamsDataEnum.get(awayTeam).correctName());

        mDrawButton = (RadioButton) findViewById(R.id.radio_draw);

        if(mBetOption.equals("1")) {
            mHomeButton.setChecked(true);
        } else if(mBetOption.equals("2")) {
            mAwayButton.setChecked(true);
        } else {
            mDrawButton.setChecked(true);
        }

        mBetValue = (EditText) findViewById(R.id.bet_value);
        mBetValue.setText(mAmount + "");

        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setProgress(mAmount);
        mSeekBar.setMax(mUserData.getPoints());

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                mBetValue.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mBetValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equals("")) {
                    mSeekBar.setProgress(0);
                } else {
                    mSeekBar.setProgress(Integer.parseInt(s.toString()));
                }
            }

        });

    }
}
