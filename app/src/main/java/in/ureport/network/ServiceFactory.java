package in.ureport.network;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import in.ureport.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {

    public static Retrofit.Builder build(String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl);
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES);
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            clientBuilder.addInterceptor(interceptor);
            builder.client(clientBuilder.build());
        }
        return builder;
    }

    public static <T> T create(Class<T> service, String baseUrl, Gson converter) {
        final Retrofit.Builder builder = build(fixBaseUrlIfNeeded(baseUrl));
        builder.addConverterFactory(GsonConverterFactory.create(converter));
        return builder.build().create(service);
    }

    public static <T> T create(Class<T> service, String baseUrl) {
        final Retrofit.Builder builder = build(fixBaseUrlIfNeeded(baseUrl));
        builder.addConverterFactory(GsonConverterFactory.create());
        return builder.build().create(service);
    }

    private static String fixBaseUrlIfNeeded(String baseUrl) {
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl.concat("/");
        }
        return baseUrl;
    }

}
