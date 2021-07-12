package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Shop {
    //This is the shop
    //I'm going to have 3 weapons and 3 specials, for a total of 6 items.
    //Each one is going to be unique, mainly because I want them to have pictures too.
    @FXML
    Pane displayScreen;
    @FXML
    Label l1;
    @FXML
    ListView<String> list1;
    @FXML
    Button b1, b2, b3;

    //I have a lot of variables throughout this project, huh
    private static String name;
    private static int[] heroStats;
    private Hero hero;
    private int currentIndex = -1;
    private Weapon useThis;
    private Special useSpec;
    private int gold = 0;
    //Custom made weapons because custom made images and automatically creating "Weapon 1" is boring
    private final Object[] stock = new Object[]{
            new Weapon(new int[]{10, 10, 10}, 1, "BLUE", "Pistol", 0, "resources/pistol.jpg"),
            new Weapon(new int[]{15, 10, 7}, 1, "ORANGE", "Magnum", 15, "resources/magnum.jpg"),
            new Weapon(new int[]{10, 20, 5}, 3, "PURPLE", "Pulse Cannon", 25, "resources/pulseCannon.png"),
            new Weapon(new int[]{150, 10, 1}, 2, "YELLOW", "Sun Gun", 50, "resources/sungun.jpg"),
            //Bullet speed, damage type, radius, damage amount, color, disappear once it hits, number of bullets, and battery cost
            new Special(new String[]{"10", "F", "25", "10", "RED", "false", "1", "10"}, 0, "Sun Blast", "resources/sunblast.jpg"),
            new Special(new String[]{"5", "N", "10", "100", "GREENYELLOW", "true", "1", "10"}, 25, "Cannon", "resources/cannon.jpg"),
            new Special(new String[]{"30", "F", "10", "10", "FUCHSIA", "true", "5", "5"}, 50, "Pink Death", "resources/pinkdeath.png"),
            //I know that this has the not-so-transparent background, but I don't really care
            new Special(new String[]{"40", "N", "75", "1000", "WHEAT", "false", "1", "15"}, 100, "Normal Death", "resources/normaldeath.png")
    };
    //I like having a tracker for what's bought
    private final int[] boughtStock = new int[stock.length];

    @FXML
    void initialize(){
        //Having a static hero class is really, really hard to pull off, so I just move its stats around the project
        //Plus it let's me modify the hero to my heart's content
        displayScreen.setStyle("-fx-border-color: black");
        hero = new Hero(heroStats, null, null, null, name, null, null, null);
        gold = heroStats[4];
        setL1();
        b2Click();
    }

    public void setL1(){
        //Having a blank name is something that must be accounted for
        if(name != null && !name.isBlank()) {
            l1.setText(name + ", welcome to Port A! Port A is the main base of operations for Space Force Pilots, and " +
                    "it's where you can buy upgrades for your ship. While your ship can be upgraded for free at the deployment area" +
                    ", it's your responsibility to purchase weapons and other features. Click the 'next' button to see" +
                    " what's available for purchase. \nCurrent Gold: " + gold);
        }else{
            l1.setText("Welcome to Port A! Port A is the main base of operations for Space Force Pilots, and " +
                    "it's where you can buy upgrades for your ship. While your ship can be upgraded for free at the deployment area" +
                    ", it's your responsibility to purchase weapons and other features. Click the 'next' button to see" +
                    " what's available for purchase. \nCurrent Gold: " + gold);
        }
    }

    @FXML
    private void b1Click(){
        //This buys the weapon that's on the screen right now and says that you bought it.
        l1.setText(" ");
        if(stock[currentIndex] instanceof Weapon){
            useThis = (Weapon)stock[currentIndex];
            if(gold >= useThis.getCost()) {
                for (int i = 0; i < boughtStock.length / 2; i++) {
                    if (boughtStock[i] == 2) {
                        boughtStock[i] = 1;
                    }
                }
                if(boughtStock[currentIndex] == 1){
                    l1.setText("You have already bought the " + useThis.getName() + ". ");
                }else{
                    gold -= useThis.getCost();
                    l1.setText("You have bought the " + useThis.getName() + ". ");
                }
                boughtStock[currentIndex] = 2;
                l1.setText(l1.getText() + "You have equipped the " + useThis.getName() + ".");
            }else{
                l1.setText("You don't have enough money! Current Gold: " + gold);
            }
        }else{
            Special s = (Special) stock[currentIndex];
            if(Integer.parseInt(s.getStats()[7]) <= heroStats[2]) {
                useSpec = s;
                if(gold >= useSpec.getCost()) {
                    for (int i = boughtStock.length / 2 - 1; i < boughtStock.length; i++) {
                        if (boughtStock[i] == 2) boughtStock[i] = 1;
                    }
                    if(boughtStock[currentIndex] == 1){
                        l1.setText("You have already bought the " + useSpec.getName() + ". ");
                    }else{
                        gold -= useSpec.getCost();
                        l1.setText("You have bought the " + useSpec.getName() + ". ");
                    }
                    boughtStock[currentIndex] = 2;
                    l1.setText(l1.getText() + "You have equipped the " + useSpec.getName() + ".");
                }else{
                    l1.setText("You don't have enough money! Current Gold: " + gold);
                }
            }else{
                l1.setText("You don't have enough battery space to fire this weapon. Try upgrading your battery.");
            }
        }
    }

    @FXML
    private void b2Click(){
        //This looks at the current weapon being sold, displays the image, and displays the information
        setL1();
        currentIndex = (currentIndex + 1)%8;
        displayScreen.getChildren().clear();
        list1.getItems().clear();
        Rectangle r = new Rectangle(50, 0, 50, 50);
        if(stock[currentIndex] instanceof Weapon){
            Weapon w = (Weapon)stock[currentIndex];
            Image i = new Image(w.getImage());
            r.setWidth(Math.min(i.getWidth(), 900));
            r.setHeight(Math.min(i.getHeight(), 600));
            r.setX(450-r.getWidth()/2);
            r.setFill(new ImagePattern(i));
            list1.getItems().add(w.getName());
            list1.getItems().add("Type: Weapon");
            list1.getItems().add("Damage: " + w.getStats()[0]);
            list1.getItems().add("Bullet Speed: " + w.getStats()[1]);
            list1.getItems().add("Attack Speed: " + w.getStats()[2]);
            list1.getItems().add("Bullets Per Shot: " + w.getNumBullets());
            list1.getItems().add("Cost: " + w.getCost());
        }else{
            Special s = (Special)stock[currentIndex];
            Image i = new Image(s.getImage());
            r.setWidth(Math.min(i.getWidth(), 900));
            r.setHeight(Math.min(i.getHeight(), 600));
            r.setX(450-r.getWidth()/2);
            r.setFill(new ImagePattern(i));
            list1.getItems().add(s.getName());
            list1.getItems().add("Type: Special");
            String[] st = s.getStats();
            list1.getItems().add("Damage: " + st[3]);
            list1.getItems().add("Damage Type: " + st[1]);
            list1.getItems().add("Bullet Speed: " + st[0]);
            list1.getItems().add("Bullets Per Shot: " + st[6]);
            list1.getItems().add("Radius: " + st[2]);
            list1.getItems().add("Cost: " + s.getCost());
        }
        displayScreen.getChildren().add(r);
    }

    @FXML
    private void b3Click(){
        //This just makes sure that you aren't leaving before you actually equipped a weapon
        if(useThis == null && useSpec != null){
            l1.setText("Your ship needs to be fully equipped with a weapon and special before proceeding. " +
                    "Current Equipment: <missing weapon> and " + useSpec.getName());
            return;
        }else if(useThis != null && useSpec == null){
            l1.setText("Your ship needs to be fully equipped with a weapon and special before proceeding. " +
                    "Current Equipment: " + useThis.getName() + " and <missing special>.");
            return;
        }else if(useThis == null){
            l1.setText("Your ship needs to be fully equipped with a weapon and special before proceeding. " +
                    "Current Equipment: <missing weapon> and <missing special>.");
            return;
        }
        hero.setWeapon(useThis);
        hero.setSpecial(useSpec);
        Bounties.receiveInfo(heroStats, hero.getExp()/20, useThis, useSpec, name);
        try{
            switchScreen();
        }catch (Exception ignored){}
    }

    private void switchScreen() throws Exception{
        //See TitleScreen
        Stage stage = (Stage) b3.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("bounties.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void receiveInfo(int[] stats, String n){
        //This is the shop's version of the "receive function"
        heroStats = stats;
        name = n;
    }
}
