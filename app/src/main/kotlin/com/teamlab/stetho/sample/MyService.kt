package com.teamlab.stetho.sample

import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface MyService {
    @GET("login")
    fun login(@Query("email") email: String, @Query("password") password: String): Observable<String>
}
