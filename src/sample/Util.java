package sample;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public void displayDate(Label label) {
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy (EEE)");
        Date currentDate = new Date();
        label.setText(dateFormat.format(currentDate) + ".");
    }

    public String displayDateForFilename() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    // overloading function with specified date
    public String displayDateForFilename(Date targetDate) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return dateFormat.format(targetDate);
    }

    public void displayTimeAnimation(Label label, ImageView imageview) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH : mm : ss");
            label.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        Calendar timeNow = Calendar.getInstance();
        int hour = timeNow.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour <= 18) {
            imageview.setImage(new Image(getClass().getResourceAsStream("images/home_day.png")));
        } else {
            imageview.setImage(new Image(getClass().getResourceAsStream("images/home_night.png")));
        }
    }

    public Pair<AnchorPane, String> getCurrentPage(ActionEvent event) {
        AnchorPane pane = (AnchorPane) ((JFXButton)event.getSource()).getParent();

        // eg pane.getId() = pane_newday
        // eg node names = newday_moodtext

        // page name, eg newday
        String pageName = pane.getId().substring(pane.getId().lastIndexOf("_") + 1);

        return new  Pair<AnchorPane, String>(pane, pageName);
    }
}
