package myusuf.hackergame;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class MainPage extends AppCompatActivity {
    final String TAG = "Main Page";
    FrameLayout f;
    boolean hacker;
    int id;
    EditText sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        TextView sessionIDText = findViewById(R.id.sessionIDText);
        sessionID = findViewById(R.id.sessionID);

        f = findViewById(R.id.mainFrame);

        Switch role = findViewById(R.id.role);
        role.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    userIsHacker();
                } else {
                    userIsPolice();
                }
            }
        });

        ImageButton startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Starting Game");

                if(hacker){
                    id = Integer.parseInt(sessionID.getText().toString());
                    Class c = InfectNodeActivity.class;
                    launchActivity(id,c);
                }
                else{
                    id = Integer.parseInt(sessionID.getText().toString());
                    Class c = ScannerActivity.class;
                    launchActivity(id,c);
                }
            }
        });


    }
    public void userIsHacker(){
        hacker = true;
        f.setBackgroundResource(R.drawable.hackerbackground);
    }
    public void userIsPolice(){
        hacker = false;
        f.setBackgroundResource(R.drawable.policebackground);
    }
    public void launchActivity(int id, Class c){
        Intent intent = new Intent(this, c);
        if(hacker){
            intent.putExtra("HACKER",true);
        }
        else{
            intent.putExtra("HACKER",false);
        }
        intent.putExtra("SESSION_ID",id);
        startActivity(intent);
    }
}
