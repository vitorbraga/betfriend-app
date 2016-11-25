package br.com.betfriend.model;

import br.com.betfriend.utils.LeaguesEnum;

public class League {

    private LeaguesEnum league;

    private boolean selected;

    public League() {
    }

    public League(LeaguesEnum league, boolean selected) {
        this.league = league;
        this.selected = selected;
    }

    public LeaguesEnum getLeague() {
        return league;
    }

    public void setLeague(LeaguesEnum league) {
        this.league = league;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
