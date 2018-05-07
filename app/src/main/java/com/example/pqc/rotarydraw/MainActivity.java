package com.example.pqc.rotarydraw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private  LuckRotary luckRotary;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        luckRotary = findViewById( R.id.id_luckyPan );
        imageView = findViewById( R.id.id_start_btn );
        imageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!luckRotary.isStart()){
                    luckRotary.luckyStart(2);
                    imageView.setImageResource( R.drawable.stop );
                }else {
                    if(!luckRotary.isShouldEnd()){
                        luckRotary.luckyEnd();
                        imageView.setImageResource( R.drawable.start );
                    }
                }
            }
        } );
    }
}
