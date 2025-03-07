package server;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.Random;

public class GameServlet {
    private int[] sequence = new int[54];
    private int currentStep = 0;
    private int userIndex = 0;
    private ArrayList<Integer> userInput = new ArrayList<>();
    private int currentScore = 0;

    private Text id_current_score, id_last_score, id_best_score;
    private AnchorPane id_background;
    private ImageView id_logo, id_green, id_red, id_yellow, id_blue;

    public GameServlet(Text id_current_score, Text id_last_score, Text id_best_score,
                       AnchorPane id_background, ImageView id_green, ImageView id_red,
                       ImageView id_yellow, ImageView id_blue, ImageView id_logo, TextField id_nickname) {

        this.id_current_score = id_current_score;
        this.id_last_score = id_last_score;
        this.id_best_score = id_best_score;
        this.id_background = id_background;
        this.id_logo = id_logo;
        this.id_green = id_green;
        this.id_red = id_red;
        this.id_yellow = id_yellow;
        this.id_blue = id_blue;
        System.out.println("GameServlet initialized");
        disableImages(true);
    }

    public void startGame() {
    	System.out.println("Starting game...");
        createSequence();
        System.out.println("Sequence created");
        currentStep = 0;
        userIndex = 0;
        userInput.clear();
        id_current_score.setText("0");
        System.out.println("Current score reset");
        
        disableImages(true);
        System.out.println("Images disabled");
        playSequence();
        System.out.println("Sequence played");
        disableImages(false);
        System.out.println("Images enabled");
    }

    public void handleColorClick(int color) {
		if (userIndex < currentStep) {
			userInput.add(color);
			illuminateColor(color);
			System.out.println("User clicked color: " + color);
			if (color != sequence[userIndex]) {
				handleError();
				return;
			}
			userIndex++;
			if (userIndex == currentStep) {
				currentScore++;
				id_current_score.setText(String.valueOf(currentScore));
				currentStep++;
				userIndex = 0;
				userInput.clear();
				playSequence();
			}
		}
    }

    private void createSequence() {
        Random rand = new Random();
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = rand.nextInt(4) + 1;
        }
    }

    private void handleError() {
        id_current_score.setText("0");
        id_last_score.setText(String.valueOf(currentScore));
        int bestScore = Integer.parseInt(id_best_score.getText());
        System.out.println("Best score: " + bestScore);
        if (currentScore > bestScore) {
            id_best_score.setText(String.valueOf(currentScore));
        }
        showAlert(AlertType.ERROR, "Attenzione!",
        		"Hai sbagliato!", "La sequenza inserita non è corretta. Riprova da capo.");
        System.out.println("Error: sequence incorrect");
    }

    private void playSequence() {
        id_background.setStyle("-fx-background-color: gray");
        id_logo.setOpacity(0.3);
        resetColor();
        userInput.clear();
        userIndex = 0;

        Timeline timeline = new Timeline();
        
        // Crea KeyFrames con ritardo per ogni colore della sequenza
        for (int i = 0; i <= currentStep; i++) {
            final int color = sequence[i];
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.0), // Aumento il ritardo a 1 secondo
                e -> illuminateColor(color)));
        }

        // KeyFrame finale per disabilitare le immagini e ripristinare il background
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds((currentStep + 1) * 1.0),
            e -> {
                disableImages(false);
                id_background.setStyle("-fx-background-color: white");
                id_logo.setOpacity(1.0);
            }));

        // Esegui l'animazione
        timeline.play();
    }
    
    public static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void illuminateColor(int color){
        switch (color) {
            case 1: id_green.setOpacity(1.0); 
            	break;
            case 2: id_red.setOpacity(1.0);
            	break;
            case 3: id_yellow.setOpacity(1.0);
            	break;
            case 4: id_blue.setOpacity(1.0);
            	break;
        }
        System.out.println("Color illuminated: " + color);
        resetColor();
    }

    private void resetColor() {
    	System.out.println("Resetting color");
        id_green.setOpacity(0.3);
        id_red.setOpacity(0.3);
        id_yellow.setOpacity(0.3);
        id_blue.setOpacity(0.3);
    }

    private void disableImages(boolean disable) {
    	System.out.println("Disabling images: " + disable);
        id_green.setDisable(disable);
        id_red.setDisable(disable);
        id_yellow.setDisable(disable);
        id_blue.setDisable(disable);
    }
}
