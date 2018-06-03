package myusuf.hackergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends AppCompatActivity {
    final String TAG = "Main Page";
    FrameLayout f;
    boolean hacker;
    int id;
    EditText sessionID;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        final TextView sessionIDText = findViewById(R.id.sessionIDText);
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
                Class c;
                if(sessionID.getText().toString().equals("myk")){
                    c = AdminActivity.class;
                    launchActivity(id, c);
                }
                else {
                    if (isInteger(sessionID.getText().toString())) {
                        if (hacker) {
                            id = Integer.parseInt(sessionID.getText().toString());
                            c = InfectNodeActivity.class;
                            launchActivity(id, c);
                        } else {
                            id = Integer.parseInt(sessionID.getText().toString());
                            c = ScannerActivity.class;
                            launchActivity(id, c);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter a number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public void userIsHacker() {
        hacker = true;
        f.setBackgroundResource(R.drawable.hackerbackground);
    }

    public void userIsPolice() {
        hacker = false;
        f.setBackgroundResource(R.drawable.policebackground);
    }

    public void launchActivity(int id, Class c) {
        Intent intent = new Intent(this, c);
        if (hacker) {
            intent.putExtra("HACKER", true);
        } else {
            intent.putExtra("HACKER", false);
        }
        intent.putExtra("SESSION_ID", id);
        startActivity(intent);
    }
}
