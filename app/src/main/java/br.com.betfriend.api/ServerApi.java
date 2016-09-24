package br.com.betfriend.api;

import java.util.ArrayList;

import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.SoccerMatch;
import br.com.betfriend.model.UserDataDTO;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ServerApi {


    @POST("/users/signup/")
    @FormUrlEncoded
    void signup(@Field("personId") String personId, @Field("email") String email, @Field("personName") String personName,
                @Field("personPhoto") String personPhoto, @Field("idToken") String idToken,
                Callback<UserDataDTO> response);

    @GET("/users/searchFriend/{personName}")
    void searchFriend(@Header("Content-Type") String contentType, @Path("personName") String personName,
                Callback<ArrayList<UserDataDTO>> response);

    @GET("/users/getUserData/{personId}")
    void getUserData(@Header("Content-Type") String contentType, @Path("personId") String personId,
                      Callback<UserDataDTO> response);

    @POST("/bets/inviteToBet/")
    @FormUrlEncoded
    void inviteToBet(@Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson, @Field("matchId") String matchId,
                @Field("option") String option, @Field("amount") Integer amount,
                Callback<JsonResponse> response);

    @POST("/bets/acceptBet/")
    @FormUrlEncoded
    void acceptBet(@Field("betId") String betId, @Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson,
                   @Field("matchId") String matchId, @Field("option") String option, @Field("amount") Integer amount,
                   Callback<JsonResponse> response);

    @POST("/bets/refuseBet/")
    @FormUrlEncoded
    void refuseBet(@Field("betId") String betId, @Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson,
                   @Field("matchId") String matchId, @Field("amount") Integer amount,
                   Callback<JsonResponse> response);

    @GET("/bets/getPendingBets/{personId}")
    void getPendingBets(@Header("Content-Type") String contentType, @Path("personId") String personId,
                      Callback<ArrayList<Bet>> response);

    @GET("/bets/getFinishedBets/{personId}")
    void getFinishedBets(@Header("Content-Type") String contentType, @Path("personId") String personId,
                        Callback<ArrayList<Bet>> response);

    @GET("/bets/getAllBets/{personId}")
    void getAllBets(@Header("Content-Type") String contentType, @Path("personId") String personId,
                         Callback<ArrayList<Bet>> response);

    @GET("/matches/getMatches/")
    void getMatches(@Header("Content-Type") String contentType, Callback<ArrayList<Match>> response);

    @GET("/bets/getNewBetInvites/{personId}")
    void getNewBetInvites(@Header("Content-Type") String contentType, @Path("personId") String personId,
                          Callback<ArrayList<Bet>> response);

    @GET("/bets/getBetInvites/{personId}")
    void getBetInvites(@Header("Content-Type") String contentType, @Path("personId") String personId,
                          Callback<ArrayList<Bet>> response);

    @POST("/bets/invitationViewed/")
    @FormUrlEncoded
    void invitationViewed(@Field("personId") String personId, Callback<JsonResponse> response);
}
