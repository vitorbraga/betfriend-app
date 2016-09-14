package br.com.betfriend.api;

import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.UserDataDTO;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

public interface ServerApi {


    @POST("/signup/")
    @FormUrlEncoded
    void signup(@Field("personId") String personId, @Field("email") String email, @Field("personName") String personName,
                @Field("personPhoto") String personPhoto, @Field("idToken") String idToken,
                Callback<UserDataDTO> response);

    @GET("/searchFriend/{personName}")
    void searchFriend(@Header("Content-Type") String contentType, @Field("personName") String personName,
                Callback<JsonResponse> response);

    @GET("/getUserData/{personId}")
    void getUserData(@Header("Content-Type") String contentType, @Field("personId") String personId,
                      Callback<UserDataDTO> response);


    @POST("/inviteToBet/")
    @FormUrlEncoded
    void inviteToBet(@Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson, @Field("matchId") String matchId,
                @Field("option") String option, @Field("amount") Integer amount,
                Callback<JsonResponse> response);

    @POST("/acceptBet/")
    @FormUrlEncoded
    void acceptBet(@Field("betId") String betId, @Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson,
                   @Field("matchId") String matchId, @Field("option") String option, @Field("amount") Integer amount,
                   Callback<JsonResponse> response);

    @POST("/refuseBet/")
    @FormUrlEncoded
    void refuseBet(@Field("betId") String betId, @Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson,
                   @Field("matchId") String matchId, @Field("amount") Integer amount,
                   Callback<JsonResponse> response);

}
