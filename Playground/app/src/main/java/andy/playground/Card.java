package andy.playground;

/**
 * Created by Andy on 2017/11/9.
 */

public class Card {
    private int id;
    private int image;
    private int backImage;

    public Card(int id,int image,int backImage){
        this.id=id;
        this.image=image;
        this.backImage=backImage;
    }
    public void setId(int id){
        this.id=id;
    }
    public void setImage(int image){
        this.image=image;
    }
    public void setBackImage(int backImage){
        this.backImage=backImage;
    }
    public int getId(){
        return id;
    }
    public int getImage(){
        return image;
    }
    public int getBackImage(){
        return backImage;
    }
}
