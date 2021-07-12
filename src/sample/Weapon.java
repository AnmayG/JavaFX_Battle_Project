package sample;

//Class Weapon:
//2 attributes: int[damage, bullet speed, attack speed], Rectangle[] bullets, Color color
//bullets is going to be all of the bullets that the weapon fires. This way I can have rapid-fire and pulse guns
//Functions:
//Shoot - called by user - fires all bullets in the bullets with a certain delay (speed), then when they hit something they'll explode in the aoe blast
//So I shoot my gun, it fires 3 bullets at a speed of "bullet speed" with a delay of "attack speed", and my attack speed sucks so I have to wait half a second before firing again

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;

public class Weapon {
    //This is the actual weapon itself, made so that I can modify basically anything I want about it
    private final int[] stats;
    private final ArrayList<Rectangle> bullets = new ArrayList<>();
    private final int numBullets;
    private Pane screen = null;
    private Timeline timer;
    private final String bulletColor;
    private final String name;
    private final int cost;
    private String image = null;
    public Weapon(int[] s, int numBullets, String color, Pane p){
        //I have 2 constructors, one for the Controller class and one as a placeholder for the shop class
        //This lets me plug in information when I need it but still be able to pull it out easily
        //Also I need information specific to the battle screen, so I can't properly make the weapon until I reach that stage
        this.stats = s;
        this.name = " ";
        this.cost = 0;
        this.numBullets = numBullets;
        this.bulletColor = color;
        this.screen = p;
    }
    public Weapon(int[] s, int numBullets, String color, String n, int c, String img){
        this.stats = s;
        this.name = n;
        this.cost = c;
        this.numBullets = numBullets;
        this.bulletColor = color;
        this.image = img;
    }

    //generic get functions, these are pretty important
    public int[] getStats() {
        return stats;
    }
    public String getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public int getNumBullets() {
        return numBullets;
    }
    public int getCost() {
        return cost;
    }
    public Weapon clone(Pane screen){
        return new Weapon(this.stats.clone(), this.numBullets, this.bulletColor, screen);
    }

    //I looked up how to make it so I could have an arraylist of any object, and it said to use <T> as a placeholder.
    public <T> void shoot(double[] coords, ArrayList<T> enemies, int angle){
        //This shoots the weapon based on the stats and the current coords
        //If I have an array of bullets, then I can create pulse cannons and the like which have multiple shots
        double angle2 = (angle + 90)*Math.PI/180;
        for (int i = 0; i < this.numBullets; i++) {
            if(angle < 180){
                this.bullets.add(Controller.newRect(10.0, 10.0, bulletColor, 10.0, 10.0, coords[0]+coords[2]/2, coords[1] - 10.0, true, Color.GREEN, this.screen));
            }else{
                this.bullets.add(Controller.newRect(10.0, 10.0, bulletColor, 10.0, 10.0, coords[0]+coords[2]/2, coords[1]+coords[3] - 10.0, true, Color.GREEN, this.screen));
            }
        }
        //Because the bullets can be fired at weird times (and as such not be fired at all) I create a new timeline
        //This also helps with the fact that I don't constantly have to plug in a timer.
        if(timer != null) return;
        timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);
        final int[] i = {0};
        timer.getKeyFrames().add(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    for (int j = 0; j <= i[0]; j++) {
                        if(this.bullets.get(j) != null) {
                            Rectangle b = this.bullets.get(j);
                            //I'm going to be using a polar coordinates system here so that I can have angled shots
                            //It's really convenient, because I can easily get the angle and use the current coordinates as my origin
                            //Using this, I can manipulate the angle to shoot where the enemy is pointing (if I rotate my enemies)
                            //And while I'm nowhere near this stage yet, I could shoot homing missiles too.
                            b.setY(b.getY() - this.stats[1]*Math.sin(angle2));
                            b.setX(b.getX() + this.stats[1]*Math.cos(angle2));
                            if(!inBounds(b)){
                                b.setVisible(false);
                                this.bullets.set(j, null);
                            }
                            for (int k = 0; k < enemies.size(); k++) {
                                if(enemies.get(k) != null) {
                                    //This just checks to see who I'm dealing damage to, as it's kind of important
                                    Rectangle r = null;
                                    Hero h = null;
                                    Enemy e = null;
                                    if(enemies.get(k) instanceof Hero){
                                        h = (Hero)enemies.get(k);
                                        r = h.getAppearance();
                                    }
                                    if(enemies.get(k) instanceof Enemy){
                                        e = (Enemy)enemies.get(k);
                                        r = e.getAppearance();
                                    }
                                    //IntelliJ told me to do this
                                    assert r != null;
                                    //This makes it so I actually hit the enemies and they take damage
                                    if (b.getBoundsInParent().intersects(r.getBoundsInParent())) {
                                        this.bullets.set(j, null);
                                        b.setVisible(false);
                                        assert enemies.get(k) != null;
                                        if(h != null){
                                            h.takeDamage(this.stats[0]);
                                            if (h.getStats()[0] <= 0) {
                                                enemies.set(k, null);
                                                r.setVisible(false);
                                            }
                                        }else {
                                            e.takeDamage(this.stats[0]);
                                            if (e.getStats()[0] <= 0) {
                                                enemies.set(k, null);
                                                r.setVisible(false);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //This makes it so that when I shoot the bullets actually get delayed by some time
                    if(i[0] < this.bullets.size() - 1) i[0]++;
                })
        );
        //When the bullets stop firing, I make sure to clear away all the null bullets that I kept as placeholders
        timer.setOnFinished(event -> {
            //https://stackoverflow.com/questions/4819635/how-to-remove-all-null-elements-from-a-arraylist-or-string-array
            bullets.removeAll(Collections.singleton(null));
        });
        timer.playFromStart();
    }

    public boolean inBounds(Rectangle b){
        return b.getBoundsInParent().intersects(screen.getLayoutBounds())
                && screen.getLayoutBounds().contains(b.getBoundsInParent());
    }
}
