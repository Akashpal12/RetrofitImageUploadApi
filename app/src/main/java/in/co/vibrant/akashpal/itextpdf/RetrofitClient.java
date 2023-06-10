package in.co.vibrant.akashpal.itextpdf;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    public static APIServices getClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://forexmillionersclub.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIServices apiService = retrofit.create(APIServices.class);
        return apiService;
    }
}
