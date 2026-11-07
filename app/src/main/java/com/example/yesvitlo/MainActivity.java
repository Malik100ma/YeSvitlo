package com.example.yesvitlo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextClock;
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


public class MainActivity extends AppCompatActivity {

    Button btnCheck;

    TextView tvResult,tvStatus;

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

        btnCheck.setOnClickListener(v -> checkTelegram());
    }

    private void checkTelegram() {
        tvStatus.setText(R.string.download);
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(channelURl).userAgent("Mozila/5.0").timeout(15000).get();
                Elements posts = doc.select(".tgme_widget_message");
                String result = String.valueOf(R.string.no_publications_found);
                if (!posts.isEmpty()) {
                    Element lastPost = posts.last();
                    Element textElement = lastPost.selectFirst(".tgme_widget_message_text");
                    if (textElement != null) {
                        result = textElement.text();
                    } else {
                        tvStatus.setText(String.valueOf(R.string.post));
                    }

                }
                String finalResult = result;
                runOnUiThread(() -> {
                    tvStatus.setText(R.string.ready);
                    tvResult.setText(finalResult);

                });
            } catch (Exception e) {
                runOnUiThread(() -> tvStatus.setText(R.string.updload_error));
            }
        }).start();

    }
}