package br.com.betfriend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class StartBetActivity extends Activity {

    private String mBetOption;

    private String mMatchId;

    private int mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_bet);

        Intent intent = getIntent();
        mBetOption = intent.getStringExtra("BET_OPTION_EXTRA");
        mMatchId = intent.getStringExtra("MATCH_ID_EXTRA");
        mAmount = intent.getIntExtra("AMOUNT", -1);

        Toast.makeText(getApplicationContext(), mAmount+"", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), mMatchId, Toast.LENGTH_SHORT).show();
    }
}
