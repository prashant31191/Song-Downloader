package com.sdsmdg.harjot.AZMusic.interfaces;

import com.sdsmdg.harjot.AZMusic.Config;
import com.sdsmdg.harjot.AZMusic.models.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;
import retrofit2.http.GET;

/**
 * Created by Harjot on 30-Apr-16.
 */
public interface StreamService {
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getTracks(@Query("q") String query, @Query("limit") int limit);
}
