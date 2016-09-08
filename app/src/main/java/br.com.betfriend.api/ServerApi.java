package br.com.betfriend.api;

import java.util.ArrayList;

import br.com.betfriend.model.SoccerMatch;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

public interface ServerApi {

    @GET("/leagues/{leagueId}")
    void getMatches(@Header("Content-Type") String contentType, @Header("X-Mashape-Key") String apiKey,
                    @Path("leagueId") String leagueId, Callback<ArrayList<SoccerMatch>> response);


}
