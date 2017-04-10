package mobileis.helloworld;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    TextView textView;
    ImageView imageView;

    final String imageTypes[] = {
            "jpg",
            "jpeg",
            "png",
            "bmp",
            "gif"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String urlString = editText.getText().toString();

                new AsyncTask<String, Void, String>(){

                    String extension = null;
                    Bitmap bitmap = null;

                    @Override
                    protected String doInBackground(String... urlStrings) {
                        String urlString = urlStrings[0];

                        int index = urlString.lastIndexOf('.');

                        if(index >= 0) {
                            extension = urlString.substring(index + 1);
                        }

                        Boolean isImage = false;

                        if(extension != null){
                            for(int n = 0; n < imageTypes.length; ++n){
                                if(imageTypes[n].equals(extension)){
                                    isImage = true;
                                    break;
                                }
                            }
                        }

                        try {
                            URL url = new URL(urlString);

                            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                            if(isImage){
                                bitmap = BitmapFactory.decodeStream(connection.getInputStream());

                                return null;
                            }else {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                                String
                                    response = "",
                                    line;

                                int b;

                                while ((line = reader.readLine()) != null) {
                                    response += line;
                                }

                                return response;
                            }
                        }catch(Exception e){
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);

                        if (response == null){
                            if(bitmap == null) {
                                imageView.setVisibility(View.GONE);
                                textView.setText("Error: Something bad happened.");
                                textView.setVisibility(View.VISIBLE);
                            }else{
                                textView.setVisibility(View.GONE);
                                imageView.setImageBitmap(bitmap);
                                imageView.setVisibility(View.VISIBLE);
                            }
                        }else{
                            imageView.setVisibility(View.GONE);
                            textView.setText(response);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }.execute(urlString);
            }
        });
    }
}
