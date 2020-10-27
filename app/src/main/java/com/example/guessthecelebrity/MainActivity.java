package com.example.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    Button b0, b1, b2, b3;

    ArrayList<Button> buttonList;
    ImageView imgview;
    private ArrayList<String> imgUrls = new ArrayList<String>();
    private ArrayList<String> celebNames = new ArrayList<String>();
    private int index = 0, correctLocation = -1,score = 0;

    public class downloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap;

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                Log.i("conn:", "connection established ");
                bitmap = BitmapFactory.decodeStream(in);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public void changeImage(View view) {
        if (view.getTag().toString().equals(Integer.toString(correctLocation))) {
            Toast.makeText(this, "your answer is correct", Toast.LENGTH_LONG).show();
            score++;
        } else
            Toast.makeText(this, "wrong answer : Correct answer is - " + celebNames.get(index - 1), Toast.LENGTH_LONG).show();

        modify();
    }

    public void modify() {
        int arraySize = imgUrls.size();
        try {
            int actualId = index++;
            if (actualId == arraySize) {
                Toast.makeText(this, "Game Over!! Your score is " + score + "/" + arraySize, Toast.LENGTH_LONG).show();
                return;
            }
            String actualUrl = imgUrls.get(actualId);
            String imageName = celebNames.get(actualId);

            int buttonIndex = (int) (Math.random() * 4);
            correctLocation = buttonIndex;

            int i = 0;
            for (Button buttons : buttonList) {
                if (i == buttonIndex) {
                    buttons.setText(imageName);
                    break;
                }
                i++;
            }
            i = 0;
            for (Button buttons : buttonList) {
                if (i != buttonIndex) {
                    int position = ((buttonIndex + 1) * (i + 10)) % arraySize;
                    buttons.setText(celebNames.get(position));
                }
                i++;
            }
            downloadImage url = new downloadImage();
            Bitmap bitmap = url.execute(actualUrl).get();
            imgview.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class downloadUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            try {

                Log.i("TASK:", "doInBackground:....... ");
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Log.i("conn:", "connection established ");
                InputStreamReader reader = new InputStreamReader(in);

                int read = reader.read();

                while (read != -1) {
                    char ch = (char) read;
                    result += ch;
                    read = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b0 = findViewById(R.id.button0);
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);

        buttonList = new ArrayList<Button>();

        buttonList.add(b0);
        buttonList.add(b1);
        buttonList.add(b2);
        buttonList.add(b3);


        imgview = (ImageView) findViewById(R.id.imageView);
        downloadUrl task = new downloadUrl();

        try {
            String result = task.execute("http://www.posh24.se/kandisar").get();
            Pattern pat = Pattern.compile("<img src=\"(.*?)\"");

            String[] res = result.split("<div class=\"col-xs-12 col-sm-6 col-md-4\">");


            Matcher matcher = pat.matcher(res[0]);


            while (matcher.find()) {
                String ImageString = matcher.group(1);
                imgUrls.add(ImageString);
            }

            Pattern namePat = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\"/>");

            Matcher nameMatcher = namePat.matcher(res[0]);

            while (nameMatcher.find()) {
                String ImageString = nameMatcher.group(2);
                celebNames.add(ImageString);
            }
            modify();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
