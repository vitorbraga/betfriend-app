package br.com.betfriend.utils;

import br.com.betfriend.R;

/**
 * Created by vitorbr on 27/09/16.
 */
public class GamificationUtils {


    public static int getUpperBound(int arcId, int value) {

        int upperBound = 0;
        switch(arcId) {

            case R.id.bets_won:

                if(0 <= value && value < 10) {
                    upperBound = 10;
                } else if(10 <= value && value < 100) {
                    upperBound = 100;
                } else {
                    upperBound = 500;
                }

                break;

            case R.id.invites_made:

                if(0 <= value && value < 10) {
                    upperBound = 10;
                } else if(10 <= value && value < 100) {
                    upperBound = 100;
                } else {
                    upperBound = 500;
                }
                break;

            case R.id.invites_accepted:
                if(0 <= value && value < 10) {
                    upperBound = 10;
                } else if(10 <= value && value < 100) {
                    upperBound = 100;
                } else {
                    upperBound = 500;
                }
                break;

            case R.id.golden_medal:
                if(0 <= value && value < 1) {
                    upperBound = 1;
                } else if(1 <= value && value < 5) {
                    upperBound = 5;
                } else {
                    upperBound = 10;
                }
                break;

            case R.id.podium:
                if(0 <= value && value < 10) {
                    upperBound = 10;
                } else if(10 <= value && value < 50) {
                    upperBound = 50;
                } else {
                    upperBound = 150;
                }
                break;
        }

        return upperBound;
    }
}
