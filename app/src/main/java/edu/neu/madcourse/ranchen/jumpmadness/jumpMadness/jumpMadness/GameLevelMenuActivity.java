package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.ranchen.jumpmadness.R;


public class GameLevelMenuActivity extends Activity {

    public static final String PREFS_NAME = "MyPrefsFile";
    Button levelOneButton;
    Button levelTwoButton;
    Button levelThreeButton;
    Button levelFourButton;
    Button levelFiveButton;
    Button levelSixButton;
    Typeface custom_font;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_level_menu);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/mario_fonts.ttf");

        levelOneButton = (Button) findViewById(R.id.level_one_btn);
        levelTwoButton = (Button) findViewById(R.id.level_two_btn);
        levelThreeButton = (Button) findViewById(R.id.level_three_btn);
        levelFourButton = (Button) findViewById(R.id.level_four_btn);
        levelFiveButton = (Button) findViewById(R.id.level_five_btn);
        levelSixButton = (Button) findViewById(R.id.level_six_btn);

        levelOneButton.setTypeface(custom_font);
        levelTwoButton.setTypeface(custom_font);
        levelThreeButton.setTypeface(custom_font);
        levelFourButton.setTypeface(custom_font);
        levelFiveButton.setTypeface(custom_font);
        levelSixButton.setTypeface(custom_font);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("levelOnePass", false)) {
            levelTwoButton.setEnabled(true);
            levelTwoButton.setBackgroundColor(Color.parseColor("#00FF7F"));
        }

        if (getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("levelTwoPass", false)) {
            levelThreeButton.setEnabled(true);
            levelThreeButton.setBackgroundColor(Color.parseColor("#00FF7F"));
        }

        if (getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("levelThreePass", false)) {
            levelFourButton.setEnabled(true);
            levelFourButton.setBackgroundColor(Color.parseColor("#00FF7F"));
        }

        if (getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("levelFourPass", false)) {
            levelFiveButton.setEnabled(true);
            levelFiveButton.setBackgroundColor(Color.parseColor("#00FF7F"));
        }

        if (getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("levelFivePass", false)) {
            levelSixButton.setEnabled(true);
            levelSixButton.setBackgroundColor(Color.parseColor("#00FF7F"));
        }
    }

    public void startLevelOne(View view) {
        Intent intent = new Intent(this, LevelOneActivity.class);
        startActivity(intent);
    }

    public void startLevelTwo(View view) {
        Intent intent = new Intent(this, LevelTwoActivity.class);
        startActivity(intent);
    }

    public void startLevelThree(View view) {
        Intent intent = new Intent(this, LevelThreeActivity.class);
        startActivity(intent);
    }

    public void startLevelFour(View view) {
        Intent intent = new Intent(this, LevelFourActivity.class);
        startActivity(intent);
    }

    public void startLevelFive(View view) {
        Intent intent = new Intent(this, LevelFiveActivity.class);
        startActivity(intent);
    }

    public void startLevelSix(View view) {
        Intent intent = new Intent(this, LevelSixActivity.class);
        startActivity(intent);
    }
}
