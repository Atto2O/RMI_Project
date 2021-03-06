package GUI;

import Objects.FileObject;
import Objects.Type;
import javafx.application.Application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import Client.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends Application {

    public static Client client;
    private String client_current_username;

    private Scene connection_scene;
    private Scene login_scene;
    private Scene main_scene;

    //region<INPUT_Limits>
    private final int max_ip_chars = 15;
    private final int max_port_chars = 8;
    private final int max_username_chars = 12;
    private final int min_username_chars = 4;
    private final int max_password_chars = 12;
    private final int min_password_chars = 4;
    private final int max_tag_chars = 15;
    private final int max_description_chars = 100;
    //endregion

    private final int width = 700;
    private final int height = 400;

    private int panels_height = height-90;

    public static ObservableList<String> observableSubscriptions;
    public static ObservableList<HBoxCell> observableSubscriptionList;
    public static ObservableList<HBoxCell> tags_list;

    public static ObservableList<HBoxCell> observableNotificationList;

    public static ObservableList<FileObject> observableUserFiles = FXCollections.observableList(new ArrayList<>());

    public static File upload_file;

    public static ObservableList<FileObject> observableSearchList = FXCollections.observableList(new ArrayList<>());
    private boolean searched = false;

    public static FileObject editing_fileobject;
    public static boolean editing = false;
    public static ObservableList<HBoxCell> edit_tags;

    //region<COLORS>
    private final Color background_start_color = Color.LIGHTSTEELBLUE;
    private final Color background_signin_color = Color.LIGHTSTEELBLUE;
    private final Color background_signup_color = Color.LIGHTSTEELBLUE;
    private final Color background_upload_color = Color.LIGHTSTEELBLUE;
    private final Color background_userFiles_color = Color.LIGHTSTEELBLUE;
    private final Color background_subscribe_color = Color.LIGHTSTEELBLUE;
    private final Color background_search_color = Color.LIGHTSTEELBLUE;
    private final Color background_changePWD_color = Color.LIGHTSTEELBLUE;
    private final Color background_disconnect_color = Color.LIGHTSTEELBLUE;
    private final Color notification_box_color = Color.WHITE;
    //endregion

    private TableView userFiles_Table;

    //region<IP/Port>
    private Scene setConnectionScene(Stage stage){
        Rectangle background = new Rectangle(width,height, background_start_color);



        Label text_ServerIp = new Label("Ip:");
        text_ServerIp.setLayoutX((width-170)/2);
        text_ServerIp.setLayoutY((height-170)-180);


        TextField serverIP = new TextField();
        serverIP.setOnKeyTyped(event ->{
            int maxCharacters = max_ip_chars;
            if(serverIP.getText().length() > maxCharacters) event.consume();
        });

        serverIP.setLayoutX((width-170)/2);
        serverIP.setLayoutY((height-170)-150);

        Label text_ServerPort = new Label("Port:");
        text_ServerPort.setLayoutX((width-170)/2);
        text_ServerPort.setLayoutY((height-170)-80);

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
        serverPORT.setLayoutY((height-170)-50);
        Button connect = new Button("Connect");
        connect.setLayoutX((width-70)/2);
        connect.setLayoutY(height-70);

        Group root1 = new Group(background, serverIP, serverPORT, connect, text_ServerIp,text_ServerPort);
        Scene scene1 = new Scene(root1, width, height);

        connect.setOnAction(action -> {
            if(/*!serverIP.getText().isEmpty() &&*/ !serverPORT.getText().isEmpty()){
                if(this.connect(serverIP.getText(), serverPORT.getText(),stage)){
                    stage.setScene(setLogin_scene(stage));
                }else{
                    Toast.makeText(stage,  "Unable to connect to the server",false);
                }
                serverIP.setText("");
                serverPORT.setText("");
            }
        });
        return scene1;
    }
    //endregion

    //region<LOGIN/SIGNUP>
    private Scene setLogin_scene(Stage stage) {
        Rectangle background;

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
        Text text_new_u = new Text("Username (min: "+min_username_chars+" max: "+max_username_chars+")");
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
        Text text_pwd1 = new Text("Password (min: "+min_password_chars+" max: "+max_password_chars+")");
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

        login.setOnAction(action -> {
            if(this.login(username.getText(), password.getText(),stage)){
                stage.setTitle("MyTube - "+username.getText().toUpperCase());
                stage.setScene(setMain_scene(stage));
            }
            else{
                //ERROR
            }
            username.setText("");
            password.setText("");
        });

        register.setOnAction(action -> {
            if(this.register(new_username.getText(), password1.getText(), password2.getText(),stage)){
                stage.setTitle("MyTube - "+new_username.getText().toUpperCase());
                stage.setScene(setMain_scene(stage));
            }
            else{
                //ERROR + BUIDAR CAMPS REGISTRAR
            }
            new_username.setText("");
            password1.setText("");
            password2.setText("");
        });

        return scene2;
    }
    //endregion

    //region<MAIN>
    private Scene setMain_scene(Stage stage) {
        Rectangle background;
        TabPane mainMenu = new TabPane();
        mainMenu.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainMenu.setMaxWidth(width);
        mainMenu.setMinWidth(width);
        mainMenu.setMaxHeight(height - 90);
        //region<User_Files>
        background = new Rectangle(width, height, background_userFiles_color);

        Text title_mainFiles = new Text("My files:");
        title_mainFiles.setFont(Font.font(null, FontWeight.BOLD, 14));
        title_mainFiles.setFill(Color.BLACK);
        title_mainFiles.setLayoutX(20);
        title_mainFiles.setLayoutY(25);

        Group mainFiles_group;
        if(editing){

            mainFiles_group = new Group(this.createEditView(stage, ClientGUI.editing_fileobject));

        }else{
            ClientGUI.observableUserFiles = FXCollections.observableList(ClientGUI.getUserFiles());
            this.userFiles_Table = this.createUsersTable(ClientGUI.observableUserFiles, stage);
            mainFiles_group= new Group(this.userFiles_Table);
        }

        Group users = new Group(background, title_mainFiles, mainFiles_group);
        Tab user_files_tab = new Tab();
        user_files_tab.setText("MY FILES");
        user_files_tab.setContent(users);
        //endregion
        //region<Upload_Files>
        background = new Rectangle(width, height, background_upload_color);
        final FileChooser fileChooser = new FileChooser();

        Label text_browse = new Label("Enter a file path:");
        TextField upload_path_file = new TextField("Enter a file path");
        upload_path_file.setDisable(true);
        Button browse_button = new Button("Browse");
        text_browse.setLayoutX(40);
        text_browse.setLayoutY(20);
        upload_path_file.setLayoutX(40);
        upload_path_file.setLayoutY(40);
        browse_button.setLayoutX(210);
        browse_button.setLayoutY(40);

        Label text_filename = new Label("Enter a file name:");
        TextField upload_file_name = new TextField();
        text_filename.setLayoutX(40);
        text_filename.setLayoutY(80);
        upload_file_name.setLayoutX(40);
        upload_file_name.setLayoutY(100);

        ObservableList<String> upload_type_options =
                FXCollections.observableArrayList(
                        "MOVIE",
                        "IMAGE",
                        "TEXT",
                        "PDF",
                        "AUDIO"
                );
        final ComboBox upload_types = new ComboBox(upload_type_options);
        Label text_types = new Label("Select a file type:");
        text_types.setLayoutX(40);
        text_types.setLayoutY(140);
        upload_types.setLayoutX(40);
        upload_types.setLayoutY(160);

        ToggleSwitch switch_state_button = new ToggleSwitch();
        Label text_state = new Label("File state:");
        text_state.setLayoutX(40);
        text_state.setLayoutY(200);
        switch_state_button.setLayoutX(40);
        switch_state_button.setLayoutY(220);

        TextArea description_file_upload = new TextArea();
        description_file_upload.setOnKeyTyped(event ->{
            int maxCharacters = max_description_chars;
            if(description_file_upload.getText().length() > maxCharacters) event.consume();
        });
        description_file_upload.setPrefWidth(340);
        description_file_upload.setPrefHeight(90);
        Label text_description = new Label("Enter a description (max " + max_description_chars + " characters):");
        text_description.setLayoutX(340);
        text_description.setLayoutY(20);
        description_file_upload.setLayoutX(340);
        description_file_upload.setLayoutY(40);

        ArrayList<String> tags_upload = new ArrayList<>();

        BorderPane tags_layout = new BorderPane();
        List<HBoxCell> tag_upload_items = new ArrayList<>();
        for (String tag : tags_upload) {
            Button delete_tag_upload = new Button("Delete");
            tag_upload_items.add(new HBoxCell(tag, delete_tag_upload, ClientGUI.client, true, null, stage));
        }

        Label text_tags = new Label("Enter file tags:");
        text_tags.setLayoutX(340);
        text_tags.setLayoutY(140);
        ListView<HBoxCell> tagsView = new ListView<>();
        ClientGUI.tags_list = FXCollections.observableList(tag_upload_items);
        tagsView.setItems(ClientGUI.tags_list);
        tagsView.setMaxHeight(70);
        tagsView.setMinHeight(10);
        tagsView.setMaxWidth(250);

        tags_layout.setCenter(tagsView);
        tags_layout.setLayoutX(340);
        tags_layout.setLayoutY(160);

        TextField addTag_field_upload = new TextField();
        addTag_field_upload.setOnKeyTyped(event -> {
            int maxCharacters = max_tag_chars;
            if (addTag_field_upload.getText().length() > maxCharacters) event.consume();
        });
        addTag_field_upload.setLayoutX(width - 240);
        addTag_field_upload.setLayoutY(240);
        Button add_button_upload = new Button("Add");
        add_button_upload.setLayoutX(width - 70);
        add_button_upload.setLayoutY(240);


        Button save_upload_button = new Button("Save");
        save_upload_button.setLayoutX(320);
        save_upload_button.setLayoutY(height-160);

        Group upload = new Group(background,text_browse, upload_path_file, browse_button, text_filename, upload_file_name,text_types, upload_types, text_state,
                switch_state_button, text_description, description_file_upload, save_upload_button, tags_layout, addTag_field_upload, add_button_upload, text_tags);
        Tab upload_files_tab = new Tab();
        upload_files_tab.setText("UPLOAD FILES");
        upload_files_tab.setContent(upload);
        //endregion
        //region<Subscribe_Topics>
        background = new Rectangle(width, height, background_subscribe_color);
        Text title_subscribe = new Text("My subscribed tags:");
        title_subscribe.setFont(Font.font(null, FontWeight.BOLD, 14));
        title_subscribe.setFill(Color.BLACK);
        title_subscribe.setLayoutX(20);
        title_subscribe.setLayoutY(25);

        Group subscription_group;

        try {
            ClientGUI.observableSubscriptions = FXCollections.observableArrayList(this.getSubscriptions());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        BorderPane layout = new BorderPane();
        List<HBoxCell> subscribed_items = new ArrayList<>();
        for (String tag : ClientGUI.observableSubscriptions) {
            Button delete_tag = new Button("Delete");
            //RED BUTTON WITH IMAGE
            subscribed_items.add(new HBoxCell(tag, delete_tag, ClientGUI.client, false, null, stage));
        }

        ListView<HBoxCell> listView = new ListView<>();
        ClientGUI.observableSubscriptionList = FXCollections.observableList(subscribed_items);
        listView.setItems(ClientGUI.observableSubscriptionList);
        listView.setMaxHeight(230);
        listView.setMinHeight(10);

        layout.setCenter(listView);
        layout.setLayoutX(50);
        layout.setLayoutY(40);
        subscription_group = new Group(layout);

        TextField addTag_field = new TextField();
        addTag_field.setOnKeyTyped(event -> {
            int maxCharacters = max_tag_chars;
            if (addTag_field.getText().length() > maxCharacters) event.consume();
        });
        addTag_field.setLayoutX(width - 240);
        addTag_field.setLayoutY(240);
        Button add_button = new Button("Add");
        add_button.setLayoutX(width - 70);
        add_button.setLayoutY(240);

        Group subscribe = new Group(background, title_subscribe, subscription_group, addTag_field, add_button);
        Tab subcriptions_tab = new Tab();
        subcriptions_tab.setText("SUBSCRIBE");
        subcriptions_tab.setContent(subscribe);
        //endregion
        //region<Search>
        background = new Rectangle(width, height, background_search_color);

        TextField search_field = new TextField();
        search_field.setLayoutX(30);
        search_field.setLayoutY(10);
        Button search_button = new Button("Search");
        search_button.setLayoutX(200);
        search_button.setLayoutY(10);

        Group search_group = new Group();
        if(this.searched && (ClientGUI.observableSearchList != null||!ClientGUI.observableSearchList.isEmpty())){
            search_group = new Group(this.createSearchTable(ClientGUI.observableSearchList, stage));
        }

        Group search = new Group(background, search_field, search_button, search_group);
        Tab search_tab = new Tab();
        search_tab.setText("SEARCH");
        search_tab.setContent(search);
        //endregion
        //region<Change_PWD>
        background = new Rectangle(width, height, background_changePWD_color);

        PasswordField current_password = new PasswordField();
        current_password.setOnKeyTyped(event -> {
            int maxCharacters = max_password_chars;
            if (current_password.getText().length() > maxCharacters) event.consume();
        });
        current_password.setLayoutX((width - 170) / 2);
        current_password.setLayoutY(50);
        Text text_current_password = new Text("Current password");
        text_current_password.setFont(Font.font(12));
        text_current_password.setFill(Color.BLACK);
        text_current_password.setLayoutX((width - 170) / 2);
        text_current_password.setLayoutY(40);

        PasswordField new_password1 = new PasswordField();
        new_password1.setOnKeyTyped(event -> {
            int maxCharacters = max_password_chars;
            if (new_password1.getText().length() > maxCharacters) event.consume();
        });
        new_password1.setLayoutX((width - 170) / 2);
        new_password1.setLayoutY(110);
        Text text_new_pwd1 = new Text("New password");
        text_new_pwd1.setFont(Font.font(12));
        text_new_pwd1.setFill(Color.BLACK);
        text_new_pwd1.setLayoutX((width - 170) / 2);
        text_new_pwd1.setLayoutY(100);

        PasswordField new_password2 = new PasswordField();
        new_password2.setOnKeyTyped(event -> {
            int maxCharacters = max_password_chars;
            if (new_password2.getText().length() > maxCharacters) event.consume();
        });
        new_password2.setLayoutX((width - 170) / 2);
        new_password2.setLayoutY(170);
        Text text_new_pwd2 = new Text("Repeat new password");
        text_new_pwd2.setFont(Font.font(12));
        text_new_pwd2.setFill(Color.BLACK);
        text_new_pwd2.setLayoutX((width - 170) / 2);
        text_new_pwd2.setLayoutY(160);

        Button change_pwd_button = new Button("Save");
        change_pwd_button.setLayoutX((width - 50) / 2);
        change_pwd_button.setLayoutY(220);

        Group changePWD = new Group(background, current_password, text_current_password, new_password1, text_new_pwd1, new_password2, text_new_pwd2, change_pwd_button);
        Tab changePWD_tab = new Tab();
        changePWD_tab.setText("CHANGE PASSWORD");
        changePWD_tab.setContent(changePWD);
        //endregion
        //region<DISCONNECT>
        background = new Rectangle(width, height, background_disconnect_color);

        Button change_user_button = new Button("Change user");
        change_user_button.setLayoutX((width - 90) / 2);
        change_user_button.setLayoutY(80);
       // change_user_button.setDisable(true);
        Button disconnect_button = new Button("Disconnect");
        disconnect_button.setLayoutX((width - 80) / 2);
        disconnect_button.setLayoutY(170);

        Group disconnect = new Group(background, change_user_button, disconnect_button);
        Tab disconnect_tab = new Tab("Log Out");
        disconnect_tab.setContent(disconnect);
        //endregion

        mainMenu.setTabMaxWidth(width - 40 / 6);
        mainMenu.setTabMinWidth(92);
        mainMenu.getTabs().addAll(user_files_tab, upload_files_tab, subcriptions_tab, search_tab, changePWD_tab, disconnect_tab);

        if(this.searched){
            mainMenu.getSelectionModel().select(3);
            this.searched = false;
        }

        //region<NOTIFICATIONS>
        Rectangle notificationBox = new Rectangle(width, height, notification_box_color);
        notificationBox.setWidth(width);
        notificationBox.setHeight(90);
        notificationBox.setY(height - 90);

        BorderPane notifications_layout = new BorderPane();
        notifications_layout.setPrefWidth(width-20);
        notifications_layout.setPrefHeight(70);

        List<HBoxCell> notification_items = new ArrayList<>();
        ListView<HBoxCell> notification_listView = new ListView<>();
        ClientGUI.observableNotificationList = FXCollections.observableList(notification_items);

        notification_listView.setItems(ClientGUI.observableNotificationList);
        notification_listView.setPrefHeight(70);
        notification_listView.setPrefWidth(width-20);
        notification_listView.setStyle("-fx-background-insets: 0;");

        notifications_layout.setCenter(notification_listView);
        notifications_layout.setLayoutX(10);
        notifications_layout.setLayoutY(height-80);
        Group not = new Group(notifications_layout);

        Group notifications_group = new Group(notificationBox, not);
        //endregion

        Group main_root = new Group(mainMenu, notifications_group);
        Scene main = new Scene(main_root, width, height);

        search_button.setOnAction(action -> {
            ClientGUI.observableSearchList = FXCollections.observableList(new ArrayList<>());
            if(search_field.getText().length()>0){
                if(this.search(search_field.getText(), stage)){
                    this.searched = true;
                    stage.setScene(setMain_scene(stage));
                }
            }
            search_field.clear();
        });

        change_pwd_button.setOnAction(action -> {
            if (this.changePWD(current_password.getText(), new_password1.getText(), new_password2.getText(),stage)) {
                //SUCCES
            } else {
                //ERROR
            }
            current_password.setText("");
            new_password1.setText("");
            new_password2.setText("");
        });

        change_user_button.setOnAction(action -> {
            this.changeUser();
            stage.setTitle("MyTube");
            stage.setScene(setLogin_scene(stage));
        });

        disconnect_button.setOnAction(action -> {
            this.exit();
        });

        add_button.setOnAction(action -> {
            if (addTag_field.getText().length() > 0) {
                Button delete_tag = new Button("Delete");
                this.addTag(addTag_field.getText().toLowerCase(),stage);
                ClientGUI.observableSubscriptionList.add(new HBoxCell(addTag_field.getText().toLowerCase(), delete_tag, ClientGUI.client, false, null,  stage));
                addTag_field.clear();
            }
        });

        add_button_upload.setOnAction(action -> {
            if (addTag_field_upload.getText().length() > 0) {
                Button delete_tag_upload = new Button("Delete");
                ClientGUI.tags_list.add(new HBoxCell(addTag_field_upload.getText().toLowerCase(), delete_tag_upload, ClientGUI.client, true, null, stage));
                addTag_field_upload.clear();
            }
        });

        browse_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = "";
                upload_path_file.setText(path);
                ClientGUI.upload_file = fileChooser.showOpenDialog(stage);
                if (ClientGUI.upload_file != null) {
                    path = ClientGUI.upload_file.getAbsolutePath();
                    upload_path_file.setText(path);
                }
            }
        });

        save_upload_button.setOnAction(action -> {
            String path = ClientGUI.upload_file.getAbsolutePath();
            if (path.isEmpty()){
                //AVIS ENTRAR PATH
                Toast.makeText(stage,  "Path is empty",false);
            }else{
                String filename = upload_file_name.getText();
                if(filename.isEmpty()){
                    //AVIS ENTRAR NAME
                    Toast.makeText(stage,"File name is empty",false);
                }else{
                    if(upload_types.getSelectionModel().isEmpty()){
                        //AVIS ENTRAR TYPE
                        Toast.makeText(stage,  "Type is empty",false);
                    }else{
                        String type = upload_types.getSelectionModel().getSelectedItem().toString();
                        System.out.println(type);
                        SimpleBooleanProperty state = switch_state_button.switchOnProperty();
                        String description = description_file_upload.getText();
                        if(description.isEmpty()){
                            //AVIS ENTRAR DESCRIPCIO
                            Toast.makeText(stage,  "Description is empty",false);
                        }else{
                            ArrayList<String> tags = new ArrayList<>();
                            for (HBoxCell tag:ClientGUI.tags_list) {
                                String t = tag.label.getText();
                                if(!tags.contains(t)){
                                    tags.add(tag.label.getText());
                                }
                            }
                            if(tags.isEmpty()){
                                Toast.makeText(stage,  "Tag list is empty",false);
                            }else{
                                this.upload(filename, type, description, tags, state.getValue(),stage);
                                stage.setScene(setMain_scene(stage));
                            }
                        }
                    }
                }
            }
        });

        return main;
    }
    //endregion

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("MyTube");
        stage.setResizable(false);

        stage.setScene(setConnectionScene(stage));
        stage.show();
    }

    @Override
    public void stop() {
        this.exit();
    }

    public static void animation(){
        launch();
    }

    private Group createEditView(Stage stage, FileObject fileObject){
        Label text_change_filename = new Label("File name:");
        TextField change_file_name = new TextField(fileObject.getFileName());
        text_change_filename.setLayoutX(40);
        text_change_filename.setLayoutY(80);
        change_file_name.setLayoutX(40);
        change_file_name.setLayoutY(100);

        ObservableList<String> change_type_options =
                FXCollections.observableArrayList(
                        "MOVIE",
                        "IMAGE",
                        "TEXT",
                        "PDF",
                        "AUDIO"
                );
        final ComboBox edit_types = new ComboBox(change_type_options);
        edit_types.getSelectionModel().select(fileObject.getType().toString());

        Label edit_file_types = new Label("File type:");
        edit_file_types.setLayoutX(40);
        edit_file_types.setLayoutY(140);
        edit_types.setLayoutX(40);
        edit_types.setLayoutY(160);

        ToggleSwitch edit_switch_state_button = new ToggleSwitch();
        Label edit_text_state = new Label("File state:");
        edit_text_state.setLayoutX(40);
        edit_text_state.setLayoutY(200);
        edit_switch_state_button.setLayoutX(40);
        edit_switch_state_button.setLayoutY(220);

        TextArea description_file_edit = new TextArea(fileObject.getDescription());
        description_file_edit.setOnKeyTyped(event ->{
            int maxCharacters = max_description_chars;
            if(description_file_edit.getText().length() > maxCharacters) event.consume();
        });
        description_file_edit.setPrefWidth(340);
        description_file_edit.setPrefHeight(90);
        Label edit_text_description = new Label("Enter a description (max " + max_description_chars + " characters):");
        edit_text_description.setLayoutX(340);
        edit_text_description.setLayoutY(20);
        description_file_edit.setLayoutX(340);
        description_file_edit.setLayoutY(40);

        ArrayList<String> tags_edit = fileObject.getTags();

        BorderPane tags_layout_edit = new BorderPane();
        List<HBoxCell> tag_edit_items = new ArrayList<>();
        for (String tag : tags_edit) {
            Button delete_tag_upload = new Button("Delete");
            tag_edit_items.add(new HBoxCell(tag, delete_tag_upload, ClientGUI.client, true, "hola", stage));
        }

        Label edit_text_tags = new Label("File tags:");
        edit_text_tags.setLayoutX(340);
        edit_text_tags.setLayoutY(140);

        ListView<HBoxCell> edit_tagsView = new ListView<>();
        ClientGUI.edit_tags = FXCollections.observableList(tag_edit_items);

        edit_tagsView.setItems(ClientGUI.edit_tags);
        edit_tagsView.setMaxHeight(70);
        edit_tagsView.setMinHeight(10);
        edit_tagsView.setMaxWidth(250);

        tags_layout_edit.setCenter(edit_tagsView);
        tags_layout_edit.setLayoutX(340);
        tags_layout_edit.setLayoutY(160);

        TextField addTag_field_edit = new TextField();
        addTag_field_edit.setOnKeyTyped(event -> {
            int maxCharacters = max_tag_chars;
            if (addTag_field_edit.getText().length() > maxCharacters) event.consume();
        });
        addTag_field_edit.setLayoutX(width - 240);
        addTag_field_edit.setLayoutY(240);
        Button add_button_edit = new Button("Add");
        add_button_edit.setLayoutX(width - 70);
        add_button_edit.setLayoutY(240);


        Button save_edit_button = new Button("Save");
        save_edit_button.setLayoutX(320);
        save_edit_button.setLayoutY(height-160);

        Group edit = new Group(change_file_name, text_change_filename, edit_types, edit_file_types,
                edit_text_state, edit_switch_state_button, edit_text_description, description_file_edit, save_edit_button,
                tags_layout_edit, addTag_field_edit, add_button_edit, edit_text_tags);

        add_button_edit.setOnAction(action -> {
            if (addTag_field_edit.getText().length() > 0) {
                Button delete_tag = new Button("Delete");
                ClientGUI.edit_tags.add(new HBoxCell(addTag_field_edit.getText(), delete_tag, ClientGUI.client,false, "hola", stage));
                addTag_field_edit.clear();
            }
        });

        save_edit_button.setOnAction(action -> {

            String filename = change_file_name.getText();
            if(filename.isEmpty()){
                //AVIS ENTRAR NAME
                Toast.makeText(stage,"File name is empty",false);
            }else{
                if(edit_types.getSelectionModel().isEmpty()){
                    //AVIS ENTRAR TYPE
                    Toast.makeText(stage,"Type is empty",false);
                }else{
                    String type = edit_types.getSelectionModel().getSelectedItem().toString();
                    SimpleBooleanProperty state = edit_switch_state_button.switchOnProperty();
                    String description = description_file_edit.getText();
                    if(description.isEmpty()){
                        //AVIS ENTRAR DESCRIPCIO
                        Toast.makeText(stage,"Description is empty",false);
                    }else{
                        ArrayList<String> new_tags = new ArrayList<>();
                        for (HBoxCell tag:tag_edit_items) {
                            String t = tag.label.getText();
                            if(!new_tags.contains(t)){
                                new_tags.add(t);
                            }
                        }
                        if(new_tags.isEmpty()){
                            Toast.makeText(stage,  "Tag list is empty",false);
                        }else{
                            fileObject.setFileName(filename);
                            fileObject.setType(Type.fromString(type));
                            fileObject.setState(state.getValue());
                            fileObject.setDescription(description);
                            fileObject.setTags(new_tags);
                            ClientGUI.modifiedFileObject(fileObject,stage);
                            ClientGUI.editing = false;
                            ClientGUI.setStage(setMain_scene(stage), stage);
                        }
                    }
                }
            }
        });
        return edit;
    }

    private TableView createUsersTable(ObservableList<FileObject> list, Stage stage){
        TableView table = new TableView();
        table.setEditable(false);
        TableColumn id_column = new TableColumn("ID");
        id_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("id"));
        TableColumn name_column = new TableColumn("File name");
        name_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("fileName"));
        TableColumn type_column = new TableColumn("Type");
        type_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("type"));
        TableColumn state_column = new TableColumn("Public");
        state_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("state"));
        TableColumn tags_column = new TableColumn("Tags");
        tags_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("tags"));
        TableColumn description_column = new TableColumn("Description");
        description_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("description"));
        TableColumn edit_column = new TableColumn("Edit");
        edit_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
            @Override
            public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                return new EditFile_fromTable(stage, table);
            }
        });
        TableColumn download_column = new TableColumn("Download");//edit, download, delete
        download_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
            @Override
            public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                return new DownloadFile_fromTable(stage, table);
            }
        });
        TableColumn delete_column = new TableColumn("Delete");//edit, download, delete
        delete_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
            @Override
            public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                return new DeleteFile_fromTable(stage, table);
            }
        });
        ObservableList<FileObject> observableFilesList = FXCollections.observableList(list);
        table.setItems(observableFilesList);
        table.getColumns().addAll(id_column, name_column, type_column, state_column, tags_column, description_column, edit_column, download_column, delete_column);
        table.setMaxHeight(230);
        table.setMaxWidth(width - 100);
        table.setLayoutX(50);
        table.setLayoutY(40);
        return table;
    }

    private TableView createSearchTable(ObservableList<FileObject> list, Stage stage){
        TableView table = new TableView();
        table.setEditable(false);
        TableColumn id_column = new TableColumn("ID");
        id_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("id"));
        TableColumn name_column = new TableColumn("File name");
        name_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("fileName"));
        TableColumn type_column = new TableColumn("Type");
        type_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("type"));
        TableColumn username_column = new TableColumn("Owner");
        username_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("user"));
        TableColumn tags_column = new TableColumn("Tags");
        tags_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("tags"));
        TableColumn description_column = new TableColumn("Description");
        description_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("description"));
        TableColumn download_column = new TableColumn("Download");//edit, download, delete
        download_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
            @Override
            public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                return new DownloadFile_fromTable(stage, table);
            }
        });
        ObservableList<FileObject> observableFilesList = FXCollections.observableList(list);
        table.setItems(observableFilesList);
        table.getColumns().addAll(id_column, username_column, name_column, type_column, tags_column, description_column, download_column);
        table.setMaxHeight(230);
        table.setMaxWidth(width - 100);
        table.setLayoutX(50);
        table.setLayoutY(40);
        return table;
    }

    public static void setStage(Scene scene, Stage stage){
        stage.setScene(scene);
    }

    //region<Table-buttons>
    private class DeleteFile_fromTable extends TableCell<FileObject, Boolean> {

        final Button deleteFile;
        {
            deleteFile = new Button("Delete");
        }

        final StackPane paddedButton = new StackPane();
        DoubleProperty buttonY = new SimpleDoubleProperty();
        DeleteFile_fromTable(final Stage stage, final TableView table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(deleteFile);
            deleteFile.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    buttonY.set(mouseEvent.getScreenY());
                }
            });

            deleteFile.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    table.getSelectionModel().select(getTableRow().getIndex());
                    FileObject file = (FileObject) table.getSelectionModel().getSelectedItem();
                    if(ClientGUI.delete(file, stage)){
                        ClientGUI.observableUserFiles = FXCollections.observableList(ClientGUI.getUserFiles());
                        ClientGUI.setStage(setMain_scene(stage), stage);
                    }
                }
            });
        }
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private class DownloadFile_fromTable extends TableCell<FileObject, Boolean> {
        final Button download;
        {
            download = new Button("Download");
        }
        final StackPane paddedButton = new StackPane();
        DoubleProperty buttonY = new SimpleDoubleProperty();
        DownloadFile_fromTable(final Stage stage, final TableView table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(download);
            download.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    buttonY.set(mouseEvent.getScreenY());
                }
            });
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a Directory for download");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            download.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    table.getSelectionModel().select(getTableRow().getIndex());
                    FileObject file = (FileObject) table.getSelectionModel().getSelectedItem();
                    String path;
                    File dir = directoryChooser.showDialog(stage);
                    if (dir != null) {
                        path = dir.getAbsolutePath();
                    } else {
                        path = "";
                    }
                    ClientGUI.download(file, path, stage);
                }
            });
        }

        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }

    private class EditFile_fromTable extends TableCell<FileObject, Boolean> {
        final Button edit;
        {
           edit = new Button("Edit");
        }
        final StackPane paddedButton = new StackPane();
        DoubleProperty buttonY = new SimpleDoubleProperty();
        EditFile_fromTable(final Stage stage, final TableView table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(edit);
            edit.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    buttonY.set(mouseEvent.getScreenY());
                }
            });
            edit.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    table.getSelectionModel().select(getTableRow().getIndex());
                    FileObject file = (FileObject) table.getSelectionModel().getSelectedItem();
                    ClientGUI.editing_fileobject = file;
                    ClientGUI.editing = true;
                    ClientGUI.setStage(setMain_scene(stage), stage);
                }
            });
        }
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }
    //endregion

    public static class HBoxCell extends HBox {
        Label label = new Label();

        HBoxCell(String labelText, Button button, Client client, boolean upload, String f, Stage stage) {
            super();
            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            this.getChildren().addAll(label, button);

            button.setOnAction(action -> {
                if(upload){
                    this.deleteUploadTag(label.getText(), stage);
                }else{
                    this.deleteTag(label.getText(), stage);
                }if(f != null){
                    ClientGUI.edit_tags.remove(this);
                }
            });
        }

        HBoxCell(String labelText) {
            super();
            System.out.println("Creating table");
            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);
            this.getChildren().add(label);
        }

        public void deleteTag(String tag, Stage stage){
            if(client.desSubscribeToTag(tag)){
                Toast.makeText(stage,  "Tag deleted successful!",true);
            }else{
                Toast.makeText(stage,  "Error deleting tag!",false);
            }
            ClientGUI.observableSubscriptionList.remove(this);
        }

        public void deleteUploadTag(String tag, Stage stage){
            ClientGUI.client.desSubscribeToTag(tag);
            ClientGUI.tags_list.remove(this);
        }
    }

    public boolean connect(String ip, String port, Stage stage){
        client.serverIP = (ip);
        client.serverPORT = (port);
        try{
            if(client.setUpConnections()){
                return true;
            }
        }catch (Exception e){
            Toast.makeText(stage,  "Error connecting to server!",false);
            System.out.println("Error on GUI-connect(): "+e.toString());
        }
        return false;
    }

    public boolean login(String username, String password,Stage stage){
        if(ClientGUI.client.logear(username,password)){
            return true;
        }else{
            Toast.makeText(stage,  "Username and password didn't match!",false);
            return false;
        }
    }

    public boolean register(String username, String password1, String password2, Stage stage){
        if(min_username_chars<=username.length() && username.length() <=max_username_chars){
            if(min_password_chars <= password1.length() && password1.length()<= max_password_chars){
                if(ClientGUI.client.registrar(username, password1, password2)){
                    Toast.makeText(stage,  "Account registered!",true);
                    return true;
                }else{
                    Toast.makeText(stage,  "Error Registering!",false);
                    return false;
                }
            }else{
                Toast.makeText(stage,  "Password must be more than "+min_password_chars+" and less"+max_password_chars+" than characters!",false);
                return false;
            }
        }else{
            Toast.makeText(stage,  "Username must be more than "+min_username_chars+" and less"+max_username_chars+" than characters!",false);
            return false;
        }
    }

    public void exit(){
        ClientGUI.client.deleteCallbackFromClienth();
        System.exit(0);
    }

    public void changeUser(){
        try{
            ClientGUI.client.deslogear();
        }catch(Exception e){
            System.out.println("Error on GUI-changeUser(): "+e.toString());
        }
    }

    public boolean changePWD(String oldPassword, String newPassword1, String newPassword2, Stage stage){
        if(newPassword1.equals(newPassword2)){
            if(ClientGUI.client.changePassword(oldPassword,newPassword1)){
                Toast.makeText(stage,  "Password change successful!",true);
                return true;
            }else{
                Toast.makeText(stage,  "Error Registering!",false);
                return false;
            }
        }
        Toast.makeText(stage,  "Passwords didn't match!",false);
        return false;
    }

    public static ArrayList<FileObject> getUserFiles (){
        ArrayList<FileObject> array = new ArrayList<>();
        try{array=client.getFilesByUser();}catch (Exception e){
            System.out.println("Error on GUI-getUserFiles(): "+e.toString());
        }
        return array;
    }

    public ArrayList<String> getSubscriptions() throws RemoteException {
        ArrayList<String> array = new ArrayList<>();
        try{array=client.getSubscriptionsClient();}catch (Exception e){
            System.out.println("Error on GUI-getSubscriptions(): "+e.toString());
        }
        return array;
    }

    public void addTag(String newtag, Stage stage){
        if(ClientGUI.client.subscribeToTag(newtag)){
            Toast.makeText(stage,  "Tag added successfully!",true);
        }else{
            Toast.makeText(stage,  "Error Adding tag!",false);
        }
    }

    public void upload(String filename, String type, String description, ArrayList<String> tags, boolean state, Stage stage){
        File uploadFile = ClientGUI.upload_file;
        System.out.printf("Filename: "+filename+"\nType: "+type+"\nState: "+ state +"\n Description: "+  description + "\nTags:");
        for (String tag: tags) {
            System.out.printf("\n-" + tag);
        }
        System.out.printf("\n");

        if(ClientGUI.client.upload(uploadFile, filename,  type,  description,  tags,  state)){
           Toast.makeText(stage,  "Upload successfully!",true);
            System.out.printf("Tot correcte\n");
            ClientGUI.observableUserFiles = FXCollections.observableList(ClientGUI.getUserFiles());
        }else{
            Toast.makeText(stage,  "Error Uploading!",false);
            System.out.printf("Hi ha agut algun problema\n");
        }
    }

    //download
    public static void download(FileObject file, String path, Stage stage){
        if(client.download( path, file)){
            Toast.makeText(stage,  "Download successfully!",true);
            System.out.printf("Tot correcte\n");

        }else{
            Toast.makeText(stage,  "Error Downloading!",false);
            System.out.printf("Hi ha hagut algun problema\n");
        }
    }

    //delete
    public static boolean delete(FileObject file, Stage stage){
        if(ClientGUI.client.deleteFile(file)){
            Toast.makeText(stage,  "Deleted successfully!",true);
            System.out.printf("Tot correcte\n");
            ClientGUI.observableUserFiles = FXCollections.observableList(ClientGUI.getUserFiles());
            return true;
        }else{
            Toast.makeText(stage,"Error Deleting!",false);
            System.out.printf("Hi ha hagut algun problema\n");
            return false;
        }
    }

    public static void  addNotification(String message){
        ClientGUI.observableNotificationList.add(new HBoxCell(message));
    }

    public boolean search(String text, Stage stage){
        ArrayList<FileObject> array = client.getFilesByText(text);

        if(array.isEmpty()){
            Toast.makeText(stage,"No matches found!",false);
            return false;
        }else{
            ClientGUI.observableSearchList = FXCollections.observableList(array);
        }
        return true;
    }

    public static void modifiedFileObject(FileObject fileObject,Stage stage){
        if(client.fileModified(fileObject)){
            Toast.makeText(stage,"Edited successfully!",true);

        }else{
            Toast.makeText(stage,"Error editing!",false);
        }
    }
}