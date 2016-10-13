package br.com.betfriend.model;

import java.util.Date;

/**
 * Created by vitorbr on 13/10/16.
 */
public class CoinRequest {

    private String personId;

    private Date created;

    private Date updated;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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
