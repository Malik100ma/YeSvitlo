package com.example.yesvitlo;

import android.os.Bundle;
import android.widget.Button;
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

    TextView tvResult, tvStatus, tvDate;

    String channelURl = "https://t.me/s/cek_info";

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
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);

        btnCheck.setOnClickListener(v -> checkTelegram());
    }

    private void checkTelegram() {
        tvStatus.setText(R.string.download);
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(channelURl).userAgent("Mozila/5.0").timeout(15000).get();
                Elements posts = doc.select(".tgme_widget_message");

                String result = String.valueOf(R.string.no_publications_found);
                String dateTime = String.valueOf(R.string.date);
                if (!posts.isEmpty()) {
                    Element lastPost = posts.last();
                    Element timeElement = lastPost.selectFirst(".tgme_widget_message_date time");
                    Element textElement = lastPost.selectFirst(".tgme_widget_message_text");

                    if (textElement != null) {
                        result = textElement.text();
                    } else {
                        tvStatus.setText(String.valueOf(R.string.post));
                    }
                    if (timeElement != null) {
                        dateTime = timeElement.attr("dateTime");
                        SimpleDateFormat inputFormat= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.UK);
                        Date date = inputFormat.parse(dateTime);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                        dateTime = outputFormat.format(date);
                    } else {
                        dateTime = "Не вдалось отпримати дату";
                    }
                }
                String finalResult = result;
                String finalDateTime = dateTime;
                runOnUiThread(() -> {
                    tvStatus.setText(R.string.ready);
                    tvResult.setText(finalResult);
                    tvDate.setText(finalDateTime);
                });
            } catch (Exception e) {
                runOnUiThread(() -> tvStatus.setText(R.string.updload_error));

            }
        }).start();

    }
}