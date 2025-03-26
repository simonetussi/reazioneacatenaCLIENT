package client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @FXML private Button btn_play;
    @FXML private Text id_nickname_setted;
    @FXML private Text id_best_score, id_current_score, id_last_score;
    @FXML private ImageView id_green, id_red, id_yellow, id_blue;
    @FXML private TextField lbl_nickname;

    private int currentScore = 0, currentStep = 0, userIndex = 0;
    private ArrayList<Integer> userInput = new ArrayList<>();
    private int[] sequence = new int[100];
    private boolean isPlaying = false;

    @FXML
    void play(ActionEvent event) {
        String nickname = lbl_nickname.getText().trim();
        if (!checkNickname(nickname)) return;

        id_nickname_setted.setText(nickname);
        btn_play.setVisible(false);
        lbl_nickname.setVisible(false);
        startGame();
    }

    @FXML
    void initialize() {
        id_green.setOnMouseClicked(_ -> handleColorClick(1));
        id_red.setOnMouseClicked(_ -> handleColorClick(2));
        id_yellow.setOnMouseClicked(_ -> handleColorClick(3));
        id_blue.setOnMouseClicked(_ -> handleColorClick(4));
    }

    public void startGame() {
        createSequence();
        currentStep = 1;
        userIndex = 0;
        currentScore = 0;
        id_current_score.setText("0");
        disableImages(true);
        playSequence();
    }

    public void handleColorClick(int color) {
        if (isPlaying) return;

        if (userIndex < currentStep) {
            userInput.add(color);
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

    private void playSequence() {
        isPlaying = true;
        disableImages(true);
        Timeline timeline = new Timeline();

        for (int i = 0; i < currentStep; i++) {
            int color = sequence[i];

            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i),
                _ -> illuminateColor(color)));
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i + 0.5),
                _ -> resetColor()));
        }

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
        id_last_score.setText(String.valueOf(currentScore));
        int bestScore = Integer.parseInt(id_best_score.getText());
        if (currentScore > bestScore) {
            id_best_score.setText(String.valueOf(currentScore));
        }
        sendScoreToServer(currentScore);
        currentScore = 0;
        currentStep = 0;
        userIndex = 0;
        showAlert(AlertType.ERROR, "Errore", "Hai sbagliato!",
            "Sequenza errata! Riprova.");
        startGame();
    }

    public boolean checkNickname(String nickname) {
        if (nickname.isEmpty() || nickname.length() < 3 || nickname.length() > 15) {
            showAlert(AlertType.WARNING, "Errore", "Nickname non valido",
                "Il nickname deve essere tra 3 e 15 caratteri.");
            return false;
        } else if (!nickname.matches("^[A-Za-z][A-Za-z0-9_]*[A-Za-z]$")) {
            showAlert(AlertType.WARNING, "Errore", "Formato errato",
                "Il nickname deve iniziare e finire con una lettera.");
            return false;
        }
        return true;
    }

    public void illuminateColor(int color) {
        resetColor();
        switch (color) {
            case 1 -> id_green.setOpacity(1.0);
            case 2 -> id_red.setOpacity(1.0);
            case 3 -> id_yellow.setOpacity(1.0);
            case 4 -> id_blue.setOpacity(1.0);
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

    private void sendScoreToServer(int score) {
        try {
            String url = "http://localhost:8080/reazioneacatenaSERVER";
            String params = "action=registerScore&nickname=" 
                    + URLEncoder.encode(id_nickname_setted.getText(), "UTF-8") 
                    + "&score=" + score;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
            }

            // Verifica la risposta del server
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                showAlert(AlertType.ERROR, "Errore", "Impossibile salvare il punteggio",
                        "Si è verificato un errore nel salvataggio del punteggio. Prova di nuovo.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Errore di Connessione", "Errore di rete",
                    "Impossibile connettersi al server. Verifica la connessione e riprova.");
        }
    }
}
