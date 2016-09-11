package br.com.betfriend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class StartBetActivity extends Activity {

    private String betOption;
    private String matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_bet);

        Intent intent = getIntent();
        betOption = intent.getStringExtra("BET_OPTION_EXTRA");
        matchId = intent.getStringExtra("MATCH_ID_EXTRA");

        Toast.makeText(getApplicationContext(), matchId, Toast.LENGTH_SHORT).show();
    }
}
