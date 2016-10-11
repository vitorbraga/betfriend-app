package br.com.betfriend.model;

import android.databinding.Bindable;

import java.io.Serializable;

import br.com.betfriend.BR;

public class UserDataDTO extends JsonResponse implements Serializable {

    private String personId;

    private String email;

    private String personName;

    private String personPhoto;

    private String idToken;

    private boolean visible;

    private Integer points;

    private Integer betsWon;

    private Integer invitesAccepted;

    private Integer invitesMade;

    private Integer podium;

    private Integer goldMedal;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonPhoto() {
        return personPhoto;
    }

    public void setPersonPhoto(String personPhoto) {
        this.personPhoto = personPhoto;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Integer getBetsWon() {
        return betsWon;
    }

    public void setBetsWon(Integer betsWon) {
        this.betsWon = betsWon;
    }

    public Integer getInvitesAccepted() {
        return invitesAccepted;
    }

    public void setInvitesAccepted(Integer invitesAccepted) {
        this.invitesAccepted = invitesAccepted;
    }

    public Integer getInvitesMade() {
        return invitesMade;
    }

    public void setInvitesMade(Integer invitesMade) {
        this.invitesMade = invitesMade;
    }

    public Integer getPodium() {
        return podium;
    }

    public void setPodium(Integer podium) {
        this.podium = podium;
    }

    public Integer getGoldMedal() {
        return goldMedal;
    }

    public void setGoldMedal(Integer goldMedal) {
        this.goldMedal = goldMedal;
    }

    @Bindable
    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
        notifyPropertyChanged(BR.points);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
