package com.example.yesvitlo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnCheck;
    TextView tvResult, tvDate;
    Spinner spinnerGroups;
    String channelURL = "https://t.me/s/cek_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerGroups = findViewById(R.id.spinnerGroups);
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);
        tvDate = findViewById(R.id.tvDate);


        String[] groups = {"Черга 1.1","Черга 1.2","Черга 2.1","Черга 2.2","Черга 3.1","Черга 3.2","Черга 4.1","Черга 4.2","Черга 5.1","Черга 5.2","Черга 6.1","Черга 6.2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroups.setAdapter(adapter);

        String selectedGroup = spinnerGroups.getSelectedItem().toString();

        btnCheck.setOnClickListener(v -> checkTelegram(selectedGroup));


    }

    private void checkTelegram(String group) {
        tvResult.setText(String.valueOf(R.string.download));
        new Thread(() -> {
            try{
                Document doc = Jsoup.connect(channelURL).userAgent("Mozilla/5.0").timeout(15000).get();
                Elements posts = doc.select(".tgme_widget_message");
                String result = String.valueOf(R.string.no_publications_found);
                String dateTime = String.valueOf(R.string.dateno);

                if (!posts.isEmpty()){
                    for (int i = posts.size()-1; i>=0; i--){
                        Element lastPost = posts.last();
                        Element textElement = lastPost.selectFirst(".tgme_widget_message_text");
                        if (textElement == null){
                            continue;
                        }
                        String postText = textElement.text();
                        if (postText.contains(group)){
                            Element timeElement = lastPost.selectFirst(".tgme_widget_message_date time");
                            if (textElement != null){
                                result = textElement.text();
                            } else {
                                result = String.valueOf(R.string.post);
                            }
                            if (timeElement != null){
                                dateTime = timeElement.attr("datetime");
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                                Date date = inputFormat.parse(dateTime);
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                                dateTime = outputFormat.format(date);
                            } else {
                                dateTime = String.valueOf(R.string.date);
                            }
                        }
                    }


                }
                String finalResult = result;
                String finalDate = dateTime;
                runOnUiThread(()-> tvResult.setText(finalResult));
                runOnUiThread(()-> tvDate.setText(finalDate));
            } catch (Exception e){
                runOnUiThread(() -> tvResult.setText(String.valueOf(R.string.updload_error)));
            }
        }).start();
    }
}