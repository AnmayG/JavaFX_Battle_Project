package sample;

//Class Special:
//This is the special ability that the hero gets
//It ended up pretty complex, but it works nicely

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;

public class Special{
    //This is basically a copy of the weapon class with a few addons
    private Hero hero = null;
    private final String[] stats;
    private final ArrayList<Rectangle> bullets = new ArrayList<>();
    private Pane screen = null;
    private Timeline timer;
    private int cost = 0;
    private String image = "";
    private String name = "";
    public Special(Hero h, String[] s, Pane pa){
        //This is the constructor for the battle screen where it's actually being used
        this.stats = s;
        this.hero = h;
        this.screen = pa;
    }
    public Special(String[] s, int c, String name, String img){
        //This is the display constructor seen in the shop
        this.stats = s;
        this.cost = c;
        this.image = img;
        this.name = name;
    }

    public String[] getStats() {
        return stats;
    }
    public int getCost() {
        return cost;
    }
    public String getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public Special clone(Hero h, Pane p){
        return new Special(h, this.stats, p);
    }

    public void shoot(ArrayList<Enemy> enemies, int angle){
        //I copy-pasted this from the weapons class but added a few things
        //Because the bullets can be fired at weird times (and as such not be fired at all) I create a new timeline
        //This also helps with the fact that I don't constantly have to plug in a timer.
        double[] coords = hero.getCoords();
        double angle2 = (angle + 90)*Math.PI/180;
        for (int i = 0; i < Integer.parseInt(this.stats[6]); i++) {
            int dimension = Integer.parseInt(this.stats[2]);
            this.bullets.add(Controller.newRect(dimension, dimension, this.stats[4], dimension, dimension, coords[0]+coords[2]/2, coords[1], true, Color.GREEN, this.screen));
        }
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
                            b.setY(b.getY() - Integer.parseInt(this.stats[0])*Math.sin(angle2));
                            b.setX(b.getX() + Integer.parseInt(this.stats[0])*Math.cos(angle2));
                            if(!inBounds(b)){
                                b.setVisible(false);
                                this.bullets.set(j, null);
                            }
                            for (int k = 0; k < enemies.size(); k++) {
                                if(enemies.get(k) != null) {
                                    //This prioritizes ship-to-ship battles, so yeah
                                    Enemy e = enemies.get(k);
                                    Rectangle r = e.getAppearance();
                                    //IntelliJ told me to do this
                                    assert r != null;
                                    if (b.getBoundsInParent().intersects(r.getBoundsInParent())) {
                                        if(Boolean.parseBoolean(this.stats[5])) {
                                            this.bullets.set(j, null);
                                            b.setVisible(false);
                                        }
                                        //If I choose to add a fire effect (f), I modify the enemy so that it knows it's affected
                                        if(stats[1].equals("F")) {
                                            e.addEffectApplied(1);
                                        }
                                        assert enemies.get(k) != null;
                                        e.takeDamage(Integer.parseInt(this.stats[3]));
                                        if (e.getStats()[0] <= 0) {
                                            enemies.set(k, null);
                                            r.setVisible(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(i[0] < this.bullets.size() - 1) i[0]++;
                })
        );
        timer.setOnFinished(event -> bullets.removeAll(Collections.singleton(null)));
        timer.playFromStart();
    }

    public boolean inBounds(Rectangle b){
        return b.getBoundsInParent().intersects(screen.getLayoutBounds())
                && screen.getLayoutBounds().contains(b.getBoundsInParent());
    }
}
