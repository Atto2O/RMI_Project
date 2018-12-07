package GUI;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class Toast
{
    public static void makeText(Stage ownerStage, String toastMsg, boolean succes)
    {
        int toastDelay = 1000; //1.0 seconds
        int fadeInDelay = 200; //0.2 seconds
        int fadeOutDelay= 200; //0.2 seconds

        Stage toastStage=new Stage();
        toastStage.setHeight(30);


        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", 15));
        text.setFill(Color.BLACK);

        StackPane root = new StackPane(text);
        if(succes){
            root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(51, 204, 51, 0.4); -fx-padding: 50px;");
        }
        else{
            root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(255, 77, 77, 0.4); -fx-padding: 50px;");
        }
        root.setOpacity(0);
        //root.setLayoutX(350);
        //root.setLayoutY(-20);
        Scene scene = new Scene(root);

        scene.setFill(Color.TRANSPARENT);

        toastStage.setScene(scene);
        toastStage.show();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae) ->
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(toastDelay);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                fadeOutTimeline.setOnFinished((aeb) -> toastStage.close());
                fadeOutTimeline.play();
            }).start();
        });
        fadeInTimeline.play();
    }
}