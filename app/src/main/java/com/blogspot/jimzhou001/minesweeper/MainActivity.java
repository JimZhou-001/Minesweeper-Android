package com.blogspot.jimzhou001.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private Button difficulty;
    private Button soundSettings;
    private Button rules;
    private Button exit;
    private String currentDifficulty;
    private boolean playingSound;
    private boolean playingBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("currentDifficulty", currentDifficulty);
                intent.putExtra("playingSound", playingSound);
                intent.putExtra("playingBackground", playingBackground);
                startActivity(intent);
            }
        });
        currentDifficulty = getSharedPreferences("difficulty", MODE_PRIVATE).getString("currentDifficulty", "simple");
        difficulty = (Button)findViewById(R.id.difficulty);
        difficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setTitle("难度选择").setIcon(R.drawable.ic_dialog).setSingleChoiceItems(
                        new String[] { "简单 9×9", "中等 16×16", "专家 30×16"},
                        getSharedPreferences("difficulty", MODE_PRIVATE).getInt("option", 0),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        currentDifficulty = "simple";
                                        break;
                                    case 1:
                                        currentDifficulty = "medium";
                                        break;
                                    case 2:
                                        currentDifficulty = "professional";
                                        break;
                                    default:
                                }
                                SharedPreferences.Editor editor = getSharedPreferences("difficulty", MODE_PRIVATE).edit();
                                editor.putInt("option", i);
                                editor.putString("currentDifficulty", currentDifficulty);
                                editor.commit();
                            }
                        }).setPositiveButton("确定", null).show();
            }
        });
        playingSound = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("playingSound", true);
        playingBackground = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("playingBackground", true);
        soundSettings = (Button)findViewById(R.id.soundSettings);
        soundSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setTitle("声音设置").setIcon(R.drawable.settings).setMultiChoiceItems(
                        new String[] { "游戏音效", "背景音乐"},
                        new boolean[] {getSharedPreferences("settings", MODE_PRIVATE).getBoolean("playingSound", true),
                                getSharedPreferences("settings", MODE_PRIVATE).getBoolean("playingBackground", true)},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                switch (i) {
                                    case 0:
                                        playingSound = b;
                                        break;
                                    case 1:
                                        playingBackground = b;
                                        break;
                                    default:
                                }
                                SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                                editor.putBoolean("playingSound", playingSound);
                                editor.putBoolean("playingBackground", playingBackground);
                                editor.commit();
                            }
                        }).setPositiveButton("确定", null).show();;
            }
        });
        rules = (Button)findViewById(R.id.rules);
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setTitle("规则说明").setIcon(R.drawable.rules).setMessage(
                        "1.您的第一次点击始终是安全的。\n" +
                                "2.方块里的数字n表示这个方块周围有且仅有n颗地雷。“周围”是指以该方块为中心的3×3范围内的方块，但不包括该方块自身。\n" +
                                "3.如果您知道某个方块里肯定有一颗地雷，可以通过长按在该方块上放一面旗子以作标记。\n" +
                                "4.在标记为地雷的方块上长按，可使该标记变为疑问标记（即不确定是否为地雷）。\n在标记为疑问的方块上长按，可使该标记消除。\n" +
                                "5.如果您认为某个方块是安全的（没有地雷），可以点击以翻开该方块。\n" +
                                "6.翻开所有安全方块即获胜，触发地雷即失败。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        exit = (Button)findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
