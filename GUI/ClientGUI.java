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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

        int panels_height = height-90;

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
        Color background_upload_color = Color.LIGHTSTEELBLUE;
        Color background_userFiles_color = Color.LIGHTSTEELBLUE;
        Color background_subscribe_color = Color.LIGHTSTEELBLUE;
        Color background_search_color = Color.LIGHTSTEELBLUE;
        Color background_changePWD_color = Color.LIGHTSTEELBLUE;
        Color background_disconnect_color = Color.LIGHTSTEELBLUE;

        Color notification_box_color = Color.DARKSLATEBLUE;
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
            signin.setText("SIGN IN");
            signin.setContent(signin_group);
        //endregion
        //region<SIGNUP>
            background = new Rectangle(width,height, background_signup_color);

            TextField new_username = new TextField();
            new_username.setOnKeyTyped(event ->{
                int maxCharacters = max_username_chars;
                if(new_username.getText().length() > maxCharacters) event.consume();
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
            register.setLayoutX((width-140)/2);
            register.setLayoutY(height-100);

            Group signup_group = new Group(background,new_username, password1, password2, text_new_u, text_pwd1, text_pwd2, register);

            Tab signup = new Tab();
            signup.setText("SIGN UP");
            signup.setContent(signup_group);
        //endregion
        tabPane.getTabs().addAll(signin, signup);
        Group root2 = new Group(tabPane);
        Scene scene2 = new Scene(root2, width, height);
        //endregion

        //region<MAIN>
        TabPane mainMenu = new TabPane();
        mainMenu.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainMenu.setMaxWidth(width);
        mainMenu.setMinWidth(width);
        mainMenu.setMaxHeight(height-90);
        //region<User_Files>
        background = new Rectangle(width,height, background_userFiles_color);


        Group users = new Group (background);
        Tab user_files_tab = new Tab();
        user_files_tab.setText("MY FILES");
        user_files_tab.setContent(users);
        //endregion
        //region<Upload_Files>
        background = new Rectangle(width,height, background_upload_color);


        Group upload = new Group (background);
        Tab upload_files_tab = new Tab();
        upload_files_tab.setText("UPLOAD FILES");
        upload_files_tab.setContent(upload);
        //endregion
        //region<Subscribe_Topics>
        background = new Rectangle(width,height, background_subscribe_color);


        Group subscribe = new Group (background);
        Tab subcriptions_tab = new Tab();
        subcriptions_tab.setText("SUBSCRIBE");
        subcriptions_tab.setContent(subscribe);
        //endregion
        //region<Search>
        background = new Rectangle(width,height, background_search_color);


        Group search = new Group (background);
        Tab search_tab = new Tab();
        search_tab.setText("SEARCH");
        search_tab.setContent(search);
        //endregion
        //region<Change_PWD>
        background = new Rectangle(width,height, background_changePWD_color);

        PasswordField current_password = new PasswordField();
        current_password.setOnKeyTyped(event ->{
            int maxCharacters = max_password_chars;
            if(current_password.getText().length() > maxCharacters) event.consume();
        });
        current_password.setLayoutX((width-170)/2);
        current_password.setLayoutY(50);
        Text text_current_password = new Text("Current password");
        text_current_password.setFont(Font.font(12));
        text_current_password.setFill(Color.BLACK);
        text_current_password.setLayoutX((width-170)/2);
        text_current_password.setLayoutY(40);

        PasswordField new_password1 = new PasswordField();
        new_password1.setOnKeyTyped(event ->{
            int maxCharacters = max_password_chars;
            if(new_password1.getText().length() > maxCharacters) event.consume();
        });
        new_password1.setLayoutX((width-170)/2);
        new_password1.setLayoutY(110);
        Text text_new_pwd1 = new Text("New password");
        text_new_pwd1.setFont(Font.font(12));
        text_new_pwd1.setFill(Color.BLACK);
        text_new_pwd1.setLayoutX((width-170)/2);
        text_new_pwd1.setLayoutY(100);

        PasswordField new_password2 = new PasswordField();
        new_password2.setOnKeyTyped(event ->{
            int maxCharacters = max_password_chars;
            if(new_password2.getText().length() > maxCharacters) event.consume();
        });
        new_password2.setLayoutX((width-170)/2);
        new_password2.setLayoutY(170);
        Text text_new_pwd2 = new Text("Repeat new password");
        text_new_pwd2.setFont(Font.font(12));
        text_new_pwd2.setFill(Color.BLACK);
        text_new_pwd2.setLayoutX((width-170)/2);
        text_new_pwd2.setLayoutY(160);

        Button change_pwd_button = new Button("Save");
        change_pwd_button.setLayoutX((width-50)/2);
        change_pwd_button.setLayoutY(220);

        Group changePWD = new Group (background, current_password, text_current_password, new_password1, text_new_pwd1, new_password2, text_new_pwd2, change_pwd_button);
        Tab changePWD_tab = new Tab();
        changePWD_tab.setText("CHANGE PASSWORD");
        changePWD_tab.setContent(changePWD);
        //endregion
        //region<DISCONNECT>
        background = new Rectangle(width,height, background_disconnect_color);

        Button change_user_button = new Button("Change user");
        change_user_button.setLayoutX((width-90)/2);
        change_user_button.setLayoutY(80);
        Button disconnect_button = new Button("Disconnect");
        disconnect_button.setLayoutX((width-80)/2);
        disconnect_button.setLayoutY(170);

        Group disconnect = new Group (background, change_user_button, disconnect_button);
        Tab disconnect_tab = new Tab();
        disconnect_tab.setGraphic(buildImage("./GUI/Graphics/logout.png"));
        disconnect_tab.setContent(disconnect);
        //endregion

        mainMenu.setTabMaxWidth(width-40/6);
        mainMenu.setTabMinWidth(92);
        mainMenu.getTabs().addAll(user_files_tab, upload_files_tab, subcriptions_tab, search_tab, changePWD_tab, disconnect_tab);

        //region<NOTIFICATIONS>
        Rectangle notificationBox = new Rectangle(width,height, notification_box_color);
        notificationBox.setWidth(width);
        notificationBox.setHeight(90);
        notificationBox.setY(height-90);
        Group notifications_group = new Group(notificationBox);
        //endregion

        Group main_root = new Group(mainMenu, notifications_group);
        Scene main = new Scene(main_root, width, height);
        //endregion

        stage.setScene(scene1);
        stage.show();

        //region<BUTTONS_Actions>
        connect.setOnAction(action -> {
            if(/*!serverIP.getText().isEmpty() &&*/ !serverPORT.getText().isEmpty()){
                if(this.connect(serverIP.getText(), serverPORT.getText())){
                    stage.setScene(scene2);
                }
            }

        });

        login.setOnAction(action -> {
            if(this.login(username.getText(), password.getText())){
                stage.setScene(main);
            }
            else{
                //ERROR + BUIDAR CAMPS DE USER I PASSWORD
            }
        });

        register.setOnAction(action -> {
            if(this.register(new_username.getText(), password1.getText(), password2.getText())){
                stage.setScene(main);
            }
            else{
                //ERROR + BUIDAR CAMPS REGISTRAR
            }
        });

        change_pwd_button.setOnAction(action -> {
            this.changePWD(new_password1.getText(), new_password2.getText());
        });

        change_user_button.setOnAction(action -> {
            stage.setScene(scene2);
            this.changeUser();
        });

        disconnect_button.setOnAction(action -> {
            this.exit();
        });
        //endregion
    }

    @Override
    public void stop() {
        this.exit();
    }

    public static void animation(){ launch();}

    private static ImageView buildImage(String imgPatch) {
        Image i = new Image(imgPatch);
        ImageView imageView = new ImageView();
        //You can set width and height
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        imageView.setImage(i);
        return imageView;
    }

    public boolean connect(String ip, String port){
        client.serverIP = (ip);
        client.serverPORT = (port);
        client.setUpConnections();
        if(h==null) return false;
        else return true;
    }

    public boolean login(String username, String password){
        return this.client.logear(this.h,username,password);

    }

    public boolean register(String username, String password1, String password2){
        return this.client.registrar(this.h, username, password1, password2);
    }

    public boolean checkUsername(String new_username){
        try {
            if(new_username.length()>0 && this.h!=null){
                if(this.client.checkUsername(h, new_username)){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void exit(){
        System.exit(0);
    }

    public void changeUser(){
    }

    public void changePWD(String newPassword1, String newPassword2){

    }
}
