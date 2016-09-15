package br.com.betfriend.model;

import java.io.Serializable;

/**
 * Created by vitorbr on 08/09/16.
 */
public class SoccerMatch implements Serializable{

    private Integer matchId;

    private String leagueName;

    private String homeTeam;

    private String awayTeam;

    private Long tstamp;

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Long getTstamp() {
        return tstamp;
    }

    public void setTstamp(Long tstamp) {
        this.tstamp = tstamp;
    }
}
