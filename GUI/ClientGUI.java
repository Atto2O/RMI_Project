package GUI;

import RemoteObject.Garage;
import javafx.application.Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import Client.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class ClientGUI extends Application {
    public static Client client;
    public static Garage h;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("RMI Project");
        stage.setResizable(false);

        int width = 700;
        int height = 400;

        //region<INPUT_Limits>
        int max_ip_chars = 8;
        int max_port_chars = 8;
        int max_username_chars = 10;
        int max_password_chars = 12;
        //endregion

        //region<COLORS>
        Color background_start_color = Color.DARKSLATEBLUE;
        Color background_signin_color = Color.LIGHTSTEELBLUE;
        Color background_signup_color = Color.LIGHTSTEELBLUE;
        //endregion

        Rectangle background = new Rectangle(width,height, background_start_color);

        //ADD TEXT
        //region<IP/Port>
        TextField clientIP = new TextField();
        clientIP.setOnKeyTyped(event ->{
            int maxCharacters = max_ip_chars;
            if(clientIP.getText().length() > maxCharacters) event.consume();
        });
        clientIP.setLayoutX((width-170)/2);

        TextField clientPORT = new TextField();
        clientPORT.setOnKeyTyped(event ->{
            int maxCharacters = max_port_chars;
            if(clientPORT.getText().length() > maxCharacters) event.consume();
        });
        clientPORT.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    clientPORT.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        clientPORT.setLayoutX((width-170)/2);

        TextField serverIP = new TextField();
        serverIP.setOnKeyTyped(event ->{
            int maxCharacters = max_ip_chars;
            if(serverIP.getText().length() > maxCharacters) event.consume();
        });
        serverIP.setLayoutX((width-170)/2);

        TextField serverPORT = new TextField();
        serverPORT.setOnKeyTyped(event ->{
            int maxCharacters = max_port_chars;
            if(serverPORT.getText().length() > maxCharacters) event.consume();
        });
        serverPORT.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    serverPORT.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        serverPORT.setLayoutX((width-170)/2);

        Button connect = new Button("Connect");
        connect.setLayoutX((width-70)/2);
        connect.setLayoutY(height-70);

        Group root1 = new Group(background, clientIP, clientPORT, serverIP, serverPORT, connect);
        Scene scene1 = new Scene(root1, width, height);
        //endregion

        //Check while writing username if exists (eventHandler);
        //region<LOGIN/SIGNUP>
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMaxWidth((width-40)/2);
        tabPane.setTabMinWidth((width-40)/2);
        //region<SIGNIN>
            background = new Rectangle(width,height, background_signin_color);

            TextField username = new TextField();
            username.setOnKeyTyped(event ->{
                int maxCharacters = max_username_chars;
                if(username.getText().length() > maxCharacters) event.consume();
            });
            username.setLayoutX((width-170)/2);
            username.setLayoutY(120);

            Text text_u_name = new Text("Username");
            text_u_name.setFont(Font.font(12));
            text_u_name.setFill(Color.BLACK);
            text_u_name.setLayoutX((width-170)/2);
            text_u_name.setLayoutY(110);

            PasswordField password = new PasswordField();
            password.setOnKeyTyped(event ->{
                int maxCharacters = max_password_chars;
                if(password.getText().length() > maxCharacters) event.consume();
            });
            password.setLayoutX((width-170)/2);
            password.setLayoutY(height-200);
            Text text_pwd = new Text("Password");
            text_pwd.setFont(Font.font(12));
            text_pwd.setFill(Color.BLACK);
            text_pwd.setLayoutX((width-170)/2);
            text_pwd.setLayoutY(190);

            Button login = new Button("Login");
            login.setLayoutX((width-55)/2);
            login.setLayoutY(height-100);

            Group signin_group = new Group(background, username, password, text_u_name, text_pwd, login);

            Tab signin = new Tab();
            signin.setText("SIGNIN");
            signin.setContent(signin_group);
        //endregion
        //region<SIGNUP>
            background = new Rectangle(width,height, background_signup_color);

            TextField new_username = new TextField();
            new_username.setOnKeyTyped(event ->{
                int maxCharacters = max_username_chars;
                if(new_username.getText().length() > maxCharacters) event.consume();
                try {
                    if(new_username.getText().length()>0 && h!=null){
                        if(client.checkUsername(h, new_username.getText())){
                            new_username.setStyle("-fx-text-fill: green;");
                        }else{
                            new_username.setStyle("-fx-text-fill: red;");
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            new_username.setLayoutX((width-170)/2);
            new_username.setLayoutY(height-310);
            Text text_new_u = new Text("Username");
            text_new_u.setFont(Font.font(12));
            text_new_u.setFill(Color.BLACK);
            text_new_u.setLayoutX((width-170)/2);
            text_new_u.setLayoutY(height-320);

            PasswordField password1 = new PasswordField();
            password1.setOnKeyTyped(event ->{
                int maxCharacters = max_password_chars;
                if(password1.getText().length() > maxCharacters) event.consume();
            });
            password1.setLayoutX((width-170)/2);
            password1.setLayoutY(height-240);
            Text text_pwd1 = new Text("Password");
            text_pwd1.setFont(Font.font(12));
            text_pwd1.setFill(Color.BLACK);
            text_pwd1.setLayoutX((width-170)/2);
            text_pwd1.setLayoutY(height-250);

            PasswordField password2 = new PasswordField();
            password2.setOnKeyTyped(event ->{
                int maxCharacters = max_password_chars;
                if(password2.getText().length() > maxCharacters) event.consume();
            });
            password2.setLayoutX((width-170)/2);
            password2.setLayoutY(height-170);
            Text text_pwd2 = new Text("Repeat password");
            text_pwd2.setFont(Font.font(12));
            text_pwd2.setFill(Color.BLACK);
            text_pwd2.setLayoutX((width-170)/2);
            text_pwd2.setLayoutY(height-180);

            Button register = new Button("Register and login");
            register.setLayoutX((width-130)/2);
            register.setLayoutY(height-100);

            Group signup_group = new Group(background,new_username, password1, password2, text_new_u, text_pwd1, text_pwd2, register);

            Tab signup = new Tab();
            signup.setText("SIGNUP");
            signup.setContent(signup_group);
        //endregion
        tabPane.getTabs().addAll(signin, signup);
        Group root2 = new Group(tabPane);
        Scene scene2 = new Scene(root2, width, height);
        //endregion

        //region<MAIN>
        //region<User_Files>
        //endregion
        //region<Upload_Files>
        //endregion
        //region<Subscribe_Topics>
        //endregion
        //region<Search>
        //endregion
        //region<Change_PWD>
        //endregion
        //endregion

        stage.setScene(scene1);
        stage.show();

        //region<BUTTONS_Actions>
        connect.setOnAction(action -> {
            client.clientIP = (clientIP.getText());
            client.clientPORT = (clientPORT.getText());
            client.serverIP = (serverIP.getText());
            client.serverPORT = (serverPORT.getText());
            //COMPROVAR CONNECTIVITAT
            stage.setScene(scene2);
        });

        login.setOnAction(action -> {
            try {
                if(client.logIn(h, username.getText(), password.getText())){
                    //NEXT SCENE
                }else{
                    //BORRAR CAMPS I MOSTRAR ERROR
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        register.setOnAction(action -> {

        });
        //endregion


/*
        // Setting the text
        Text text = new Text(
                "Type any letter to rotate the box, and click on the box to stop the rotation");

        // Setting the font of the text
        text.setFont(Font.font(null, FontWeight.BOLD, 15));

        // Setting the color of the text
        text.setFill(Color.CRIMSON);

        // Setting the position of the text
        text.setX(20);
        text.setY(50);

        // Setting the material of the box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.DARKSLATEBLUE);

        // Setting the diffuse color material to box
        box.setMaterial(material);

        // Setting the rotation animation to the box
        RotateTransition rotateTransition = new RotateTransition();

        // Setting the duration for the transition
        rotateTransition.setDuration(Duration.millis(1000));

        // Setting the node for the transition
        rotateTransition.setNode(box);

        // Setting the axis of the rotation
        rotateTransition.setAxis(Rotate.Y_AXIS);

        // Setting the angle of the rotation
        rotateTransition.setByAngle(360);

        // Setting the cycle count for the transition
        rotateTransition.setCycleCount(50);

        // Setting auto reverse value to false
        rotateTransition.setAutoReverse(false);

        // Creating a text filed
        TextField textField = new TextField();

        // Setting the position of the text field
        textField.setLayoutX(50);
        textField.setLayoutY(100);

        // Handling the key typed event
        EventHandler<KeyEvent> eventHandlerTextField = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // Playing the animation
                rotateTransition.play();
            }
        };

        // Adding an event handler to the text feld
        textField.addEventHandler(KeyEvent.KEY_TYPED, eventHandlerTextField);

        // Handling the mouse clicked event(on box)
        EventHandler<javafx.scene.input.MouseEvent> eventHandlerBox =
                new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent e) {
                        rotateTransition.stop();
                    }
                };

        // Adding the event handler to the box
        box.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandlerBox);

        // Creating a Group object
        Group root = new Group(box, textField, text);

        // Creating a scene object
        Scene scene = new Scene(root, 600, 300);

        // Setting camera
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(0);
        scene.setCamera(camera);

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();*/
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void animation(){ launch();}
}
