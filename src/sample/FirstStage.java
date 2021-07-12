package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FirstStage {
    //This covers the first few parts, including the name, upgrade screen, and a story page
    @FXML
    AnchorPane a;
    @FXML
    Button b1;
    @FXML
    ProgressBar p1, p2, p3;
    @FXML
    TextField t1;
    @FXML
    Label l1, l2, l3, l4;

    //Fun global variables are fun
    private final Button[][] buttons = new Button[3][4];
    private final ProgressBar[] progresses = new ProgressBar[3];
    private final Label[] labels = new Label[4];
    private static String name;
    private final int[] stats = new int[5];
    private static int tempexp = 100;
    private static int tempgold = 0;
    private static int totalPoints = 15;
    private static boolean first = true;

    public static void receiveStage(int r, int exp, int g, String n, boolean f){
        //In order to transfer information between FXML files and controllers, I use these "receive" functions that transfer the info through
        //This way I can have multiple scenes and varying pieces of code without taking too long.
        totalPoints = r;
        tempexp = exp;
        tempgold = g;
        name = n;
        first = f;
    }

    @FXML
    void initialize(){
        //This sets the background and actually makes info useful (like tempexp and tempgold, which shouldn't be static)
        a.setBackground(new Background(new BackgroundImage(new Image("resources/backgroundSky.jpg"), null, null, null, null)));
        stats[3] = tempexp;
        stats[4] = tempgold;
        progresses[0] = p1;
        progresses[1] = p2;
        progresses[2] = p3;
        //I don't want to repeatedly ask for their name
        if(!first) stage1();
    }

    @FXML
    private void stage1(){
        //This is the upgrade station, it basically creates all of the buttons/progress bars then tells you to move on
        if(first) {
            name = t1.getText();
        }
        t1.setVisible(false);
        labels[3] = newLabel(1115, 427, "Remaining: " + totalPoints);
        //This just creates the 12 buttons that I wanted automatically
        for (int i = 0; i < buttons.length; i++) {
            labels[i] = newLabel(1115, 247+i*60, "Amount: 0");
            for (int j = 0; j < buttons[i].length; j++) {
                Button b = new Button();
                b.setLayoutX(674 + j*110);
                b.setLayoutY(247+i*60);
                b.setPrefHeight(50);
                b.setPrefWidth(100);
                //IntelliJ forces you to make final variables
                int finalI = i;
                int finalJ = j;
                b.setOnAction(event -> {
                    //This is easier than making up some strange equation that somehow gets me -5, -1, 1, 5 from 0, 1, 2, 3
                    //I can set the OnAction based on what iteration of the loop I'm on
                    switch (finalJ){
                        case 0 -> {
                            ProgressBar p = progresses[finalI];
                            if(p.getProgress() >= 0.25 && totalPoints <= 25) {
                                p.setProgress(p.getProgress() - 0.25);
                                stats[finalI] -= 5;
                                totalPoints += 5;
                                labels[finalI].setText("Amount: " + stats[finalI]);
                                labels[3].setText("Remaining: " + totalPoints);
                            }
                        }
                        case 1 -> {
                            ProgressBar p = progresses[finalI];
                            if(p.getProgress() >= 0.05 && totalPoints <= 29){
                                p.setProgress(p.getProgress() - 0.05);
                                stats[finalI] -= 1;
                                totalPoints += 1;
                                labels[finalI].setText("Amount: " + stats[finalI]);
                                labels[3].setText("Remaining: " + totalPoints);
                            }
                        }
                        case 2 -> {
                            ProgressBar p = progresses[finalI];
                            if(p.getProgress() <= 0.95 && totalPoints >= 1){
                                p.setProgress(p.getProgress() + 0.05);
                                stats[finalI] += 1;
                                totalPoints -= 1;
                                labels[finalI].setText("Amount: " + stats[finalI]);
                                labels[3].setText("Remaining: " + totalPoints);
                            }
                        }
                        case 3 -> {
                            ProgressBar p = progresses[finalI];
                            if(p.getProgress() <= 0.75 && totalPoints >= 5){
                                p.setProgress(p.getProgress() + 0.25);
                                stats[finalI] += 5;
                                totalPoints -= 5;
                                labels[finalI].setText("Amount: " + stats[finalI]);
                                labels[3].setText("Remaining: " + totalPoints);
                            }
                        }
                    }
                });
                switch(j){
                    case 0 -> b.setText("-5");
                    case 1 -> b.setText("-1");
                    case 2 -> b.setText("+1");
                    case 3 -> b.setText("+5");
                }
                b.setFont(new Font(22.0));
                a.getChildren().add(b);
                buttons[i][j] = b;
            }
        }
        //I like to see the things that I made
        p1.setVisible(true);
        p2.setVisible(true);
        p3.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        l4.setVisible(true);
        l1.setText("Alright " + name + ", let's set up your abilities! Add 'skill points' to each of these abilities. " +
                "You can invest up to " + totalPoints + " skill points. Health dictates how much damage you can take, " +
                "movement speed determines how fast you can move, and batteries allow for you to use your special.");
        if(first) {
            b1.setOnAction(event -> stage2());
        }else{
            stats[0] = stats[0]*10 + 10;
            stats[2] = stats[2]*2 + 10;
            stats[1]++;
            b1.setLayoutX(1100);
            b1.setPrefWidth(178);
            b1.setText("Go to Port A");
            b1.setOnAction(event -> {
                Shop.receiveInfo(stats, name);
                try {
                    switchScreen();
                } catch (Exception ignored) {}
            });
        }
    }

    public Label newLabel(int x, int y, String text){
        //I got sick of creating labels
        Label l = new Label();
        l.setLayoutX(x);
        l.setLayoutY(y);
        l.setPrefHeight(50);
        l.setWrapText(true);
        l.setFont(new Font(22.0));
        l.setTextFill(new Color(1, 1, 1, 1));
        l.setText(text);
        a.getChildren().add(l);
        return l;
    }

    public void stage2(){
        //This is just a story panel, nothing special.
        //The health, mana, and speed all need to have initial values so you can at least survive without them
        //0 health isn't very useful
        stats[0] = stats[0]*10 + 10;
        stats[2] = stats[2]*2 + 10;
        stats[1]++;
        for (Label l:labels) {
            l.setVisible(false);
        }
        for (Button[] bu:buttons) {
            for (Button b:bu) {
                b.setVisible(false);
            }
        }
        p1.setVisible(false);
        p2.setVisible(false);
        p3.setVisible(false);
        l2.setVisible(false);
        l3.setVisible(false);
        l4.setVisible(false);
        //I'm an amazing storyteller, look at the amount of very useful detail
        l1.setText("The year is 2082. Humanity has been at war for over 20 years now, and the casualties show no sign of stopping. " +
                "Struggling for resources, any fighters sent out to combat the alien invasion are sent out with the " +
                "bare minimum of fuel and resources: \n pilots only have 1 minute of flight time before they have to return to Earth, " +
                "taking as many invaders as they can with them. \n You are one of these pilots. \n Fight for your world.");
        b1.setLayoutX(1100);
        b1.setPrefWidth(178);
        b1.setText("Go to Port A");
        b1.setOnAction(event -> {
            Shop.receiveInfo(stats, name);
            try {
                switchScreen();
            } catch (Exception ignored) {}
        });
    }

    private void switchScreen() throws Exception{
        //See TitleScreen
        Stage stage = (Stage) b1.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("shop.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
