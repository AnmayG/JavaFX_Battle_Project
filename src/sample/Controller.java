package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class Controller {
    //This is the actual place where the "battles" happen
    //Timer info can be found here https://asgteach.com/2011/10/javafx-animation-and-binding-simple-countdown-timer-2/
    //Image info can be found here https://stackoverflow.com/questions/22848829/how-do-i-add-an-image-inside-a-rectangle-or-a-circle-in-javafx
    @FXML
    AnchorPane pane1;
    @FXML
    Pane battleScreen;
    @FXML
    Label l1, l2, l3, l4, l5;
    @FXML
    Button b1;
    @FXML
    ProgressBar p1, p2, p3;
    @FXML
    ToggleButton tb1;

    private static int[] heroStats;
    private static String name;
    private static Weapon weap;
    private static Special spec;
    private static int difficulty;
    private static int[] rewards;
    private Hero hero;
    private int tracker = 10;
    private int frameNum = 0;
    private Timeline timer = null;
    private final ArrayList<Enemy> enemies = new ArrayList<>();

    public static void receiveHero(int[] stats, int diff, Weapon w, Special s, String n, int[] r){
        //Generic "receive info button", this time tailored to the hero
        heroStats = stats;
        difficulty = diff;
        weap = w;
        spec = s;
        name = n;
        rewards = r;
    }

    public void buildHero(){
        //Because I'm actually going to be using the hero on this screen, I need to build it up again using past information
        int[] s = heroStats.clone();
        hero = new Hero(s, newRect(50.0, 50.0, "resources/galaga.png", 5.0, 5.0, 575.0, 600.0, true, null, battleScreen),
                weap.clone(battleScreen), null, name, new ProgressBar[]{p1, p2},
                new Label[]{l2, l3}, battleScreen);
        //Bullet speed, damage type, radius, damage amount, color, disappear once it hits, number of bullets, and battery cost
        hero.setSpecial(spec.clone(hero, battleScreen));
    }

    public static Rectangle newRect(double width, double height, String f, double arcWidth, double arcHeight, double layoutX, double layoutY, boolean vis, Color stroke, Pane p){
        //This creates a new rectangle/sprite for me to use and show on the screen
        Rectangle output;
        Color fill;
        try{
            if(f != null){
                //I can't find it again, but this is a way to get a color from a string
                Field field = Class.forName("javafx.scene.paint.Color").getField(f);
                fill = (Color) field.get(null);
            }else{
                fill = new Color(Math.random(), Math.random(), Math.random(), 1);
            }
            output = new Rectangle(width, height, fill);
        }catch (Exception e){
            assert f != null;
            Image image = new Image(f);
            ImagePattern fillImage = new ImagePattern(image);
            output = new Rectangle(width, height, fillImage);
        }
        output.setStrokeType(StrokeType.INSIDE);
        output.setArcHeight(arcHeight);
        output.setArcWidth(arcWidth);
        output.setLayoutX(layoutX);
        output.setLayoutY(layoutY);
        if(stroke != null) output.setStroke(stroke);
        output.setVisible(vis);
        //https://stackoverflow.com/questions/26327216/javafx-invoking-button-to-draw-rectangle-on-scene
        p.getChildren().add(output);
        return output;
    }

    //Generic random integer function, it's pretty useful
    public static int randInt(int min, int max){
        return (int)(Math.random()*(max + 1 - min)) + min;
    }

    @FXML
    void initialize(){
        //Sets background of the battle screen
        //https://www.geeksforgeeks.org/javafx-background-class/
        battleScreen.setBackground(new Background(new BackgroundImage(new Image("resources/backgroundSky.jpg"), null, null, null, null)));
        pane1.setBackground(new Background(new BackgroundImage(new Image("resources/backgroundSky.jpg"), null, null, null, null)));
    }

    @FXML
    private void start(){
        //start button needs to work
        setUpBattle(difficulty);
    }

    boolean cheat = true;
    public void setUpBattle(int difficulty){
        //This creates the timer and the enemies so that the battle actually works and isn't just a blank screen
        //I also have a "cheat" button so I can skip battles and automatically win
        b1.setVisible(false);
        pane1.requestFocus();
        p1.setVisible(true);
        p2.setVisible(true);
        p3.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        l4.setVisible(true);
        //https://stackoverflow.com/questions/27712213/how-do-i-make-a-simple-solid-border-around-a-flowpane-in-javafx
        battleScreen.setStyle("-fx-border-color: black");
        battleScreen.getChildren().clear();
        cheat = tb1.isSelected();
        for (int i = 0; i < difficulty; i++) {
            addEnemy(difficulty, difficulty*25, i);
        }
        buildHero();
        if(timer != null) timer.stop();
        timer = new Timeline();
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1), event -> ticker(timer)));
        timer.playFromStart();
    }

    public void ticker(Timeline timer){
        //This is the equivalent of the "draw" function, it just checks what's going to happen every "tick"
        hero.move();
        tracker++;
        frameNum++;
        //Sometimes there are null enemies for some reason, this just clears them out
        enemies.removeAll(Collections.singleton(null));
        if(cheat) enemies.clear();
        //1 minute rounds, 600 frames
        if(!hero.getAppearance().isVisible() || frameNum >= 600){
            //Loss condition is invisible or ran out of fuel. This just clears the screen and shows that you lost
            battleScreen.getChildren().clear();
            battleScreen.getChildren().add(l1);
            l1.toFront();
            p1.setVisible(false);
            p2.setVisible(false);
            p3.setVisible(false);
            l2.setVisible(false);
            l3.setVisible(false);
            l4.setVisible(false);
            l1.setText("You lost!");
            if(frameNum >= 600) l1.setText("You ran out of fuel!");
            timer.stop();
            b1.setVisible(true);
            b1.setOnAction(event -> {
                Shop.receiveInfo(hero.getInitStats(), hero.getName());
                try {
                    switchScreen();
                } catch (Exception ignored) {}
            });
            b1.setText("Back to Port A");
            return;
        }else{
            if(enemies.size() == 0){
                //Win condition is that all the enemies are gone. This clears the screen, shows you won, and adds rewards
                battleScreen.getChildren().clear();
                battleScreen.getChildren().add(l1);
                battleScreen.getChildren().add(l5);
                l1.toFront();
                l5.toFront();
                b1.setVisible(false);
                p1.setVisible(false);
                p2.setVisible(false);
                p3.setVisible(false);
                l2.setVisible(false);
                l3.setVisible(false);
                l4.setVisible(false);
                l1.setText("You win!");
                l5.setText("You earned " + rewards[0] + " gold and " + rewards[1] + " experience points.");
                timer.getKeyFrames().clear();
                timer.stop();
                hero.addGold(rewards[0]);
                hero.addExp(rewards[1]);
                b1.setVisible(true);
                b1.setOnAction(event -> {
                    Shop.receiveInfo(hero.getInitStats(), hero.getName());
                    try {
                        switchScreen();
                    } catch (Exception ignored) {}
                });
                b1.setText("Back to Port A");
                return;
            }
        }
        //Natural battery regeneration, solar panels are a thing
        if(frameNum%30 == 0 && hero.getStats()[2] < heroStats[2]){
            hero.getStats()[2] += 1;
        }
        p3.setProgress((600-frameNum)/600.0);
        l4.setText("Time left: " + (60 - frameNum/10) + "s");
        //Because I have specials that apply burn status effects, I actually "tint" the enemy red for a few seconds
        for (Enemy e : enemies) {
            if(e.getEffectApplied() != 0 && e.getEffectApplied() <= 20){
                e.addEffectApplied(1);
                if(!e.getImage().equals("resources/hitenemy1.png") &&
                        !e.getImage().equals("resources/hitenemy2.png") &&
                        !e.getImage().equals("resources/hitenemy3.png"))
                    e.setImage("resources/hit" + e.getImage().substring(10));
            }else if(e.getImage().equals("resources/hitenemy1.png") ||
                        e.getImage().equals("resources/hitenemy2.png") ||
                        e.getImage().equals("resources/hitenemy3.png")){
                e.setImage("resources/" + e.getImage().substring(13));
            }
            e.move(hero.getCoords(), randInt(1, 2));
            noEnemyCollisions();
            if(randInt(1, 10) == 1) e.getWeapon().shoot(e.getCoords(), new ArrayList<>(Collections.singletonList(hero)), 180);
        }
        hero.updateDisplays();
    }

    private void switchScreen() throws Exception{
        //See TitleScreen
        Stage stage = (Stage) b1.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("shop.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addEnemy(int difficulty, int length, int number){
        //Read the function name
        //This works by creating an enemy then dealing with the number of enemies and the difficulty
        //Do not mess with difficulty 15, it takes max gear, max stats, the enemies have 250 health, and basically oneshot you
        String s = "resources/enemy" + randInt(1, 3) + ".png";
        double y = 50.0;
        if(600.0 - length < 100 && number > 15){
            y += 50.0;
        }
        enemies.add(new Enemy(new int[]{100 + difficulty*10, 10, 10},
                newRect(50.0, 50.0, s, 5.0, 5.0, 600.0-length, y, true, Color.BLACK, battleScreen),
                new Weapon(new int[]{10 + difficulty*5, 10, 10}, 1, "GREEN", battleScreen), s, battleScreen));
        noEnemyCollisions();
    }

    public void noEnemyCollisions() {
        //This checks to make sure that anybody colliding gets pushed back
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                Rectangle e = enemies.get(i).getAppearance();
                Rectangle e2 = enemies.get(j).getAppearance();
                if(e.getBoundsInLocal().intersects(e2.getBoundsInLocal())){
                    if(e.getX() >= e2.getX()){
                        e.setX(e2.getX() + e2.getWidth());
                        if(!inBounds(e)){
                            for (Enemy en:enemies) {
                                //If it's out of bounds, everybody needs to go back a step
                                en.getAppearance().setX(en.getAppearance().getX() - Math.abs(en.getVelX()));
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean inBounds(Rectangle appearance){
        //This is just nice to have, it checks if you're inside or outside of the battle screen
        return appearance.getBoundsInParent().intersects(battleScreen.getLayoutBounds())
                && battleScreen.getLayoutBounds().contains(appearance.getBoundsInParent());
    }

    //https://www.youtube.com/watch?v=L5GJ-i_6uGQ
    @FXML
    private void onPress(KeyEvent e) {
        //I like having diagonal movement
        if(hero.getAppearance().isVisible() && hero != null) {
            int speed = hero.getStats()[1];
            if (e.getCode() == KeyCode.W) hero.setVelY(-1 * speed);
            if (e.getCode() == KeyCode.A) hero.setVelX(-1 * speed);
            if (e.getCode() == KeyCode.S) hero.setVelY(speed);
            if (e.getCode() == KeyCode.D) hero.setVelX(speed);
        }
    }

    @FXML
    private void onRelease(KeyEvent e){
        //I especially like having smooth diagonal movement
        if(hero.getAppearance().isVisible() && hero != null) {
            if (e.getCode() == KeyCode.W) hero.setVelY(0);
            if (e.getCode() == KeyCode.A) hero.setVelX(0);
            if (e.getCode() == KeyCode.S) hero.setVelY(0);
            if (e.getCode() == KeyCode.D) hero.setVelX(0);
        }
    }

    @FXML
    private void mouseClick(MouseEvent e){
        //I do the weapon firing independent of everything else because the input system isn't really suited for things like this
        if(hero.getAppearance().isVisible() && hero != null) {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (tracker >= 10 - hero.getWeapon().getStats()[2]) {
                    hero.getWeapon().shoot(hero.getCoords(), enemies, 0);
                    tracker = 0;
                }
            }else{
                hero.shootSpecial(enemies);
            }
        }
    }
}
