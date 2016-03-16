package <%= appPackage %>.environment;

import android.app.Application
import android.content.Context
import android.support.annotation.NonNull

import <%= appPackage %>.network.OkHttpInterceptors
import <%= appPackage %>.network.OkHttpNetworkInterceptors

import com.google.gson.Gson
import javax.inject.Singleton
import <%= appPackage %>.di.ForApplication
import <%= appPackage %>.BuildConfig
import <%= appPackage %>.application.App
<% if (mixpanel == true) { %>import com.mixpanel.android.mpmetrics.MixpanelAPI<% } %>

import dagger.Module
import dagger.Provides

import java.util.List
import java.io.File
import java.util.concurrent.TimeUnit

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@Module
class EnvironmentModule(val app: App) {

    val DISK_CACHE_SIZE = 1_000_000L.toInt()

    @Provides
    @Singleton
    fun provideOkHttpClient(@ForApplication app: Context,
                                     @OkHttpInterceptors @NonNull interceptors: List<Interceptor>,
                                     @OkHttpNetworkInterceptors @NonNull networkInterceptors: List<Interceptor>): OkHttpClient {

        val cacheDir = File(app.getCacheDir(), "http")
        val cache = Cache(cacheDir, DISK_CACHE_SIZE)

        val okHttpBuilder = OkHttpClient.Builder()

        for (interceptor in interceptors) {
            okHttpBuilder.addInterceptor(interceptor)
        }

        for (networkInterceptor in networkInterceptors) {
            okHttpBuilder.addNetworkInterceptor(networkInterceptor)
        }

        okHttpBuilder.cache(cache)
        okHttpBuilder.readTimeout(30, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(30, TimeUnit.SECONDS)
        okHttpBuilder.connectTimeout(30, TimeUnit.SECONDS)

        return okHttpBuilder.build()

    }

    @Provides
    @Singleton
    fun provideRestAdapter(val okHttpClient: OkHttpClient, val gson: Gson): Retrofit  {
        val restAdapter = Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT_LOCAL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return restAdapter
    }

    <% if (mixpanel == true) { %>@Provides
    @Singleton
    fun provideMixpanelApi(@ForApplication application: Application): MixpanelAPI{
        val mixpanel = MixpanelAPI.getInstance(application, "token")
        return mixpanel
    }<% } %>

}
