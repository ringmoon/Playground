package andy.playground;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andy on 2017/11/9.
 */

public class Memory_card_game extends AppCompatActivity {
    private String IP;
    private int mSecond;
    private int correctCount;
    private boolean isTiming;
    private boolean isCountdown;
    private Timer timer;
    private Handler timeHandler;
    private List<Card> cardList;
    private List<View> cardImageList;
    private TextView tvSecond, tvMinute;
    private Button btGameStart, btGameReset;
    private GridView gvCard;
    private MediaPlayer mp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_card_game);
        variableInit();
        gameInitByIP();
    }

    //初始化變數並依照倒數及開始計時配置記憶牌正反面
    private void variableInit() {
        timer = new Timer();
        mSecond = 0;
        correctCount=0;
        isTiming = false;
        isCountdown = true;
        cardList = new ArrayList<>();
        cardImageList = new ArrayList<>();
        IP = getIntent().getExtras().getString("IP");
        tvMinute = (TextView) findViewById(R.id.tvMinute);
        tvSecond = (TextView) findViewById(R.id.tvSecond);
        btGameStart = (Button) findViewById(R.id.btGameStart);
        btGameReset = (Button) findViewById(R.id.btGameReset);
        gvCard = (GridView) findViewById(R.id.gvCard);
        if(IP.equals("YO-KAI Watch")){
            mp=MediaPlayer.create(this,R.raw.yokai);
        }else if(IP.equals("Pokemon")){
            mp=MediaPlayer.create(this,R.raw.pokemon);
        }else if(IP.equals("One Piece")){
            mp=MediaPlayer.create(this,R.raw.one_piece);
        }
        mp.start();
        mp.setLooping(true);
        timeHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //case 1 倒數, case 2 計時
                switch (msg.what) {
                    case 1:
                        mSecond-=10;
                        displayTime(mSecond+1000);
                        if (mSecond ==0) {
                            for(int i=0;i<gvCard.getChildCount();i++){
                                Card card=(Card)gvCard.getItemAtPosition(i);
                                ((ImageView) gvCard.getChildAt(i).findViewById(R.id.ivImage)).setImageResource(card.getBackImage());
                            }
                            setCardListener();
                            isCountdown = false;
                            tvSecond.setTextColor(Color.parseColor("#FFFFFF"));
                            displayTime(mSecond);
                        }
                        break;
                    case 2:
                        mSecond+=10;
                        displayTime(mSecond);
                        break;
                }
            }
        };
        timer.schedule(new CountTime(), 0, 10);
    }
    //計算時間的timer
    private class CountTime extends TimerTask {
        @Override
        public void run() {
            if (isTiming) {
                if (isCountdown) {
                    Message msg = new Message();
                    msg.what = 1;
                    timeHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 2;
                    timeHandler.sendMessage(msg);
                }
            }

        }
    }
    //格式化輸出時間
    private void displayTime(int ms) {
       int s=ms/1000;
        String second, minute;
        if (s % 60 == 0) {
            second = "00";
        } else if (s % 60 < 10) {
            second = "0" + String.valueOf(s % 60);
        } else {
            second = String.valueOf(s % 60);
        }
        if (s / 60 == 0) {
            minute = "00";
        } else if (s / 60 < 10) {
            minute = "0" + String.valueOf(s / 60);
        } else {
            minute = String.valueOf(s / 60);
        }
        tvMinute.setText(minute);
        tvSecond.setText(second);
    }
    //設定記憶牌的點擊事件
    private void setCardListener(){
        gvCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cardList.size() == 2) {
                    if (cardList.get(0).getImage() == cardList.get(1).getImage()) {
                        cardImageList.get(0).setVisibility(View.INVISIBLE);
                        cardImageList.get(1).setVisibility(View.INVISIBLE);
                        correctCount++;
                    } else {
                        ((ImageView) cardImageList.get(0).findViewById(R.id.ivImage)).setImageResource(cardList.get(0).getBackImage());
                        ((ImageView) cardImageList.get(1).findViewById(R.id.ivImage)).setImageResource(cardList.get(1).getBackImage());
                    }
                    cardImageList.clear();
                    cardList.clear();
                }
                Card card = (Card) parent.getItemAtPosition(position);
                if(cardImageList.size()==1&&view.equals(cardImageList.get(0))) return;  //避免重複點取造成的bug
                cardList.add(card);
                cardImageList.add(view);
                ((ImageView) view.findViewById(R.id.ivImage)).setImageResource(card.getImage());
                if(correctCount==(gvCard.getChildCount()/2-1)&&cardList.size() == 2){
                    correctCount++;
                    isTiming = false;
                }
            }
        });
    }
    //根據IP(肖像)設置GardView的初始化
    private void gameInitByIP() {
        gvCard.setAdapter(new CardAdapter(this, IP));
        btGameReset.setEnabled(false);
    }
    //gameStart按鈕事件
    public void onGameStart(View view) {
        mSecond = 5000;
        tvSecond.setTextColor(Color.parseColor("#FF0000"));
        displayTime(mSecond);
        isTiming = true;
        isCountdown=true;
        btGameStart.setEnabled(false);
        btGameReset.setEnabled(true);
        for(int i=0;i<gvCard.getChildCount();i++){
            Card card=(Card)gvCard.getItemAtPosition(i);
            ((ImageView) gvCard.getChildAt(i).findViewById(R.id.ivImage)).setImageResource(card.getImage());
        }
    }
    //gameReset按鈕事件
    public void onGameReset(View view) {
        mSecond = 0;
        correctCount=0;
        tvSecond.setTextColor(Color.parseColor("#FFFFFF"));
        displayTime(mSecond);
        isTiming = false;
        btGameStart.setEnabled(true);
        gameInitByIP();
        cardImageList.clear();
        cardList.clear();
        gvCard.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
    //Activity結束方法
    public void onDestroy(){
        super.onDestroy();
        mp.stop();
        timer.cancel();
    }
}
