package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class Bounties {
    //This lets me have the "pick your mission" type screen, as well as provide a way to update your info
    @FXML
    ImageView img1;
    @FXML
    GridPane grid1;
    @FXML
    Label l1, l2, l3;

    //I have a lot of global variables, huh
    private static int[] heroStats;
    private static Weapon weap;
    private static Special spec;
    private static String name;
    private final Quest[] bountyList = new Quest[3];
    private static int level;
    private int bounty;
    private final ArrayList<Integer> pos = new ArrayList<>(Arrays.asList(Controller.randInt(0, 17), Controller.randInt(0, 17), Controller.randInt(0, 17)));
    private final ImageView[][] board = new ImageView[3][6];

    public static void receiveInfo(int[] hStats, int l, Weapon w, Special s, String n){
        //This is this controller's version of the "receive function", this time with a weapon and a special attached
        heroStats = hStats;
        level = l;
        weap = w;
        spec = s;
        name = n;
    }

    @FXML
    void initialize(){
        //This sets up the cool earth display and creates "quests" that have a difficulty + a name
        l2.setText("Gold: " + heroStats[4] + "\nExp: " + heroStats[3]);
        l3.setText("Current Enemy Level: " + level);
        ArrayList<String> names = new ArrayList<>(Arrays.asList("Paris", "Chicago", "Seattle", "New York", "Rome", "New Delhi", "Ghana", "Beijing", "Hong Kong", "Moscow", "Brasilia"));
        for (int i = 0; i < bountyList.length; i++) {
            int d = level + Controller.randInt(-2, 2);
            int name = Controller.randInt(0, names.size());
            bountyList[i] = new Quest(new int[]{level*2 + Controller.randInt(1, 5), level*2 + Controller.randInt(1, 5)}, d, "Defend " + names.get(name));
            names.remove(name);
        }
        img1.setImage(new Image("resources/earth.jpg"));
        //I create a random set of integers corresponding to locations on the board, then put the markers on those locations
        //Probably the weirdest way to do this but it's 4am, I'm lazy, and too many loops is a hassle
        for (int i = 0; i < pos.size(); i++) {
            while(pos.indexOf(pos.get(i)) != pos.lastIndexOf(pos.get(i))){
                pos.set(i, Controller.randInt(0, 17));
            }
        }
        //I just ripped this from TicTacToe and Bold
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new ImageView();
                board[i][j].setPreserveRatio(false);
                if(pos.contains(board[i].length*i + j)){
                    board[i][j].setImage(new Image("resources/squares.png"));
                }
                board[i][j].setTranslateX(25);
                board[i][j].setFitHeight(150);
                board[i][j].setFitWidth(150);
                grid1.add(board[i][j], j, i);
            }
        }
        EventHandler<MouseEvent> z = mouseEvent -> {
            int rowIndex = GridPane.getRowIndex(((ImageView) mouseEvent.getSource()));
            int colIndex = GridPane.getColumnIndex(((ImageView) mouseEvent.getSource()));
            bounty = pos.indexOf(rowIndex*board[0].length + colIndex);
            if(bounty != -1) {
                Quest q = bountyList[bounty];
                l1.setText("Bounty: " + q.getName() + " | Difficulty: " + q.getDifficulty() + " | Rewards: " + q.getGoldReward() + " gold, " + q.getExpReward() + " experience");
            }
        };
        for (ImageView[] i:board) {
            for (ImageView i2:i) {
                i2.setOnMouseClicked(z);
            }
        }
    }

    @FXML
    private void b1Click(){
        //A generic switch stage button that goes to the upgrade screen
        try{
            FirstStage.receiveStage((level*5) - 10, heroStats[3], heroStats[4], name, false);
            switchScreen("firstStage.fxml");
        }catch(Exception ignored){}
    }

    public void chooseBounty() {
        //Another generic switch stage button that goes to the battle screen
        bountyList[bounty].chooseQuest(heroStats, weap, spec, name);
        try {
            switchScreen("sample.fxml");
        } catch (Exception ignored) {}
    }

    private void switchScreen(String name) throws Exception{
        //See TitleScreen
        Stage stage = (Stage) grid1.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(name));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
