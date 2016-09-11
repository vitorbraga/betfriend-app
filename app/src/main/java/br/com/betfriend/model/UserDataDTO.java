package br.com.betfriend.model;

import android.databinding.Bindable;

import java.io.Serializable;

import br.com.betfriend.BR;

public class UserDataDTO extends JsonResponse implements Serializable {

    private String email;

    private String personName;

    private String personPhoto;

    private String idToken;

    private Integer points;

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

    @Bindable
    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
        notifyPropertyChanged(BR.points);
    }
}
