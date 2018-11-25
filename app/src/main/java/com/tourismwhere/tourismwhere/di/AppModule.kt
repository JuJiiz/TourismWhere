package com.tourismwhere.tourismwhere.di

import android.app.Application
import android.content.Context
import com.tourismwhere.tourismwhere.Constant.LOADING_TIMEOUT_SECONDS
import com.tourismwhere.tourismwhere.Constant.ENDPOINT
import com.tourismwhere.tourismwhere.network.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Provides
    @Production
    @Singleton
    fun provideApiServiceProduction(client: OkHttpClient): ApiService =
        Retrofit.Builder().baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().readTimeout(LOADING_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
                .connectTimeout(LOADING_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
                .build()


    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context = application.applicationContext


}
