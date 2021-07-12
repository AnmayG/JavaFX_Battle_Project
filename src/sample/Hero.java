package sample;

//Class Hero:
//This is the player
//3 attributes: int[health, movement speed, accuracy, exp, gold], Special spec, Rectangle appearance, and Weapon[2] weapons

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Hero {
    //I really need to work on managing global variables
    private final int[] stats;
    private Special special;
    private String name;
    private final Rectangle appearance;
    private Weapon weapon;
    private final Pane screen;
    private int velX = 0;
    private int velY = 0;
    private int exp;
    private int gold;
    private final ProgressBar[] display;
    private final Label[] displayText;
    private final int[] initStats;
    public Hero(int[] s, Rectangle a, Weapon w, Special spec, String name, ProgressBar[] d, Label[] d2, Pane p){
        //The longest constructor for the most important piece
        this.stats = s;
        this.initStats = s.clone();
        this.name = name;
        this.display = d;
        this.displayText = d2;
        this.appearance = a;
        this.weapon = w;
        this.special = spec;
        this.exp = s[3];
        this.gold = s[4];
        this.screen = p;
    }

    //Sets and gets for everything
    public int[] getStats() {
        return stats;
    }
    public Rectangle getAppearance() {
        return appearance;
    }
    public int getExp() {
        return exp;
    }
    public Weapon getWeapon() {
        return weapon;
    }
    public String getName() {
        return name;
    }
    public double[] getCoords(){
        return new double[]{this.appearance.getX() + this.appearance.getLayoutX(), this.appearance.getY() + this.appearance.getLayoutY(), this.appearance.getWidth(), this.appearance.getHeight()};
    }
    public int[] getInitStats() {
        return initStats;
    }
    public int getGold() {
        return gold;
    }

    public void takeDamage(int damage){
        this.stats[0] -= damage;
    }
    public void setVelX(int velocity) {
        this.velX = velocity;
    }
    public void setVelY(int velocity){this.velY = velocity;}
    public void setWeapon(Weapon weapons) {
        this.weapon = weapons;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSpecial(Special special) {
        this.special = special;
    }
    public void addExp(int amount){
        this.exp += amount;
        this.stats[3] += amount;
        this.initStats[3] += amount;
    }
    public void addGold(int amount){
        this.gold += amount;
        this.stats[4] += amount;
        this.initStats[4] += amount;
    }
    public void shootSpecial(ArrayList<Enemy> enemies){
        if(this.stats[2] - Integer.parseInt(this.special.getStats()[7]) >= 0) {
            this.stats[2] -= Integer.parseInt(this.special.getStats()[7]);
            this.special.shoot(enemies, 0);
        }
    }

    public void updateDisplays(){
        //I have the progress bars as a parameter so it's easy for me to actually put this stuff in.
        //I could have done it in the controller itself, but this is much easier and doesn't require as much finagling
        display[0].setProgress((double) this.stats[0]/initStats[0]);
        displayText[0].setText("Health: " + this.stats[0]);
        display[1].setProgress((double) this.stats[2]/initStats[2]);
        displayText[1].setText("Charge: " + this.stats[2]);
    }

    public void move(){
        //Intersection code from https://stackoverflow.com/questions/44861123/how-to-check-if-rectangular-node-is-in-the-window
        //IntelliJ recommended I create a function, then call it in another place
        move(this.velX, this.velY, this.appearance, this.screen);
    }

    public static void move(int velX, int velY, Rectangle appearance, Pane screen) {
        //I make sure that anything that moves gets teleported back in with everything else.
        //The enemies leaving the bounds drove me crazy
        appearance.setX(appearance.getX() + velX);
        while(!(appearance.getBoundsInParent().intersects(screen.getLayoutBounds())
                && screen.getLayoutBounds().contains(appearance.getBoundsInParent()))){
            appearance.setX(appearance.getX() - velX);
        }
        appearance.setY(appearance.getY() + velY);
        while(!(appearance.getBoundsInParent().intersects(screen.getLayoutBounds())
                && screen.getLayoutBounds().contains(appearance.getBoundsInParent()))){
            appearance.setY(appearance.getY() - velY);
        }
    }
}
