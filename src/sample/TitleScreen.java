package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;

public class TitleScreen {
    //This is just the title screen/front page, all it really has is the background and the cooler button
    @FXML
    Button b1;
    @FXML
    AnchorPane a;
    @FXML
    void initialize(){
        a.setBackground(new Background(new BackgroundImage(new Image("resources/TitleScreen.png"), null, null, null, null)));
        b1.setBackground(new Background(new BackgroundImage(new Image("resources/startButtonImg.png"), null, null, null, null)));
    }
    //https://gist.github.com/pethaniakshay/302072fda98098a24ce382a361bdf477
    @FXML
    private void switchScreen(ActionEvent event) throws Exception{
        //Javafx works by putting "scenes" (everything that's described in the FXML file) on a "stage".
        //The way this works is by getting a new scene (through an intermediary known as a parent) and putting it on the stage.
        Stage stage = (Stage) b1.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("titleScreen.fxml"));
        if(event.getSource()==b1){
            root = FXMLLoader.load(getClass().getResource("firstStage.fxml"));
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
