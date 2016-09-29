package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.ranchen.jumpmadness.R;

public class JumpHomeScreen extends Activity {

    ImageView homeMenuIcon;
    TextView appName;
    Button startGame;
    Button ackButton;
    private AlertDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_home_screen);
        setUpViews();
    }

    private void setUpViews() {
        homeMenuIcon = (ImageView) findViewById(R.id.HomeMenuIcon);
        appName = (TextView) findViewById(R.id.new_app_label);
        startGame = (Button) findViewById(R.id.start_game_btn);
        ackButton = (Button) findViewById(R.id.ack_button);
    }

    public void startGame(View view) {
        if (isFirstTime()) {
            Intent intent = new Intent(this, WalkthroughActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    public void showAck(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.jump_madness_ack);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing
                    }
                });
        mDialog = builder.show();
    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }
}
