package com.earthquakealert.app.data.remote.api

import com.earthquakealert.app.data.remote.dto.BmkgAutoResponseDto
import com.earthquakealert.app.data.remote.dto.BmkgListResponseDto
import retrofit2.http.GET

interface BmkgApiService {

    @GET("DataMKG/TEWS/autogempa.json")
    suspend fun getAutoGempa(): BmkgAutoResponseDto

    @GET("DataMKG/TEWS/gempaterkini.json")
    suspend fun getRecentGempa(): BmkgListResponseDto

    @GET("DataMKG/TEWS/gempadirasakan.json")
    suspend fun getFeltGempa(): BmkgListResponseDto
}
