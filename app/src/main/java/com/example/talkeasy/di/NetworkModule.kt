package com.example.talkeasy.di

import com.example.talkeasy.gemini.GeminiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dagger Hiltのモジュール。
 * ネットワーク関連の依存性（Retrofit、APIサービスなど）をDIコンテナに提供する。
 */
@Module
@InstallIn(SingletonComponent::class) // アプリケーション全体で単一のインスタンスを提供
object NetworkModule {

    /**
     * Retrofitのシングルトンインスタンスを提供する。
     * Retrofitは、HTTPリクエストを簡単に行うためのライブラリ。
     * @return Retrofitのインスタンス
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gemini-server-sigma.vercel.app/") // APIのベースURL
            .addConverterFactory(GsonConverterFactory.create()) // JSONをKotlinオブジェクトに変換するためのコンバーター
            .build()
    }

    /**
     * GeminiApiServiceのシングルトンインスタンスを提供する。
     * このサービスは、Retrofitによって実装され、Gemini APIとの通信を行う。
     * @param retrofit Retrofitのインスタンス
     * @return GeminiApiServiceのインスタンス
     */
    @Provides
    @Singleton
    fun provideGeminiApi(retrofit: Retrofit): GeminiApiService {
        return retrofit.create(GeminiApiService::class.java)
    }
}
