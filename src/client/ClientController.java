package client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;

public class ClientController {

    @FXML private URL location;
    @FXML private Button btn_play;
    @FXML private Text id_nickname_setted;
    @FXML private Text id_best_score, id_current_score, id_last_score;
    @FXML private ImageView id_green, id_red, id_yellow, id_blue;
    @FXML private ImageView id_logo;
    @FXML private AnchorPane id_background;
    @FXML private TextField lbl_nickname;

    private int currentScore = 0, currentStep = 0, userIndex = 0;
    private ArrayList < Integer > userInput = new ArrayList < > ();
    private int[] sequence = new int[100];
    private boolean isPlaying = false;

    @FXML
    void play(ActionEvent event) {
        String nickname = lbl_nickname.getText().trim();
        if (checkNickname(nickname) == false) return;

        id_nickname_setted.setText(lbl_nickname.getText());
        btn_play.setVisible(false);
        lbl_nickname.setVisible(false);

        startGame();
    }

    @FXML
    void initialize() {
        // Imposta i listener per il clic sui colori
        id_green.setOnMouseClicked(_ -> handleColorClick(1));
        id_red.setOnMouseClicked(_ -> handleColorClick(2));
        id_yellow.setOnMouseClicked(_ -> handleColorClick(3));
        id_blue.setOnMouseClicked(_ -> handleColorClick(4));
    }

    public void startGame() {
        createSequence();
        currentStep = 1;
        userIndex = 0;
        id_current_score.setText("0");

        disableImages(true);
        playSequence();
    }

    public void handleColorClick(int color) {
        if (isPlaying) return; // Ignora se la sequenza è in riproduzione

        if (userIndex < currentStep) {
            userInput.add(color);
            if (color != sequence[userIndex]) { // Controlla se il colore è corretto
                handleError(); // Gestisce l'errore
                return;
            }
            userIndex++;
            if (userIndex == currentStep) { // Se la sequenza è completata
                currentScore++; // Incrementa il punteggio
                id_current_score.setText(String.valueOf(currentScore));
                currentStep++; // Aumenta la lunghezza della sequenza
                userIndex = 0;
                userInput.clear(); // Pulisce l'input dell'utente
                playSequence(); // Mostra la nuova sequenza
            }
        }
    }

    private void playSequence() {
        isPlaying = true;
        disableImages(true);
        Timeline timeline = new Timeline(); // Anima la sequenza

        for (int i = 0; i < currentStep; i++) {
            int color = sequence[i];

            // Illumina il colore
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.0),
                _ -> illuminateColor(color)));

            // Resetta il colore dopo 0.5 secondi
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.0 + 0.5),
                _ -> resetColor()));
        }

        // Al termine della sequenza, abilita le immagini
        timeline.setOnFinished(_ -> {
            disableImages(false);
            isPlaying = false;
            userInput.clear();
            userIndex = 0;
        });
        timeline.play();
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
        if (currentScore > bestScore) {
            id_best_score.setText(String.valueOf(currentScore));
        }
        currentScore = 0;
        currentStep = 0;
        userIndex = 0;
        showAlert(AlertType.ERROR, "Attenzione!", "Hai sbagliato!",
            "La sequenza inserita non è corretta. Riprova da capo.");
        startGame();
    }

    public boolean checkNickname(String nickname) {
        if (nickname.isEmpty()) {
            showAlert(AlertType.WARNING, "ATTENZIONE", "Errore nickname",
                "Devi inserire un nickname per poter giocare.");
            return false;
        } else if (nickname.length() < 3) {
            showAlert(AlertType.WARNING, "ATTENZIONE", "Nickname troppo corto",
                "Il nickname deve essere lungo tra 3 e 15 caratteri.");
            return false;
        } else if (nickname.length() > 15) {
        	showAlert(AlertType.WARNING, "ATTENZIONE", "Nickname troppo lungo",
        			"Il nickname deve essere lungo tra 3 e 15 caratteri.");
        	return false;
        } else if (!nickname.matches("^[A-Za-z][A-Za-z0-9_]*[A-Za-z]$")) {
            showAlert(AlertType.WARNING, "ATTENZIONE", "Nickname inadeguato",
                "Il nickname deve:\n  ➔ iniziare e finire con una lettera,\n"
                + "  ➔ contenere solo lettere, numeri e underscore,\n"
                + "  ➔ non contenere spazi o caratteri speciali.");
            return false;
        }
        return true;
    }

    public void illuminateColor(int color) {
        resetColor();
        switch (color) {
        case 1:
            id_green.setOpacity(1.0);
            break;
        case 2:
            id_red.setOpacity(1.0);
            break;
        case 3:
            id_yellow.setOpacity(1.0);
            break;
        case 4:
            id_blue.setOpacity(1.0);
            break;
        }
    }

    public static void showAlert(AlertType type, String title,
        String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void resetColor() {
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