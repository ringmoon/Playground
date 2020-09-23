package andy.playground;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        TextView TextView1 = (TextView)findViewById(R.id.tvTest);
//        TextView1.setText("手機銀幕大小為 "+metrics.widthPixels+" X "+metrics.heightPixels+"\n"+metrics.density);
    }

    public void goMemoryCardGame(String IP){
        Intent intent=new Intent(this,Memory_card_game.class);
        Bundle bundle=new Bundle();
        bundle.putString("IP",IP);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void goSnakeGame(String level){
        Intent intent=new Intent(this,SnakeGame.class);
        Bundle bundle=new Bundle();
        bundle.putString("level",level);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void chooseMemoryCardGame(View view){
        PopupMenu popupMenu =new PopupMenu(this,view);
        popupMenu.inflate(R.menu.memory_card_game_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String IP="";
                if(item.getTitle().toString().equals("YO-KAI Watch")||item.getTitle().toString().equals("妖怪手錶")){
                    IP="YO-KAI Watch";
                }else if(item.getTitle().toString().equals("Pokemon")||item.getTitle().toString().equals("神奇寶貝")){
                    IP="Pokemon";
                }else if(item.getTitle().toString().equals("One Piece")||item.getTitle().toString().equals("海賊王")){
                    IP="One Piece";
                }
                goMemoryCardGame(IP);
                return true;
            }
        });
        popupMenu.show();
    }
    public void chooseSnakeGame(View view){
        PopupMenu popupMenu =new PopupMenu(this,view);
        popupMenu.inflate(R.menu.snake_game_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String level="";
                if(item.getTitle().toString().equals("Simple")||item.getTitle().toString().equals("簡單")){
                    level="Simple";
                }else if(item.getTitle().toString().equals("Normal")||item.getTitle().toString().equals("正常")){
                    level="Normal";
                }else if(item.getTitle().toString().equals("Difficult")||item.getTitle().toString().equals("困難")){
                    level="Difficult";
                }
                goSnakeGame(level);
                return true;
            }
        });
        popupMenu.show();
    }
}
