package sample;

//Class Enemy:
//This is an enemy
//4 attributes: int[health, movement speed], Rectangle appearance, and Weapon weapons
//The level acts as a multiplier, but I don't include it here. Instead, the level's effect is applied in the controller
//Functions: Same as player. I'm going to have randomizers for when each thing happens, as well as specific "time frames" dictated every second.

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Enemy {
    //A lot of this is just copied from the hero class
    private final int[] stats;
    private final Rectangle appearance;
    private final Weapon weapon;
    private final Pane screen;
    private int velX = 0;
    private String image;
    private int effectApplied = 0;
    public Enemy(int[] s, Rectangle ap, Weapon w, String img, Pane pa){
        //This doesn't have any fancy overloading or anything, it's nice and simple
        this.image = img;
        this.stats = s;
        this.appearance = ap;
        this.weapon = w;
        this.screen = pa;
    }
    //Pretty self-explanatory
    public Rectangle getAppearance() {
        return appearance;
    }
    public int[] getStats() {
        return stats;
    }
    public int getVelX() {
        return velX;
    }
    public Weapon getWeapon() {
        return weapon;
    }
    public int getEffectApplied() {
        return effectApplied;
    }
    public String getImage() {
        return image;
    }
    public void setVelX(int velX) {
        this.velX = velX;
    }
    public void takeDamage(int damage){
        this.stats[0] -= damage;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void addEffectApplied(int effectApplied) {
        this.effectApplied += effectApplied;
    }

    public double[] getCoords(){
        return new double[]{this.appearance.getX() + this.appearance.getLayoutX(), this.appearance.getY() + this.appearance.getLayoutY(), this.appearance.getWidth(), this.appearance.getHeight()};
    }
    public void move(double[] targetCoords, int chaos){
        //It's always going to try and move towards the player, but there's a chaos factor that messes with it
        if(this.effectApplied != 0) {
            this.takeDamage(1);
            this.getAppearance().setFill(new ImagePattern(new Image(this.image)));
        }
        int chaos2 = Controller.randInt(1, 2);
        if (this.getCoords()[0]*chaos > targetCoords[0]*chaos2) {
                this.setVelX(-1 * this.stats[1]);
        } else {
            this.setVelX(this.stats[1]);
        }
        Hero.move(this.getVelX(), 0, this.getAppearance(), this.screen);
    }
}
