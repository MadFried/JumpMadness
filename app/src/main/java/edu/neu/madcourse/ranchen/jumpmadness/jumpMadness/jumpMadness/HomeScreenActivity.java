package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.neu.madcourse.ranchen.jumpmadness.R;


public class HomeScreenActivity extends Activity {
    private MediaPlayer backGroundMusicPlayer;
    private MediaPlayer gameIntroPlayer;
    TextView appName;
    Typeface custom_font;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_game_main_screen);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/mario_fonts.ttf");


/*        backGroundMusicPlayer = MediaPlayer.create(this, R.raw.background_music);
        backGroundMusicPlayer.setLooping(true);
        backGroundMusicPlayer.setVolume(0.4f, 0.4f);*/

        gameIntroPlayer = MediaPlayer.create(this, R.raw.taosmb3_intro);
        gameIntroPlayer.setLooping(false);
        gameIntroPlayer.setVolume(1.0f, 1.0f);

        appName = (TextView) findViewById(R.id.app_name_label);
        appName.setTypeface(custom_font);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        backGroundMusicPlayer.start();
        gameIntroPlayer.start();
    }

    public void startGame(View view) {
        gameIntroPlayer.stop();
        finish();
        Intent intent = new Intent(this, GameLevelMenuActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

//        backGroundMusicPlayer.stop();
        gameIntroPlayer.stop();
    }
}
