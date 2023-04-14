package dk.easv;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController
{
    private final List<Image> images = new ArrayList<>();
    @FXML
    private Label lblFileName;
    @FXML
    private Button btnStartSlideShow, btnStopSlide;
    private int currentImageIndex = 0;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    private  Thread t;

    @FXML
    private void handleBtnLoadAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (files != null && !files.isEmpty())
        {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
        }
    }

    @FXML
    private void handleBtnPreviousAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    public void handleBtnNextAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    imageView.setImage(images.get(currentImageIndex));
                    File file = new File(images.get(currentImageIndex).getUrl());
                    lblFileName.setText("File name: "+file.getName());
                }
            });
        }
    }

    @FXML
    private void handleBtnStartSlides() throws InterruptedException {
        btnStartSlideShow.setDisable(true);
        btnStopSlide.setDisable(false);
        Task task = new Task<>() {
            @Override public Void call() throws InterruptedException {

                for (int i=1; i<=images.size(); i++) {
                    displayImage();
                    Thread.sleep(1000);
                    handleBtnNextAction();
                    if(i == images.size()){
                        i = 0;
                        currentImageIndex = 0;

                    }
                }
                return null;
            }
        };
        t = new Thread(task);
        t.start();

    }

    @FXML
    private void handleBtnStopSlides(ActionEvent actionEvent) {
        btnStartSlideShow.setDisable(false);
        btnStopSlide.setDisable(true);
        t.interrupt();
    }
}