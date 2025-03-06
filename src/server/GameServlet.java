package server;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Random;

import client.ClientController;

public class GameServlet {
    private int[] sequence = new int[54];
    private int currentStep = 0;
    private int userIndex = 0;
    private ArrayList<Integer> userInput = new ArrayList<>();
    private String currentScore = "0";
    private boolean nicknameValid = false;

    private Text id_current_score, id_last_score, id_best_score;
    private AnchorPane id_background;
    private ImageView id_green, id_red, id_yellow, id_blue, id_logo;

    public GameServlet(Text id_current_score, Text id_last_score, Text id_best_score,
                       AnchorPane id_background, ImageView id_green, ImageView id_red,
                       ImageView id_yellow, ImageView id_blue, ImageView id_logo, TextField id_nickname) {
    	
        this.id_current_score = id_current_score;
        this.id_last_score = id_last_score;
        this.id_best_score = id_best_score;
        this.id_background = id_background;
        this.id_green = id_green;
        this.id_red = id_red;
        this.id_yellow = id_yellow;
        this.id_blue = id_blue;
        this.id_logo = id_logo;
        
        disableImages(true);
    }

    

    public void setNicknameValid(boolean isValid) {
        this.nicknameValid = isValid;
        disableImages(!isValid);
    }

    public void startGame() {
        if (!nicknameValid) return;

        if (sequence[0] == 0) {  // Se la sequenza non è ancora stata generata, generala una sola volta
            createSequence();
        }

        currentStep = 0;  // Inizializza il passo corrente
        userIndex = 0;    // Indice per tenere traccia della sequenza dell'utente
        userInput.clear(); // Pulisce l'input dell'utente
        id_current_score.setText("0");

        disableImages(true);

        playSequence();  // Avvia la sequenza
    }

    public void handleColorClick(int color) {
        // Assicurati che il clic sia possibile solo quando la sequenza è stata completata e non durante la sequenza stessa
        if (!nicknameValid || userInput.size() > currentStep) return;  // Permetti solo se la sequenza è finita

        userInput.add(color);  // Aggiungi l'input dell'utente

        if (userInput.get(userIndex) == sequence[userIndex]) {  // Se l'input è corretto
            userIndex++;  // Passa al prossimo indice

            // Se l'utente ha completato l'intera sequenza di un passo
            if (userIndex == currentStep + 1) {
                currentStep++;  // Aumenta il passo corrente (sequenza più lunga)
                id_background.setStyle("-fx-background-color: gray");
                currentScore = "" + userIndex;  // Aggiorna il punteggio
                id_current_score.setText(currentScore);

                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1), _ -> playSequence()));
                delay.play();  // Avvia il prossimo passo della sequenza
            }
        } else {
            handleError();  // Chiamato per gestire l'errore e ricominciare il gioco
        }
    }

    private void createSequence() {
        Random rand = new Random();
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = rand.nextInt(4) + 1;  // Crea una sequenza casuale (valori tra 1 e 4)
        }
    }
    
    private void handleError() {
        id_current_score.setText("0");
        id_last_score.setText(currentScore);
        int bestScore = Integer.parseInt(id_best_score.getText());
        int lastScore = Integer.parseInt(id_last_score.getText());
        if (lastScore > bestScore) {
            id_best_score.setText(id_last_score.getText());
        }
        ClientController.showAlert(AlertType.ERROR, "Attenzione!", "Hai sbagliato!",
                "La sequenza inserita non è corretta. Riprova da capo.");

        startGame(); // Avvia un nuovo gioco dopo l'errore
    }


    private void playSequence() {
        id_background.setStyle("-fx-background-color: gray");
        id_logo.setOpacity(0.3);

        disableImages(true);  // Disabilita i clic sui colori fino a che la sequenza non è finita.

        Timeline timeline = new Timeline();
        for (int i = 0; i <= currentStep; i++) {
            final int index = i;
            double startTime = index * 1.5;  // Aumenta il tempo tra i colori per renderli visibili più a lungo
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(startTime), _ -> illuminateColor(sequence[index])));
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(startTime + 1.0), _ -> resetColor()));  // Durata maggiore
        }

        timeline.setOnFinished(_ -> {
            id_background.setStyle("-fx-background-color: white");
            id_logo.setOpacity(1.0);
            disableImages(false);  // Abilita i colori per l'interazione dell'utente
        });

        timeline.play();
    }

    private void illuminateColor(int color) {
        resetColor();
        
        ImageView targetImage = null;
        switch (color) {
            case 1: targetImage = id_green; break;
            case 2: targetImage = id_red; break;
            case 3: targetImage = id_yellow; break;
            case 4: targetImage = id_blue; break;
        }
        
        if (targetImage != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), targetImage);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }
    
    public static void addHoverEffects(ImageView image) {
        image.setOnMouseEntered(_ -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), image);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), image);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        image.setOnMouseExited(_ -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), image);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), image);
            fadeOut.setToValue(0.3);
            fadeOut.play();
        });
    }

    private void resetColor() {
        id_green.setOpacity(0.3);
        id_red.setOpacity(0.3);
        id_yellow.setOpacity(0.3);
        id_blue.setOpacity(0.3);
    }

    private void disableImages(boolean disable) {
        id_green.setDisable(disable);
        id_red.setDisable(disable);
        id_yellow.setDisable(disable);
        id_blue.setDisable(disable);
    }
}
