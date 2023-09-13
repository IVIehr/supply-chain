package ir.alimojahed.general.elasticwrapper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created By Khojasteh on 2/25/2019
 */
public class RetrofitUtil {


    public static Retrofit notificationRetrofit = null;
    public static Retrofit restCountriesRetrofit = null;
    public static Retrofit elasticRetrofit = null;


    public static synchronized Retrofit getInstanceWithTimeOut(Retrofit retrofit, String baseUrl,
                                                               int connectTimeout, int readTimeout) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new MyDateTypeAdapter())
                    .create();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();

        }

        return retrofit;
    }


    public static synchronized Retrofit getInstance(Retrofit retrofit, String baseUrl) {

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new MyDateTypeAdapter())
//                    .addSerializationExclusionStrategy(new SuperclassExclusionStrategy())
//                    .addDeserializationExclusionStrategy(new SuperclassExclusionStrategy())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

    public static <T> Response<T> request(Call<T> addContactService) throws IOException {
        return addContactService.execute();
    }

}
