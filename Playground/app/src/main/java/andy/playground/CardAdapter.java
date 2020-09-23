package andy.playground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Andy on 2017/11/9.
 */

public class CardAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private List<Card> cardList;


    public CardAdapter(Context context,String IP){
        layoutInflater =LayoutInflater.from(context);
        cardList=new ArrayList<>();
        if(IP.equals("YO-KAI Watch")){
            for(int i=0;i<2;i++){
                cardList.add(new Card(i,R.drawable.y01,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y02,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y03,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y04,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y05,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y06,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y07,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y08,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y09,R.drawable.yokai_back));
                cardList.add(new Card(i,R.drawable.y10,R.drawable.yokai_back));
            }
        }else if(IP.equals("Pokemon")){
            for(int i=0;i<2;i++){
                cardList.add(new Card(i,R.drawable.p01,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p02,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p03,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p04,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p05,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p06,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p07,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p08,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p09,R.drawable.pokemon_back));
                cardList.add(new Card(i,R.drawable.p10,R.drawable.pokemon_back));
            }
        }else if(IP.equals("One Piece")){
            for(int i=0;i<2;i++){
                cardList.add(new Card(i,R.drawable.o01,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o02,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o03,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o04,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o05,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o06,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o07,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o08,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o09,R.drawable.one_piece_back));
                cardList.add(new Card(i,R.drawable.o10,R.drawable.one_piece_back));
            }
        }
        shuffleCardList();
    }

    private void shuffleCardList(){
        Random random=new Random();
        for(int i=0;i<cardList.size();i++){
            Card card=cardList.get(i);
            int ran=random.nextInt(cardList.size());
            cardList.set(i,cardList.get(ran));
            cardList.set(ran,card);
        }
    }
    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int i) {
        return cardList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return cardList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view ==null){
            view=layoutInflater.inflate(R.layout.gridview_item,viewGroup,false);
        }
        Card card=cardList.get(i);
        ImageView ivImage=(ImageView) view.findViewById(R.id.ivImage);
        ivImage.setImageResource(card.getBackImage());
        return view;
    }
}
