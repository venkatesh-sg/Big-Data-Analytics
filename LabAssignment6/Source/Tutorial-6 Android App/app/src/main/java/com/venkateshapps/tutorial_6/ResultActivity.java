package com.venkateshapps.tutorial_6;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64DataException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;





public class ResultActivity extends AppCompatActivity {
    private ImageView mimageview;
    private TextView Result;
    public String TAG ="*********TAG******";
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mimageview =(ImageView) findViewById(R.id.RImage);
        Intent intent = getIntent();
        final String image_path= intent.getStringExtra("Image");
        final int selection= intent.getIntExtra("Selection",0);
        Uri fileUri = Uri.parse(image_path);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
            mimageview.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mimageview.setImageURI(fileUri);
        Result =(TextView) findViewById(R.id.RText);
        if(selection==1){
            SparkApi tasks =new SparkApi();
            tasks.execute(bitmap);
        }
        else{
            ClarifaiApi task=new ClarifaiApi();
            task.execute(bitmap);
        }
    }

    private class SparkApi extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... pic) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pic[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bbytes = stream.toByteArray();

            byte[] img = Base64.encode(bbytes,0);
            String urlstring ="http://192.168.1.229:8080/get_custom";
            String resultToDisplay = "";
            InputStream in = null;
            try {

                URL url = new URL(urlstring);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write(img.toString());
                osw.flush();
                osw.close();

                in = new BufferedInputStream(urlConnection.getInputStream());


            } catch (Exception e) {

                e.printStackTrace();

            }
            if(in != null)
                try {
                    resultToDisplay = convertInputStreamToString(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                resultToDisplay = "Did not work!";

            return resultToDisplay;

        }
        protected void onProgressUpdate() {

        }
        @Override
        protected void onPostExecute(String result) {
            Result.setText(result);
        }
    }

    private class ClarifaiApi extends AsyncTask<Bitmap, String, String> {
        @Override
        protected String doInBackground(Bitmap... pic) {
            String ClarifaiResult = "";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pic[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bbytes = stream.toByteArray();
            publishProgress("Image Converted to byte Array");
            final ClarifaiClient client = new ClarifaiBuilder("Ofr6hOnpopDamQ5fWhmargWwhJLxqFFIN5RipraE", "CzEnwp18RhGqHx_tYMq-whw_36gD1fZOqBcD8V-x")
                    .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                    .buildSync(); // or use .build() to get a Future<ClarifaiClient>
            client.getToken();

            ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(bbytes))).executeSync();
            publishProgress("Request Sent to ClarifaiAPI");
            List<ClarifaiOutput<Concept>> predictions = (List<ClarifaiOutput<Concept>>) response.get();
            List<Concept> data = predictions.get(0).data();
            publishProgress("Got response from ClarifaiAPI");
            for(int i=0;i<5;i++){                                            //Top 5 Annotations
                ClarifaiResult=ClarifaiResult+" "+data.get(i).name().toString();
            }
            return ClarifaiResult;
        }
        protected void onProgressUpdate() {

        }
        @Override
        protected void onPostExecute(String result) {
            Result.setText(result);

        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
