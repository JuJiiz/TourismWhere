package com.tourismwhere.tourismwhere.network

import com.tourismwhere.tourismwhere.model.AttractionsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("tourist/attractions/search")
    fun getAttractions(@Query("token") token: String, @Query("ll") ll: String, @Query("radius") radius: String): Observable<AttractionsResponse>
}