package br.com.betfriend.model;

import java.util.Date;

/**
 * Created by vitorbr on 16/09/16.
 */
public class Bet {

    private String _id;

    private UserDataDTO srcPerson;

    private UserDataDTO destPerson;

    private Match match;

    private Integer status;

    private Integer result;

    private String option;

    private Integer matchId;

    private Integer amount;

    private boolean notified;

    private String notChosenOption;

    private Date created;

    private Date updated;

    private int checked;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public UserDataDTO getSrcPerson() {
        return srcPerson;
    }

    public void setSrcPerson(UserDataDTO srcPerson) {
        this.srcPerson = srcPerson;
    }

    public UserDataDTO getDestPerson() {
        return destPerson;
    }

    public void setDestPerson(UserDataDTO destPerson) {
        this.destPerson = destPerson;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public String getNotChosenOption() {
        return notChosenOption;
    }

    public void setNotChosenOption(String notChosenOption) {
        this.notChosenOption = notChosenOption;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
