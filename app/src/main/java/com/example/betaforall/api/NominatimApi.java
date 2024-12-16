package com.example.betaforall.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimApi {
    @GET("search")
    Call<List<NominatimResponse>> search(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("countrycodes") String countryCodes
    );

}
