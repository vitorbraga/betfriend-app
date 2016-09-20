package br.com.betfriend.model;

import java.util.Date;

/**
 * Created by vitorbr on 16/09/16.
 */
public class Bet {

    private String _id;

    private String srcPerson;

    private String destPerson;

    private Integer status;

    private String option;

    private Integer matchId;

    private Integer amount;

    private Date created;

    private Date updated;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSrcPerson() {
        return srcPerson;
    }

    public void setSrcPerson(String srcPerson) {
        this.srcPerson = srcPerson;
    }

    public String getDestPerson() {
        return destPerson;
    }

    public void setDestPerson(String destPerson) {
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
}
