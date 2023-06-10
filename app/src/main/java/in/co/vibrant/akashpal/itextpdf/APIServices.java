package in.co.vibrant.akashpal.itextpdf;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIServices {

        @FormUrlEncoded
        @POST("koi/updateapi/imageapi.php")
        Call<JsonObject> postData(
                @Field("param1") String Image,
                @Field("param2") String Email
        );

        @Multipart
        @POST("koi/updateapi/imageapi.php")
        Call<ResponseBody> uploadData(
                @Part MultipartBody.Part image,
                @Part("email") RequestBody email
        );

}
