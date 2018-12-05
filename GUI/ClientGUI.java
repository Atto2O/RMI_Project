package GUI;

import Objects.FileObject;
import Objects.Type;
import RemoteObject.Garage;
import javafx.application.Application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import Client.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientGUI extends Application {
    public static Client client;
    public static Garage h;

    private String client_current_username;

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
        int max_tag_chars = 15;
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
                /*if(this.checkUsername(new_username.getText())){
                    new_username.setStyle("-fx-text-fill: green;");
                }
                else if(new_username.getText().length()>0){
                    new_username.setStyle("-fx-text-fill: red;");
                }*/
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

        Text title_mainFiles = new Text("My files:");
        title_mainFiles.setFont(Font.font(null, FontWeight.BOLD, 14));
        title_mainFiles.setFill(Color.BLACK);
        title_mainFiles.setLayoutX(20);
        title_mainFiles.setLayoutY(25);

        Group mainFiles_group;

        if(this.getUserFiles().size()==0||this.getUserFiles()==null) {
            Text no_mainFiles = new Text("You don't upload files yet.");
            no_mainFiles.setFont(Font.font(null, FontPosture.ITALIC, 10));
            no_mainFiles.setFill(Color.BLACK);
            no_mainFiles.setLayoutX((width - 140) / 2);
            no_mainFiles.setLayoutY(110);

            mainFiles_group = new Group(no_mainFiles);
        }else{
            TableView userFiles_table = new TableView();
            userFiles_table.setEditable(false);

            TableColumn id_column = new TableColumn("ID");
            id_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("id"));
            TableColumn name_column = new TableColumn("Name");
            name_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("user"));
            TableColumn type_column = new TableColumn("Type");
            type_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("type"));
            TableColumn state_column = new TableColumn("Public");
            state_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("state"));
            TableColumn tags_column = new TableColumn("Tags");
            tags_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("tags"));
            TableColumn description_column = new TableColumn("Description");
            description_column.setCellValueFactory(new PropertyValueFactory<FileObject, String>("description"));
            TableColumn edit_column = new TableColumn("Edit");//edit, download, delete
            edit_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
                @Override public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                    return new EditFile_fromTable(stage, userFiles_table);
                }
            });
            TableColumn download_column = new TableColumn("Download");//edit, download, delete
            download_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
                @Override public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                    return new DownloadFile_fromTable(stage, userFiles_table);
                }
            });
            TableColumn delete_column = new TableColumn("Delete");//edit, download, delete
            delete_column.setCellFactory(new Callback<TableColumn<FileObject, Boolean>, TableCell<FileObject, Boolean>>() {
                @Override public TableCell<FileObject, Boolean> call(TableColumn<FileObject, Boolean> personBooleanTableColumn) {
                    return new DeleteFile_fromTable(stage, userFiles_table);
                }
            });

            ObservableList<FileObject> observableFilesList = FXCollections.observableList(this.getUserFiles());
            userFiles_table.setItems(observableFilesList);
            userFiles_table.getColumns().addAll(id_column, name_column, type_column, state_column, tags_column, description_column, edit_column, download_column, delete_column);
            userFiles_table.setMaxHeight(230);
            userFiles_table.setMaxWidth(width-100);
            userFiles_table.setLayoutX(50);
            userFiles_table.setLayoutY(40);

            mainFiles_group = new Group(userFiles_table);
        }

        Group users = new Group (background, title_mainFiles, mainFiles_group);
        Tab user_files_tab = new Tab();
        user_files_tab.setText("MY FILES");
        user_files_tab.setContent(users);
        //endregion
        //region<Upload_Files>
        background = new Rectangle(width,height, background_upload_color);
        final FileChooser fileChooser = new FileChooser();
        TextField upload_path_file = new TextField("Enter a file path");

        Button browse_button = new Button("Browse");

        TextField upload_file_name = new TextField();


        ObservableList<String> upload_type_options =
                FXCollections.observableArrayList(
                        "MOVIE",
                        "IMAGE",
                        "TEXT",
                        "PDF",
                        "AUDIO"
                );
        final ComboBox upload_types = new ComboBox(upload_type_options);

        Button save_upload_button = new Button("Save");

        Group upload = new Group (background, upload_path_file, browse_button, upload_file_name, upload_types, save_upload_button);
        Tab upload_files_tab = new Tab();
        upload_files_tab.setText("UPLOAD FILES");
        upload_files_tab.setContent(upload);
        //endregion
        //region<Subscribe_Topics>
        background = new Rectangle(width,height, background_subscribe_color);
        Text title_subscribe = new Text("My subscribed tags:");
        title_subscribe.setFont(Font.font(null, FontWeight.BOLD, 14));
        title_subscribe.setFill(Color.BLACK);
        title_subscribe.setLayoutX(20);
        title_subscribe.setLayoutY(25);

        Group subscription_group;

        if(this.getSubscriptions().size()==0){
            //MISSATGE NO SUBSCRIPCIONS
            Text no_subscribed_tags = new Text("You are not subscribed to any tags yet.");
            no_subscribed_tags.setFont(Font.font(null, FontPosture.ITALIC, 10));
            no_subscribed_tags.setFill(Color.BLACK);
            no_subscribed_tags.setLayoutX((width-180)/2);
            no_subscribed_tags.setLayoutY(110);

            subscription_group = new Group(no_subscribed_tags);
        }else{
            BorderPane layout = new BorderPane();
            List<HBoxCell> subscribed_items = new ArrayList<>();
            for (String tag:this.getSubscriptions()) {
                Button delete_tag = new Button();
                delete_tag.setGraphic(this.buildImage("./GUI/Graphics/delete.png"));
                delete_tag.setStyle("-fx-border-color: #000000; -fx-border-width: 5px;");
                delete_tag.setStyle("-fx-background-color: #ffbbbb");
                //RED BUTTON WITH IMAGE
                subscribed_items.add(new HBoxCell(tag, delete_tag, this.client));
                }

                ListView<HBoxCell> listView = new ListView<HBoxCell>();
                ObservableList<HBoxCell> observableSubscriptionList = FXCollections.observableList(subscribed_items);
                listView.setItems(observableSubscriptionList);
                listView.setMaxHeight(230);
                //listView.setPrefHeight(230);

                layout.setCenter(listView);
                layout.setLayoutX(50);
                layout.setLayoutY(40);


            subscription_group = new Group(layout);
        }
        TextField addTag_field = new TextField();
        addTag_field.setOnKeyTyped(event ->{
            int maxCharacters = max_tag_chars;
            if(addTag_field.getText().length() > maxCharacters) event.consume();
        });
        addTag_field.setLayoutX(width-220);
        addTag_field.setLayoutY(240);
        Button add_button = new Button();
        add_button.setGraphic(this.buildImage("./GUI/Graphics/add.png"));
        add_button.setLayoutX(width-50);
        add_button.setLayoutY(240);

        Group subscribe = new Group (background, title_subscribe, subscription_group, addTag_field, add_button);
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
                }else{
                    //ERROR
                }
                serverIP.setText("");
                serverPORT.setText("");
            }
        });

        login.setOnAction(action -> {
            if(this.login(username.getText(), password.getText())){
                stage.setScene(main);
            }
            else{
                //ERROR
            }
            username.setText("");
            password.setText("");
        });

        register.setOnAction(action -> {
            if(this.register(new_username.getText(), password1.getText(), password2.getText())){
                stage.setScene(main);
            }
            else{
                //ERROR + BUIDAR CAMPS REGISTRAR
            }
            new_username.setText("");
            password1.setText("");
            password2.setText("");
        });

        change_pwd_button.setOnAction(action -> {
            if(this.changePWD(current_password.getText(), new_password1.getText(), new_password2.getText())){
                //SUCCES
            }else{
                //ERROR
            }
            current_password.setText("");
            new_password1.setText("");
            new_password2.setText("");
        });

        change_user_button.setOnAction(action -> {
            stage.setScene(scene2);
            this.changeUser();
        });

        disconnect_button.setOnAction(action -> {
            this.exit();
        });

        add_button.setOnAction(action -> {
            if(addTag_field.getText().length()>0){
                this.addTag(addTag_field.getText());
            }
        });

        browse_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String path = "";
                upload_path_file.setText(path);
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    path = file.getAbsolutePath();
                    upload_path_file.setText(path);

                }
            }
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

    //region<Table-buttons>
    private class DeleteFile_fromTable extends TableCell<FileObject, Boolean> {

        final Button deleteFile;
        {
            deleteFile = new Button();
            deleteFile.setGraphic(buildImage("./GUI/Graphics/delete.png"));
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
                    //remove this file from server
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
            download = new Button();
            download.setGraphic(buildImage("./GUI/Graphics/download.png"));
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
                    String path;
                    File dir = directoryChooser.showDialog(stage);
                    if (dir != null) {
                        path = dir.getAbsolutePath();
                    } else {
                        path = "";
                    }
                    //download this file from server && select download path
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
           edit = new Button();
           edit.setGraphic(buildImage("./GUI/Graphics/edit.png"));
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

                    //open edit window from this file;
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
        Button button = new Button();

        HBoxCell(String labelText, Button button, Client client) {
            super();

            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            this.getChildren().addAll(label, button);

            button.setOnAction(action -> {
                this.deleteTag(label.getText());
            });
        }

        public void deleteTag(String tag){
            ///client.deleteTag(tag);
        }
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

        this.client.deleteCallbackFromClienth(h);
        System.exit(0);
    }

    public void changeUser(){
        this.client.deslogear(h);
    }

    public boolean changePWD(String oldPassword, String newPassword1, String newPassword2){
        if(newPassword1.equals(newPassword2)){
            return (this.client.changePassword(h,oldPassword,newPassword1));
        }
        return false;
    }

    public ArrayList<FileObject> getUserFiles (){
        ArrayList<FileObject> f = new ArrayList<>();
        ArrayList<String> a = new ArrayList<>();
        a.add("1tag");
        a.add("2tag");
        f.add(new FileObject(a, "this File name", Type.IMAGE, null, true, "admin", "this is my custom description"));
        f.add(new FileObject(null, "sdfasdfasdfasdf", Type.PDF, null, false, "adaff", "tsdfkjdfs hdsafh asdf hodsf a flkjfeaslfen"));
        return f;
    }

    public ArrayList<String> getSubscriptions(){
        //this.client_current_username;
        ArrayList<String> a = new ArrayList<>();
        a.add("añsldkf");
        a.add("asdfasd");
        a.add("uiluik");
        a.add("uiktywefrger");
        a.add("hngguweo");
        return a;
    }

    public void addTag(String tag){

    }
}

