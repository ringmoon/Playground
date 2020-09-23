package andy.playground;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Andy on 2017/10/29.
 */

public class SnakeGameView extends View {
    private boolean gameIsRunning = false;
    private boolean outBound, hitSelf;
    private int frequency;
    private int nodesCount = 0;
    private int score = 0;
    private int width, height;
    int minY, maxY, minX, maxX;
    private int nodeWidth, nodeHeight;
    private float boundWidth = 20;
    private float boundHeight = 20;
    private float margin = 3;
    private List<Point> snakeNodes;
    private List<Point> foods;
    private Queue<Direction> directions;
    private Direction direction;
    private Paint paint = new Paint();
    private Timer timer;
    private Handler gameHandler;
    private MediaPlayer bgmPlayer, soundPlayer;
    private Context context;

    public SnakeGameView(Context context) {
        super(context);
    }

    public SnakeGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        snakeNodes = new LinkedList<>();  //貪吃蛇身體
        foods = new LinkedList<>();   //食物
        directions = new LinkedList<>();  //方向Queue
        directions.add(Direction.RIGHT);
        bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
        soundPlayer = MediaPlayer.create(context, R.raw.eat);
        bgmPlayer.start();
        bgmPlayer.setLooping(true);
        //設定handler
        gameHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                invalidate();
            }
        };
    }

    //啟動遊戲執行程序
    public void startGameThread() {
        timer = new Timer();
        timer.schedule(new GameTask(), 0, frequency);
    }

    //設定寬高等屬性
    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
        this.nodeHeight = (int) (height - boundHeight * 2) / 15;
        this.nodeWidth = (int) (width - boundWidth * 2) / 15;
        this.boundWidth += ((width - boundWidth * 2) % 15) / 2.0;
        this.boundHeight += ((height - boundHeight * 2) % 15) / 2.0;
        minX = 0;
        maxX = 14 * nodeWidth;
        minY = 0;
        maxY = 14 * nodeHeight;
    }

    //開始遊戲初始配置
    public void gameStart() {
        if (!gameIsRunning) {
            gameIsRunning = true;
            if (nodesCount == 0) {
                snakeNodes.add(new Point(2 * nodeWidth, 2 * nodeHeight));
                snakeNodes.add(new Point(1 * nodeWidth, 2 * nodeHeight));
                snakeNodes.add(new Point(0, 2 * nodeHeight));
                nodesCount = 3;
                generateFoods(5);
            }
        }
    }

    //暫停遊戲
    public void gamePause() {
        gameIsRunning = false;
    }

    //讀取按鈕加入後續移動方向
    public void addDirection(Direction d) {
        if(!gameIsRunning) return ;
        switch (d) {
            case UP:
                if (direction == Direction.DOWN) {
                    return;   //不能180度轉彎
                }
                break;
            case DOWN:
                if (direction == Direction.UP) {
                    return;     //不能180度轉彎
                }
                break;
            case LEFT:
                if (direction == Direction.RIGHT) {
                    return;  //不能180度轉彎
                }
                break;
            case RIGHT:
                if (direction == Direction.LEFT) {
                    return;   //不能180度轉彎
                }
                break;
        }
        directions.add(d);
    }

    //設定頻率
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    //取得分數
    public int getScore() {
        return score;
    }

    //判斷是否出界
    public boolean isOutOfBound(Direction d) {
        boolean outOfBound = false;
        switch (d) {
            case UP:
                if (snakeNodes.get(0).y == minY) {
                    outOfBound = true;
                }
                break;
            case DOWN:
                if (snakeNodes.get(0).y == maxY) {
                    outOfBound = true;
                }
                break;
            case LEFT:
                if (snakeNodes.get(0).x == minX) {
                    outOfBound = true;
                }
                break;
            case RIGHT:
                if (snakeNodes.get(0).x == maxX) {
                    outOfBound = true;
                }
                break;
        }
        if (outOfBound) {
            outBound = true;
            reset();
            return true;
        }
        return false;
    }

    //遊戲運行程序,並在過程中判斷是否有自撞情況或超出邊界
    public void snakeTransfer(Direction d) {
        int transferX = 0, transferY = 0;
        //判斷是否將超出邊邊界,是就離開此方法
        if (isOutOfBound(direction)) {
            return;
        }
        switch (d) {
            case UP:
                transferY -= nodeHeight;
                break;
            case DOWN:
                transferY += nodeHeight;
                break;
            case LEFT:
                transferX -= nodeWidth;
                break;
            case RIGHT:
                transferX += nodeWidth;
                break;
        }
        //以下判斷是否自撞
        for (int i = 0; i < snakeNodes.size() - 1; i++) {
            if (snakeNodes.get(i).x == snakeNodes.get(0).x + transferX && snakeNodes.get(i).y == snakeNodes.get(0).y + transferY) {
                hitSelf = true;
                reset();
                return;
            }
        }
        //如果沒有自撞情況就執行正常移動,並判斷是否有吃到食物
        snakeNodes.add(0, new Point(snakeNodes.get(0).x + transferX,
                snakeNodes.get(0).y + transferY));
        checkGetFood();
        return;
    }

    //檢查是否吃到食物
    public boolean checkGetFood() {
        for (int i = 0; i < foods.size(); i++) {
            if (snakeNodes.get(0).x == foods.get(i).x && snakeNodes.get(0).y == foods.get(i).y) {
                foods.remove(i);
                generateFoods(1);
                nodesCount++;
                score++;
                soundPlayer.start();
                return true;
            }
        }
        snakeNodes.remove(nodesCount);
        return false;
    }

    //reset
    public void reset() {
        gameIsRunning = false;
        snakeNodes.clear();
        foods.clear();
        directions.clear();
        directions.add(Direction.RIGHT);
        direction = null;
        setCurrentDirection();
        nodesCount = 0;
        MediaPlayer.create(context, R.raw.fail).start();
    }

    //設定現在前進方向
    public void setCurrentDirection() {
        //沒有設定方向的情況下,直接取用方向
        if (direction == null) {
            direction = directions.poll();
        }
        //將和當前方向相同的方向刪除
        while (direction == directions.peek()) {
            directions.poll();
        }
        //確定下個方向不是null就取用
        if (directions.peek() != null) {
            direction = directions.poll();
        }
    }

    //生成食物
    public void generateFoods(int count) {
        Random random = new Random();
        boolean isRepeatedFood;
        if (nodesCount < 200) {
            for (int c = 0; c < count; c++) {
                isRepeatedFood = false;
                Point point = new Point(random.nextInt(15) * nodeWidth, random.nextInt(15) * nodeHeight);
                //判斷是否和先前食物重複,重複就重新生成
                for (int f = 0; f < foods.size(); f++) {
                    if (foods.get(f).x == point.x && foods.get(f).y == point.y) {
                        c -= 1;
                        isRepeatedFood = true;
                        break;
                    }
                }
                if (isRepeatedFood) continue;
                //避免食物產生跟蛇節點重複
                for (int i = 0; i < snakeNodes.size(); i++) {
                    if (snakeNodes.get(i).x == point.x && snakeNodes.get(i).y == point.y) {
                        c -= 1;
                        point = null;
                        break;
                    }
                }
                if (point != null) {
                    foods.add(point);
                }
            }

        } else {
            if (foods.size() == 0) {
                for (int c = 0; c < count; c++) {
                    Point point = new Point(random.nextInt(15) * nodeWidth, random.nextInt(15) * nodeHeight);
                    //避免食物產生跟蛇節點重複
                    for (int i = 0; i < snakeNodes.size(); i++) {
                        if (snakeNodes.get(i).x == point.x && snakeNodes.get(i).y == point.y) {
                            c -= 1;
                            point = null;
                            break;
                        }
                    }
                    if (point != null) {
                        foods.add(point);
                    }
                }
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.parseColor("#BB5500"));
        paint.setStrokeWidth(boundWidth);
        canvas.drawLine(0 + boundWidth / 2, 0, 0 + boundWidth / 2, height, paint);            //left bound
        canvas.drawLine(width - boundWidth / 2, 0, width - boundWidth / 2, height, paint);    //right bound
        paint.setStrokeWidth(boundHeight);
        canvas.drawLine(0, 0 + boundHeight / 2, width, 0 + boundHeight / 2, paint);             //up bound
        canvas.drawLine(0, height - boundHeight / 2, width, height - boundHeight / 2, paint);   //down bound

        //貪吃蛇身體繪製
        paint.setColor(Color.parseColor("#00DDDD"));
        for (int i = 0; i < snakeNodes.size(); i++) {
            canvas.drawRect(snakeNodes.get(i).x + margin + boundWidth, snakeNodes.get(i).y + margin + boundHeight,
                    snakeNodes.get(i).x + boundWidth + nodeWidth - margin, snakeNodes.get(i).y + boundHeight + nodeHeight - margin, paint);
        }

        //食物繪製
        paint.setColor(Color.parseColor("#FFBB66"));
        for (int i = 0; i < foods.size(); i++) {
            canvas.drawRect(foods.get(i).x + margin + boundWidth, foods.get(i).y + margin + boundHeight,
                    foods.get(i).x + boundWidth + nodeWidth - margin, foods.get(i).y + boundHeight + nodeHeight - margin, paint);
        }
    }

    //遊戲執行程序
    private class GameTask extends TimerTask {
        @Override
        public void run() {
            if (gameIsRunning) {
                setCurrentDirection();
                snakeTransfer(direction);
                gameHandler.sendMessage(new Message());
            }
        }
    }

    public void onDestroy() {
        bgmPlayer.stop();
        soundPlayer.stop();
        timer.cancel();
    }

    public boolean getOutBound(){
        return outBound;
    }

    public boolean getHitSelf(){
        return hitSelf;
    }
    //還原
    public void revertHitSelf(){
        hitSelf=false;
        score=0;
    }
    //還原
    public void revertOutBound(){
        outBound=false;
        score=0;
    }
}
