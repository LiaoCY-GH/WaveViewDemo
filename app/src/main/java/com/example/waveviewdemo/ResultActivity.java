package com.example.waveviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    private TextView TV;
    private WaveView WV;
    private EditText ET;
    private Button BT;
    private SeekBar SB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        init();

        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WV.setProgressWithAnim(Float.valueOf(ET.getText().toString().trim()));
            }
        });

        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                WV.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TV = findViewById(R.id.txt_tl);
        Intent intent = getIntent();
        String data = intent.getStringExtra("send");
        TV.setText(data);

    }

    private void init() {
        WV = findViewById(R.id.wv_wave);
        BT = findViewById(R.id.btn_wave);
        ET = findViewById(R.id.edt_wave);
        SB = findViewById(R.id.sb_wave);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.caidan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sy:
                Toast.makeText(this, "正在跳转首页，请稍后", Toast.LENGTH_SHORT).show();
                break;
            case R.id.xx:
                Toast.makeText(this, "正在跳转消息，请稍后", Toast.LENGTH_SHORT).show();
                break;
            case R.id.kf:
                Toast.makeText(this, "正在跳转客服，请稍后", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}