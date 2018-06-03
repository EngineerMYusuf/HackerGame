package myusuf.hackergame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {
    boolean hacker;
    boolean won;
    FrameLayout frame;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        hacker = getIntent().getBooleanExtra("HACKER",true);
        won = getIntent().getBooleanExtra("WON",true);
        frame = findViewById(R.id.endGameFrame);
        txt = findViewById(R.id.endGameTxt);
        if(hacker){
            frame.setBackgroundResource(R.drawable.hackerbackground);
        }
        else{
            frame.setBackgroundResource(R.drawable.policebackground);
        }
        if(won){
            txt.setText("You Have Won!");
        }
        else{
            txt.setText("You Have Lost...");
        }
    }
}
