package myusuf.hackergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class InfectNodeActivity extends AppCompatActivity {
    int sessionID;
    byte node;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infect_node);
        node = (byte) 0x00;
        sessionID = getIntent().getIntExtra("SESSION_ID", 0);

        ImageButton node1 = findViewById(R.id.node1);
        node1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 1");
                node = (byte) 0x01;
                goToQuestions();
            }
        });
        ImageButton node2 = findViewById(R.id.node2);
        node2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 2");
                node = (byte) 0x02;
                goToQuestions();
            }
        });
        ImageButton node3 = findViewById(R.id.node3);
        node3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 3");
                node = (byte) 0x03;
                goToQuestions();
            }
        });
        ImageButton node4 = findViewById(R.id.node4);
        node4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 4");
                node = (byte) 0x04;
                goToQuestions();
            }
        });
        ImageButton node5 = findViewById(R.id.node5);
        node5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 5");
                node = (byte) 0x05;
                goToQuestions();
            }
        });
        ImageButton node6 = findViewById(R.id.node6);
        node6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 6");
                node = (byte) 0x06;
                goToQuestions();
            }

        });
        ImageButton node7 = findViewById(R.id.node7);
        node7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 7");
                node = (byte) 0x07;
                goToQuestions();
            }
        });
        ImageButton node8 = findViewById(R.id.node8);
        node8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 8");
                node = (byte) 0x08;
                goToQuestions();
            }
        });
        ImageButton node9 = findViewById(R.id.node9);
        node9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 9");
                node = (byte) 0x09;
                goToQuestions();
            }
        });
        ImageButton node10 = findViewById(R.id.node10);
        node10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Infect", "Infecting Node 10");
                node = (byte) 0x0A;
                goToQuestions();
            }
        });
    }
    public void goToQuestions(){
        Intent intent = new Intent(getApplicationContext(), QuestionsActivity.class);
        intent.putExtra("HACKER",true);
        intent.putExtra("SESSION_ID",sessionID);
        intent.putExtra("NODE",node);
        startActivity(intent);
    }

}
