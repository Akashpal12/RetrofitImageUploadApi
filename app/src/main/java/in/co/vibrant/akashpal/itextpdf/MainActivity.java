package in.co.vibrant.akashpal.itextpdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int READ_STORAGE_PERMISSION_REQUEST = 2;

    private Button selectImageButton;

    private APIServices apiService;
    EditText enterGmail;
    TextView response_Text;
    ImageView image_view2;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectImageButton = findViewById(R.id.select_image_button);
        enterGmail = findViewById(R.id.enterGmail);
        response_Text = findViewById(R.id.response_Text);
        progress_bar = findViewById(R.id.progress_bar);
        image_view2 = findViewById(R.id.image_view2);
        selectImageButton.setOnClickListener(v -> openGallery());
        progress_bar.setVisibility(View.GONE);

        // Request permission to access external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST);
        }

   /*     // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://forexmillionersclub.com/") // Replace with your API base URL
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        // Create API service
        apiService = retrofit.create(APIServices.class);*/
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            sendImageAndEmail(imageUri);
            image_view2.setImageURI(imageUri);

        }
    }

    private void sendImageAndEmail(Uri imageUri) {
        progress_bar.setVisibility(View.VISIBLE);

        String imagePath = RealPathUtil.getRealPathFromURI_API19(this, imageUri);
        File imageFile = new File(imagePath);
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("profile_img", imageFile.getName(), imageRequestBody);

        RequestBody emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), enterGmail.getText().toString().trim());

       RetrofitClient.getClient().uploadData(imagePart, emailRequestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseBodyString = null;
                    try {
                        responseBodyString = response.body().string();
                    } catch (Exception e) {

                    }
                    JsonObject jsonObject = new Gson().fromJson(responseBodyString, JsonObject.class);
                    Toast.makeText(MainActivity.this, "" + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    response_Text.setText(jsonObject.toString());
                    progress_bar.setVisibility(View.GONE);


                } else {

                    Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(MainActivity.this, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with your logic
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}