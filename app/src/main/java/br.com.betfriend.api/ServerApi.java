package br.com.betfriend.api;

import java.util.ArrayList;

import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.Match;
import br.com.betfriend.model.Ranking;
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
    void signup(@Header("Server-Key") String serverKey, @Field("personId") String personId, @Field("email") String email, @Field("personName") String personName,
                @Field("personPhoto") String personPhoto, @Field("idToken") String idToken,
                Callback<UserDataDTO> response);

    @GET("/users/searchFriend/{personName}/{userId}")
    void searchFriend(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personName") String personName,
                      @Path("userId") String userId, Callback<ArrayList<UserDataDTO>> response);

    @GET("/users/getUserData/{personId}")
    void getUserData(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personId") String personId,
                      Callback<UserDataDTO> response);

    @POST("/bets/inviteToBet/")
    @FormUrlEncoded
    void inviteToBet(@Header("Server-Key") String serverKey, @Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson, @Field("matchId") String matchId,
                @Field("option") String option, @Field("amount") Integer amount,
                Callback<JsonResponse> response);

    @POST("/bets/acceptBet/")
    @FormUrlEncoded
    void acceptBet(@Header("Server-Key") String serverKey, @Field("betId") String betId, @Field("srcPerson") String srcPerson, @Field("destPerson") String destPerson,
                   @Field("matchId") String matchId, @Field("option") String option, @Field("amount") Integer amount,
                   Callback<JsonResponse> response);

    @POST("/bets/refuseBet/")
    @FormUrlEncoded
    void refuseBet(@Header("Server-Key") String serverKey, @Field("betId") String betId, Callback<JsonResponse> response);

    @GET("/bets/getPendingBets/{personId}")
    void getPendingBets(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personId") String personId,
                      Callback<ArrayList<Bet>> response);

    @GET("/bets/getFinishedBets/{personId}")
    void getFinishedBets(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personId") String personId,
                        Callback<ArrayList<Bet>> response);

    @GET("/bets/getAllBets/{personId}")
    void getAllBets(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personId") String personId,
                         Callback<ArrayList<Bet>> response);

    @GET("/matches/getMatches/")
    void getMatches(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, Callback<ArrayList<Match>> response);

    @GET("/bets/getNewBetInvites/{personId}")
    void getNewBetInvites(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personId") String personId,
                          Callback<ArrayList<Bet>> response);

    @GET("/bets/getBetInvites/{personId}")
    void getBetInvites(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, @Path("personId") String personId,
                          Callback<ArrayList<Bet>> response);

    @POST("/bets/invitationViewed/")
    @FormUrlEncoded
    void invitationViewed(@Header("Server-Key") String serverKey, @Field("personId") String personId, Callback<JsonResponse> response);

    @GET("/rankings/getLastWeekRanking/")
    void getLastWeekRanking(@Header("Server-Key") String serverKey, @Header("Content-Type") String contentType, Callback<ArrayList<Ranking>> response);

    @POST("/users/setBetVisibility/")
    @FormUrlEncoded
    void setBetVisibility(@Header("Server-Key") String serverKey, @Field("personId") String personId, @Field("visible") boolean visible,
                   Callback<JsonResponse> response);

    @POST("/users/removeAccount/")
    @FormUrlEncoded
    void removeAccount(@Header("Server-Key") String serverKey, @Field("personId") String personId, Callback<JsonResponse> response);
}
