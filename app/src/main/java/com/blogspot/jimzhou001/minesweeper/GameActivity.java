package com.blogspot.jimzhou001.minesweeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundHashMap;
    private boolean playingSound;
    private MediaPlayer backgroundPlayer;
    private boolean playingBackground;
    private ImageView back;
    private TextView shortestTime;
    private TextView currentTime;
    private int time;
    private ImageView restart;
    private GridLayout gameLayout;
    private Block[][] blocks;
    private boolean hasSetMines;
    private TextView numberOfMines;
    private int number;
    private int rowCount;
    private int columnCount;
    private int numberOfBlocksOpened;
    private int currentShortestTime;
    private boolean isRunning;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ++time;
                    if (isRunning) {
                        currentTime.setText(time + "");
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shortestTime = (TextView)findViewById(R.id.shortestTime);
        currentTime = (TextView)findViewById(R.id.time);

        numberOfMines = (TextView)findViewById(R.id.numberOfMines);

        restart = (ImageView)findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playingBackground) {
                    backgroundPlayer.stop();
                    backgroundPlayer.release();
                }
                initGame();
            }
        });

        gameLayout = (GridLayout)findViewById(R.id.gameLayout);

        timer.schedule(task, 1000, 1000);

        initGame();

    }

    private void initGame() {
        time = 0;
        currentTime.setText("0");
        isRunning = true;
        initSoundPool();
        playingSound = getIntent().getBooleanExtra("playingSound", false);
        playingBackground = getIntent().getBooleanExtra("playingBackground", false);
        if (playingBackground) {
            backgroundPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
            backgroundPlayer.setLooping(true);
            backgroundPlayer.start();
        }
        currentShortestTime = getSharedPreferences(getIntent().getStringExtra("currentDifficulty"), MODE_PRIVATE).getInt("shortestTime", Integer.MAX_VALUE);
        if (currentShortestTime < Integer.MAX_VALUE) {
            shortestTime.setText(currentShortestTime + "");
        }
        switch (getIntent().getStringExtra("currentDifficulty")) {
            case "simple":
                number = 10;
                rowCount = 9;
                columnCount = 9;
                break;
            case "medium":
                number = 40;
                rowCount = 16;
                columnCount = 16;
                break;
            case "professional":
                number = 99;
                rowCount = 16;
                columnCount = 30;
        }
        numberOfMines.setText(number + "");
        gameLayout.setRowCount(rowCount);
        gameLayout.setColumnCount(columnCount);
        gameLayout.removeAllViews();
        hasSetMines = false;//尚未设定地雷位置
        numberOfBlocksOpened = 0;
        blocks = new Block[gameLayout.getRowCount()][gameLayout.getColumnCount()];
        for(int i=0;i<blocks.length;++i) {
            for(int j=0;j<blocks[0].length;++j) {
                final int currentRow = i;
                final int currentColumn = j;
                blocks[i][j] = new Block(this);
                blocks[i][j].isClickable = true;//尚未翻开，可以点击
                blocks[i][j].hasMine = false;//此处没有地雷
                blocks[i][j].isFlaggedMine = false;//没有标记为地雷
                blocks[i][j].isFlaggedPuzzled = false;//没有疑问
                blocks[i][j].numberOfMinesInSurrounding = 0;//周围地雷数目为0
                int width = getResources().getDisplayMetrics().widthPixels/5;
                blocks[i][j].setLayoutParams(new ViewGroup.LayoutParams(width, width));
                blocks[i][j].show();
                gameLayout.addView(blocks[i][j]);
                blocks[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Block block = ((Block)view);
                        if (block.isClickable) {
                            if (block.hasMine) {
                                if (playingSound) {
                                    soundPlay(1, 0);
                                }
                                showAllBlocks();
                                new AlertDialog.Builder(GameActivity.this).setTitle("游戏结束").setMessage("很遗憾，您遇到地雷啦").setPositiveButton("点击此处查看详情", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                            } else {
                                if (playingSound) {
                                    soundPlay(2, 0);
                                }
                                block.isClickable = false;
                                ++numberOfBlocksOpened;
                                checkGameWin();
                                block.show();
                                if (!hasSetMines) {
                                    setMines(block);
                                    hasSetMines = true;
                                }
                                if (block.numberOfMinesInSurrounding==0) {
                                    openSurrounddingBlankBlock(currentRow, currentColumn);
                                }
                            }
                        }
                    }
                });
                blocks[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (playingSound) {
                            soundPlay(3, 0);
                        }
                        Block block = ((Block)view);
                        if (block.isClickable) {
                            block.isClickable = false;
                            block.isFlaggedMine = true;
                        } else if (block.isFlaggedMine) {
                            block.isFlaggedMine = false;
                            block.isFlaggedPuzzled = true;
                        } else if (block.isFlaggedPuzzled) {
                            block.isFlaggedPuzzled = false;
                            block.isClickable = true;
                        }
                        block.show();
                        return true;
                    }
                });
            }
        }
    }

    private void setMines(Block firstClickedBlock) {

        Random random = new Random();
        int y, x;

        OUT:
        for (int i = 0; i < number; ++i) {
            y = random.nextInt(blocks.length);
            x = random.nextInt(blocks[0].length);
            for(int j=y-1;j<=y+1;++j) {
                for(int k=x-1;k<=x+1;++k) {
                    if (j>=0&&j<blocks.length&&k>=0&&k<blocks[0].length&&firstClickedBlock==blocks[j][k]) {
                        --i;
                        continue OUT;
                    }
                }
            }
            if (!blocks[y][x].hasMine) {
                blocks[y][x].hasMine = true;
            } else {
                --i;
            }
        }

        for(int i=0;i<blocks.length;++i) {
            for (int j = 0; j < blocks[0].length; ++j) {
                if (!blocks[i][j].hasMine) {
                    for(int k=i-1;k<=i+1;++k) {
                        for(int l=j-1;l<=j+1;++l) {
                            if (k>=0&&k<blocks.length&&l>=0&&l<blocks[0].length&&blocks[k][l].hasMine) {
                                ++(blocks[i][j].numberOfMinesInSurrounding);
                            }
                        }
                    }
                }
            }
        }

    }

    private void openSurrounddingBlankBlock(int row, int column) {
        for(int i=row-1;i<=row+1;++i) {
            for(int j=column-1;j<=column+1;++j) {
                if (i>=0&&i<blocks.length&&j>=0&&j<blocks[0].length&&blocks[i][j].isClickable) {
                    blocks[i][j].isClickable = false;
                    ++numberOfBlocksOpened;
                    checkGameWin();
                    blocks[i][j].show();
                    if (blocks[i][j].numberOfMinesInSurrounding==0) {
                        openSurrounddingBlankBlock(i, j);
                    }
                }
            }
        }
    }

    private void checkGameWin() {
        if (numberOfBlocksOpened==blocks.length*blocks[0].length-number) {
            showAllBlocks();
            if (time<currentShortestTime) {
                shortestTime.setText(time + "");
                SharedPreferences.Editor editor = getSharedPreferences(getIntent().getStringExtra("currentDifficulty"), MODE_PRIVATE).edit();
                editor.putInt("shortestTime", time);
                editor.commit();
            }
            new AlertDialog.Builder(GameActivity.this).setTitle("游戏胜利").setMessage("恭喜您成功躲开所有地雷！").setPositiveButton("点击此处查看详情", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }

    private void showAllBlocks() {
        isRunning = false;
        currentTime.setText(time + "");
        for(int i=0;i<blocks.length;++i) {
            for (int j = 0; j < blocks[0].length; ++j) {
                blocks[i][j].isClickable = false;
                blocks[i][j].isFlaggedMine = false;
                blocks[i][j].isFlaggedPuzzled = false;
                blocks[i][j].show();
            }
        }
    }

    private void initSoundPool() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundHashMap = new HashMap<Integer, Integer>();
        soundHashMap.put(1, soundPool.load(getApplicationContext(), R.raw.explode, 1));
        soundHashMap.put(2, soundPool.load(getApplicationContext(), R.raw.safe, 1));
        soundHashMap.put(3, soundPool.load(getApplicationContext(), R.raw.convert, 1));
    }

    private void soundPlay(int sound, int loop) {
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = currVolume / maxVolume;
        soundPool.play(soundHashMap.get(sound), volume, volume, 1, loop, 1.0f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playingBackground) {
            backgroundPlayer.stop();
            backgroundPlayer.release();
        }
    }

}