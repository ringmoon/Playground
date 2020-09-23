package andy.playground;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SnakeGame extends AppCompatActivity {
    private LinearLayout linearLayout;
    private SnakeGameView snakeGameView;
    private TextView tvScore;
    private Button btUp, btDown, btLeft, btRight, btGameStart, btGamePause;
    private int activityWidth, activityHeight;
    private int frequency;
    private float density;
    private Timer timer;
    private Handler scoreHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_game);
        init();
        snakeGameView.startGameThread();
        //計算分數
        scoreHandler=new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tvScore.setText(pad(snakeGameView.getScore()));
                //判斷撞牆並輸出訊息
                if(snakeGameView.getOutBound()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SnakeGame.this);
                    String message=getString(R.string.out_of_bound);
                    builder.setMessage(message+"\n"+"Your Score is "+tvScore.getText());
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alert=builder.create();
                    alert.show();
                    snakeGameView.revertOutBound();
                }
                //判斷自撞並輸出訊息
                if(snakeGameView.getHitSelf()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SnakeGame.this);
                    String message=getString(R.string.hit_self);
                    builder.setMessage(message+"\n"+"Your Score is "+tvScore.getText());
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alert=builder.create();
                    alert.show();
                    snakeGameView.revertHitSelf();
                }
            }
        };
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scoreHandler.sendMessage(new Message());
            }
        },0,frequency);
    }

    //初始化UI元件
    private void init() {
        //取得各元件
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        snakeGameView = (SnakeGameView) findViewById(R.id.snakeGameView);
        tvScore = (TextView) findViewById(R.id.tvScore);
        btUp = (Button) findViewById(R.id.btUp);
        btDown = (Button) findViewById(R.id.btDown);
        btRight = (Button) findViewById(R.id.btRight);
        btLeft = (Button) findViewById(R.id.btLeft);
        btGameStart = (Button) findViewById(R.id.btGameStart);
        btGamePause = (Button) findViewById(R.id.btGamePause);

        //取得難度並設定
        String level = getIntent().getExtras().getString("level");
        if (level.equals("Simple")) {
            frequency=500;
        } else if (level.equals("Normal")) {
            frequency=350;
        } else if (level.equals("Difficult")) {
            frequency=200;
        }
        snakeGameView.setFrequency(frequency);

        //取得畫面大小到metrics,並取得density
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
        //動態取得activity
        linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                activityHeight = linearLayout.getHeight();
                activityWidth = linearLayout.getWidth();
                setViewsWidthAndHeight();
            }
        });
    }

    //設定各View寬高
    private void setViewsWidthAndHeight() {
        ViewGroup.LayoutParams params = tvScore.getLayoutParams();
        params.height = activityHeight / 15;
        params.width = activityWidth / 3;
        tvScore.setLayoutParams(params);

        params = btGameStart.getLayoutParams();
        params.height = activityHeight / 15;
        btGameStart.setLayoutParams(params);
        btGamePause.setLayoutParams(params);

        params = btUp.getLayoutParams();
        params.height = 2*activityHeight / 15;
        params.width = activityWidth / 3 - convertDpToPixel(6);
        btUp.setLayoutParams(params);
        btDown.setLayoutParams(params);
        btLeft.setLayoutParams(params);
        btRight.setLayoutParams(params);

        params = snakeGameView.getLayoutParams();
        params.height = 10 * activityHeight / 15 - convertDpToPixel(17);
        params.width = activityWidth;
        snakeGameView.setLayoutParams(params);
        snakeGameView.setDimension(params.width, params.height);  //768,1184   768,690
    }

    //dp轉px
    private int convertDpToPixel(int dp) {
        return (int) (dp * density);
    }

    public void gameStart(View view) {
        snakeGameView.gameStart();
    }
    public void gamePause(View view){
        snakeGameView.gamePause();
    }
    public void goRight(View view) {
        snakeGameView.addDirection(Direction.RIGHT);
    }
    public void goLeft(View view) {
        snakeGameView.addDirection(Direction.LEFT);
    }
    public void goUp(View view) {
        snakeGameView.addDirection(Direction.UP);
    }
    public void goDown(View view) {
        snakeGameView.addDirection(Direction.DOWN);
    }

    //格式輸出
    public String pad(int score){
        if(score<10){
            return "0"+score;
        }else{
            return String.valueOf(score);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        snakeGameView.onDestroy();
    }

}
