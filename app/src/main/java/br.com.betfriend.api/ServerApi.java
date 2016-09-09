package br.com.betfriend.api;

import java.util.ArrayList;

import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.SoccerMatch;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ServerApi {

    @POST("/signup/")
    @FormUrlEncoded
    void signup(@Field("email") String email, @Field("personName") String personName,
                @Field("personPhoto") String personPhoto, @Field("idToken") String idToken,
                Callback<JsonResponse> response);

    @GET("/searchFriend/{personName}")
    void signup(@Header("Content-Type") String contentType, @Field("personName") String personName,
                Callback<JsonResponse> response);


}
