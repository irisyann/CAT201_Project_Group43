package sample;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {

    Quote quote = new Quote();
    Util util = new Util();
    Weather weather = new Weather();
    VBox vbox = new VBox();
    ArrayList<CheckBox> todos = new ArrayList<>(); // list to hold collection of checkboxes (1 checkbox for each todo)

    final String daysFolderPath = "C:\\Users\\Ray\\Desktop\\CODING\\GitHub\\CAT201_Diary\\src\\days\\";
    final String mainFolderPath = "C:\\Users\\Ray\\Desktop\\CODING\\GitHub\\CAT201_Diary\\src\\sample\\";
    List<JFXButton> daysList = new ArrayList<>(); // list to hold collection of HBox (1 HBox for each day)

    @FXML
    private AnchorPane pane_all, pane_newday, pane_viewdays, pane_todolist, pane_calendar, pane_home, pane_editday, menu, calendar_calendarholder;

    @FXML
    private JFXButton tab_newday, tab_viewdays, tab_todolist, tab_calendar, tab_home, editday_saveButton;

    @FXML
    private Label home_date, home_username, home_quote, home_weatherText, home_weatherTemp, home_time, editday_date;

    @FXML
    private ImageView home_weatherIcon, home_daynightimage;

    @FXML
    private TextArea newday_textarea;

    @FXML
    private ScrollPane viewdays_scrollpane;

    @FXML
    private VBox todolist_vbox;

    @FXML
    private TextField todolist_todoInput;

    @FXML
    private DatePicker viewdays_datepicker;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        // *************** Home should always be set to visible unless when editing other panes
        // traverse all the AnchorPanes in the parent AnchorPane
        for( Node node: pane_all.getChildren()) {

            // make all AnchorPanes invisible
            if( node instanceof AnchorPane) {
                node.setVisible(false);
            }
        }

        // traverse all the AnchorPanes in the parent AnchorPane
        for( Node node: menu.getChildren()) {

            // make all AnchorPanes invisible
            if( node instanceof JFXButton) {
                node.setStyle(null);
            }
        }

        // show the AnchorPane that was selected
        if (event.getSource() == tab_home) {
            pane_home.toFront();
            pane_home.setVisible(true);
            tab_home.setStyle("-fx-background-color: #FBFFBC;");

        } else if (event.getSource() == tab_newday) {
            pane_newday.toFront();
            pane_newday.setVisible(true);
            tab_newday.setStyle("-fx-background-color: #FBFFBC;");

        } else if (event.getSource() == tab_viewdays) {
            pane_viewdays.toFront();
            pane_viewdays.setVisible(true);
            tab_viewdays.setStyle("-fx-background-color: #FBFFBC;");

            initViewDays();

        } else if (event.getSource() == tab_todolist) {
            pane_todolist.toFront();
            pane_todolist.setVisible(true);
            tab_todolist.setStyle("-fx-background-color: #FBFFBC;");
            displayTodolist();

        } else if (event.getSource() == tab_calendar) {
            pane_calendar.toFront();
            pane_calendar.setVisible(true);
            tab_calendar.setStyle("-fx-background-color: #FBFFBC;");

            initCalendar();
        }
    }

    @FXML
    private void handleMoodButtonAction(ActionEvent event) {

        Pair<AnchorPane, String> currentPage = util.getCurrentPage(event);

        AnchorPane pane = currentPage.getKey();
        String pageName = currentPage.getValue();

        int numCharToStartSubstring = 0;

        if (pageName.contains("newday")) {
            numCharToStartSubstring = 12;

        } else {
            numCharToStartSubstring = 13;

        }

        Label label_moodselectedprompttext = (Label) pane.lookup("#" + pageName + "_moodselectedprompttext");
        Label label_moodtext = (Label) pane.lookup("#" + pageName + "_moodtext");

        for( Node node: pane.getChildren()) {

            // remove selected color from all buttons
            if( node instanceof JFXButton && !node.getId().contains("saveButton")) {
                node.setStyle("-fx-background-color: #E8ECAE;");
            }
        }

        // add selected colour to selected button
        ((JFXButton)event.getSource()).setStyle("-fx-background-color: #494B35;");

        // mood fxid is newday_mood_happy, so substring 12 gets the mood after 12 characters
        String mood = ((JFXButton)event.getSource()).getId().substring(numCharToStartSubstring);
        label_moodselectedprompttext.setVisible(true);
        label_moodtext.setVisible(true);
        label_moodtext.setText(mood);
    }

    @FXML
    private void todolist_enterKeyAdd(KeyEvent event) {
        if( event.getCode() == KeyCode.ENTER ) {
            addTodo();
        }
    }

    @FXML
    private void addTodo() {

        String newTodoText = todolist_todoInput.getText();

        if (newTodoText.length() > 0) {
            CheckBox newTodo = new CheckBox(newTodoText);
            todos.add(newTodo);

            try {
                FileWriter w = new FileWriter(mainFolderPath + "todolist.txt");

                for (Node todo : todos) {
                    w.write(((CheckBox) todo).getText());
                    w.append(System.getProperty( "line.separator" ));
                    todolist_todoInput.clear();
                }
                w.close();
                displayTodolist();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void displayTodolist() {

        todos.clear();
        todolist_vbox.getChildren().clear();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(mainFolderPath + "todolist.txt");
            Scanner sc = new Scanner(fileReader);

            while (sc.hasNextLine()) {
                CheckBox todo = new CheckBox(sc.nextLine());

                Font font = Font.font("System", FontWeight.BOLD, 13);
                todo.setFont(font);
                todo.setStyle("-fx-text-fill:  #494B35");

                todo.setOnAction(e -> {
                    checkTodo();
                });

                todos.add(todo);
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        todolist_vbox.getChildren().addAll(todos);
    }

    private void checkTodo() {

        // loop through all checkboxes
        for( Node node: todolist_vbox.getChildren()) {

            if (node instanceof CheckBox) {

                if (((CheckBox) node).isSelected()) {
                    FadeTransition ft = new FadeTransition(Duration.millis(3000), node);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    ft.play();

                    int indexToRemove = todos.indexOf(node);
                    todos.remove(indexToRemove);

                    try {
                        FileWriter w = new FileWriter(mainFolderPath + "todolist.txt");

                        for (Node todo : todos) {
                            w.write(((CheckBox) todo).getText());
                            w.append(System.getProperty( "line.separator" ));
                        }

                        w.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        displayTodolist();

    }

    @FXML
    private void saveNewEntry(ActionEvent event) throws IOException {

        Pair<AnchorPane, String> currentPage = util.getCurrentPage(event);

        AnchorPane pane = currentPage.getKey();
        String pageName = currentPage.getValue();

        TextArea textarea = (TextArea) pane.lookup("#" + pageName + "_textarea");
        Label label_moodtext = (Label) pane.lookup("#" + pageName + "_moodtext");
        Label label_moodselectedprompttext = (Label) pane.lookup("#" + pageName + "_moodselectedprompttext");

        String filename= daysFolderPath + util.displayDateForFilename() + ".txt";

        File currentDayFile = new File(filename);

        currentDayFile.createNewFile(); // if file already exists will do nothing
        String mood = String.valueOf(label_moodtext.getText());

        // if text is empty or no mood is selected
        if ((textarea.getText().trim().length() <= 0) || mood.equals("[mood]")  || mood == null) {
            setResponse("null", event);

        } else {
            try {
                FileWriter w = new FileWriter(currentDayFile);
                w.write(mood);
                w.append(System.getProperty( "line.separator" ));
                w.append(textarea.getText());
                w.close();

                textarea.clear();
                setResponse("success", event);

            } catch (Exception e) {
                e.printStackTrace();
                setResponse("error", event);
            }
        }

        // reset everything to default
        for( Node node: pane.getChildren()) {

            // remove selected color from all buttons
            if( node instanceof JFXButton && node.getId() != "newday_saveButton") {
                node.setStyle("-fx-background-color: #E8ECAE;");
            }
        }
        label_moodselectedprompttext.setVisible(false);
        label_moodtext.setVisible(false);
        label_moodtext.setText(null);

    }

    private void setResponse(String type, ActionEvent event) {

        Pair<AnchorPane, String> currentPage = util.getCurrentPage(event);

        AnchorPane pane = currentPage.getKey();
        String pageName = currentPage.getValue();

        Label label_responsemsg = (Label) pane.lookup("#" + pageName + "_responsemsg");

        label_responsemsg.setWrapText(true);
        label_responsemsg.setAlignment(Pos.CENTER_RIGHT);

        if (type == "success") {
            label_responsemsg.setText("Your entry has been saved!"); // success message
            label_responsemsg.setStyle("-fx-text-fill: #137000"); // set text colour to dark green

        } else if (type == "null") {
            label_responsemsg.setText("Choose your mood and write something first, then try again."); // error message
            label_responsemsg.setStyle("-fx-text-fill: #700C00"); // set text colour to dark green

        } else if (type == "error") {
            label_responsemsg.setText("An error has occurred. Please try again!"); // error message
            label_responsemsg.setStyle("-fx-text-fill: #700C00"); // set text colour to dark red
        }

        label_responsemsg.setVisible(true); // display message

        // make success message fade out after 3s
        FadeTransition ft = new FadeTransition(Duration.millis(3000), label_responsemsg);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    public void renderDay(String filename, BufferedReader bufferedReader, String type) throws IOException {

        try {
            StringBuilder sb = new StringBuilder();
            String line = bufferedReader.readLine();

            String mood = line;
            line = bufferedReader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }

            Image moodIcon = new Image(getClass().getResourceAsStream("images/moods/" + mood + ".png"), 40, 40, false, false);

            filename = filename.replace("-", " ");

            JFXButton day = new JFXButton(filename);

            Font font = Font.font("System", FontWeight.BOLD, 15);
            day.setFont(font);
            ImageView imageView = new ImageView(moodIcon);
            imageView.setSmooth(true);

            day.setGraphic(imageView);
            day.setGraphicTextGap(20);
            day.setAlignment(Pos.CENTER_LEFT);

            day.setPrefHeight(100);
            day.setPrefWidth(viewdays_scrollpane.getPrefWidth() - 17);

            day.setStyle("-fx-padding: 0 0 0 20;" +
                    "-fx-background-color: #494B35;" +
                    "-fx-text-fill: white;");

            // handle event for when one day is clicked
            day.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {
                    // STYLING
                    for( Node node: vbox.getChildren()) {

                        // remove selected color from all buttons
                        if( node instanceof JFXButton) {
                            node.setStyle("-fx-padding: 0 0 0 20;" +
                                    "-fx-background-color: #494B35;" +
                                    "-fx-text-fill: white;");
                        }
                    }

                    day.setStyle("-fx-padding: 0 0 0 20;" +
                            "-fx-background-color: #E8ECAE;" +
                            "-fx-text-fill: #494B35;");

                    // detect double click
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 2){
                            openDay();
                        }
                    }
                }
            });


//            daysList.add(day);
            if (type == "specific") {
                day.setPrefWidth(viewdays_scrollpane.getPrefWidth() - 2);
            }
            vbox.getChildren().add(day);
            vbox.setStyle("-fx-background-color: #494B35;");

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            bufferedReader.close();
        }
    }

    @FXML
    public void displaySpecificDay() {
        // clear list of days from view
        daysList.clear();
        vbox.getChildren().clear();

        File file = new File(daysFolderPath);
        String filename;

        // Reading directory contents
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {

            // get name of every file in the days folder
            filename = files[i].getName().substring(0, files[i].getName().length() - 4);

            // convert LocalDate to Date
            ZoneId defaultZoneId = ZoneId.systemDefault();

            //creating the instance of LocalDate using the day, month, year info
            LocalDate localDate = viewdays_datepicker.getValue();

            //local date + atStartOfDay() + default time zone + toInstant() = Date
            Date targetDate = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

            if (filename.contains(util.displayDateForFilename(targetDate))) {

                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(files[i]));
                    renderDay(filename, bufferedReader, "specific");
                    bufferedReader.close();

                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }

                break;
            }

        }

    }

    @FXML
    public void displayViewDays() throws IOException {
        File file = new File(daysFolderPath);
        String filename;

        // Reading directory contents
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {

            // get name of every file in the days folder
            filename = files[i].getName().substring(0, files[i].getName().length() - 4);

            // render every single day
            BufferedReader bufferedReader = new BufferedReader(new FileReader(files[i]));
            renderDay(filename, bufferedReader, "all");
        }
//        vbox.getChildren().addAll(daysList);
        vbox.setStyle("-fx-background-color: #494B35;");
        viewdays_scrollpane.setContent(vbox);

    }

    private Node getSelectedDay() {

        // loop through all days in the list of days
        for( Node node: vbox.getChildren()) {

            if (node instanceof JFXButton) {
                // check if the node has the light grey background (check that it is the selected day)
                if (node.getStyle().contains("#E8ECAE")) {
                    return node;
                }
            }
        }
        // if none are selected / no days in the list
        return null;
    }

    @FXML
    private void deleteDay() {
        Node node = getSelectedDay();

        if (node != null) {
            String selectedDayName = ((JFXButton) node).getText();
            String targetDayFilename = daysFolderPath + selectedDayName.replace(" ", "-") + ".txt";

            File fileToDelete = new File(targetDayFilename);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setContentText("Are you sure you want to delete the entry for day " + selectedDayName + "?");

            ButtonType buttonYes = new ButtonType("Yes");
            ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonYes, buttonNo);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == buttonYes) {

                if (fileToDelete.delete()) {
                     initViewDays();
                } else {
                    System.out.println("Failed to delete the file.");
                }
            }

        } else {
            alertIfNoDaySelected("delete");
        }


    }

    @FXML
    private void editDay() {
        Node node = getSelectedDay();

        if (node != null) {
            pane_viewdays.setVisible(false);
            pane_editday.toFront();
            pane_editday.setVisible(true);

            String selectedDayName = ((JFXButton) node).getText(); // eg 16 Jan 2022
            String targetDayFilename = daysFolderPath + selectedDayName.replace(" ", "-") + ".txt";

            String pageName = "editday";

            TextArea textarea = (TextArea) pane_editday.lookup("#" + pageName + "_textarea");
            Label label_moodtext = (Label) pane_editday.lookup("#" + pageName + "_moodtext");
            Label label_moodselectedprompttext = (Label) pane_editday.lookup("#" + pageName + "_moodselectedprompttext");

            editday_date.setText(selectedDayName);

            // read original content of the day's entry
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(targetDayFilename);

            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }

            BufferedReader readFileBuffer = new BufferedReader(fileReader);
            StringBuilder dayContent = new StringBuilder();
            String contentLine;

            try {
                String mood = readFileBuffer.readLine(); // read mood line so that it is not included in the actual content

                // eg JFXButton mood fxid: editday_mood_happy
                for( Node moodNode: pane_editday.getChildren()) {

                    // remove selected color from all buttons
                    if( moodNode instanceof JFXButton && !moodNode.getId().contains("saveButton")) {
                        moodNode.setStyle("-fx-background-color: #E8ECAE;");
                    }

                    // add selected colour to selected button
                    if (moodNode instanceof JFXButton && moodNode.getId().contains(mood)) {
                        moodNode.setStyle("-fx-background-color: #494B35;");
                    }
                }

                // mood fxid is newday_mood_happy, so substring 12 gets the mood after 12 characters
                label_moodselectedprompttext.setVisible(true);
                label_moodtext.setVisible(true);
                label_moodtext.setText(mood);

                while( (contentLine = readFileBuffer.readLine()) != null) {
                    dayContent.append(contentLine);
                }

                fileReader.close();
                readFileBuffer.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            // take content of the day and put it into textarea to edit it
            textarea.setText(String.valueOf(dayContent));

            // after save button is clicked
            editday_saveButton.setOnAction(event -> {
                String mood = String.valueOf(label_moodtext.getText());

                // if text is empty or no mood is selected
                if ((textarea.getText().trim().length() <= 0) || mood == "[mood]" || mood == null) {
                    setResponse("null", event);

                } else {
                    try {
                        FileWriter w = new FileWriter(targetDayFilename);
                        w.write(mood);
                        w.append(System.getProperty( "line.separator" ));
                        w.append(textarea.getText());
                        w.close();

                        textarea.clear();
                        setResponse("success", event);

                    } catch (Exception e) {
                        e.printStackTrace();
                        setResponse("error", event);
                    }
                }
                // reset everything to default
                for( Node moodNode: pane_editday.getChildren()) {

                    // remove selected color from all buttons
                    if( moodNode instanceof JFXButton && !moodNode.getId().contains("saveButton")) {
                        moodNode.setStyle("-fx-background-color: #E8ECAE;");
                    }
                }
                label_moodselectedprompttext.setVisible(false);
                label_moodtext.setVisible(false);
                label_moodtext.setText(null);

                // after edit done, hide the edit pane
                // show the viewdays pane again after 2 seconds
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                    pane_editday.setVisible(false);

                    initViewDays();
                    pane_viewdays.setVisible(true);
                    pane_viewdays.toFront();
                }));
                timeline.play();

            });
        } else {
            alertIfNoDaySelected("edit");
        }

    }

    @FXML
    private void openDay() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.getButtonTypes().add(ButtonType.OK);

        Node node = getSelectedDay();

        if (node != null) {
            String selectedDayName = ((JFXButton) node).getText();
            String targetDayFilename = daysFolderPath + selectedDayName.replace(" ", "-") + ".txt";

            alert.setTitle(targetDayFilename);

            FileReader fileReader = null;
            try {
                fileReader = new FileReader(targetDayFilename);

            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }

            BufferedReader readFileBuffer = new BufferedReader(fileReader);

            StringBuilder dayContent = new StringBuilder();
            String contentLine;

            try {
                readFileBuffer.readLine(); // read mood line so that it is not included in the actual content

                while( (contentLine = readFileBuffer.readLine()) != null) {
                    dayContent.append(contentLine);
                }

                fileReader.close();
                readFileBuffer.close();

            } catch (IOException e2) {
                e2.printStackTrace();
            }

            alert.setHeaderText(selectedDayName);
            alert.setContentText(String.valueOf(dayContent));
            alert.show();

        } else {
            alertIfNoDaySelected("open");
        }
    }

    private void initViewDays() {
        try {
            vbox.getChildren().clear();
            displayViewDays();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCalendar() {

        DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
        Node popupContent = datePickerSkin.getPopupContent();

        popupContent.setStyle("-fx-font-size: 2em;");

        calendar_calendarholder.getChildren().add(popupContent);
    }

    private void initUsername() throws IOException {
        File usernameFile = new File(mainFolderPath + "username.txt");

        BufferedReader br = new BufferedReader(new FileReader(usernameFile));

        String username = br.readLine();

        home_username.setText(username + "!");

    }

    private void alertIfNoDaySelected(String actionType) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.getButtonTypes().add(ButtonType.OK);
        alert.setHeaderText("Oops!");
        alert.setContentText("Please select a day to " + actionType + "!");
        alert.show();
    }

    @FXML
    private void handleExitButton() {
        System.exit(0);
    }

    private void initHomePage() {
        try {
            initUsername();
            Image weatherIcon = new Image(weather.getWeatherIcon());
            home_weatherIcon.setImage(weatherIcon);
            home_weatherText.setText(weather.getWeatherText());
            home_weatherTemp.setText(weather.getWeatherTemp());
        } catch (Exception e) {
            e.printStackTrace();
        }

        newday_textarea.setWrapText(true);
        util.displayDate(home_date);
        util.displayTimeAnimation(home_time, home_daynightimage);
        home_quote.setText("“" + quote.generateQuote() + "”" );
        home_quote.setWrapText(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initHomePage();


//        initViewDays();
//        initCalendar();
//        displayTodolist();

    }
}
