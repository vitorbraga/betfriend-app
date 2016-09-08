package br.com.betfriend.api;

import java.util.ArrayList;

import br.com.betfriend.model.SoccerMatch;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ServerApi {

    @POST("/signup/")
    void signup(@Field("email") String email, @Field("personName") String personName, @Field("personPhoto") String personPhoto);


}
