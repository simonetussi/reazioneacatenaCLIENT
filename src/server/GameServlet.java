package server;

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
        disableImages(true);
    }

    public void startGame() {
        createSequence();
        currentStep = 0;
        userIndex = 0;
        userInput.clear();
        id_current_score.setText("0");
        
        disableImages(true);
        playSequence();
        disableImages(false);
    }

    public void handleColorClick(int color) {
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
		disableImages(true);
		for (int i = 0; i < currentStep; i++) {
			int color = sequence[i];
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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			switch (color) {
			case 1: id_green.setOpacity(0.3);
				break;
			case 2: id_red.setOpacity(0.3);
				break;
			case 3: id_yellow.setOpacity(0.3);
				break;
			case 4: id_blue.setOpacity(0.3);
				break;
			}
		}
		disableImages(false);
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
        showAlert(AlertType.ERROR, "Attenzione!",
        		"Hai sbagliato!", "La sequenza inserita non è corretta. Riprova da capo.");
    }
    
    public static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void disableImages(boolean disable) {
    	System.out.println("Disabling images: " + disable);
        id_green.setDisable(disable);
        id_red.setDisable(disable);
        id_yellow.setDisable(disable);
        id_blue.setDisable(disable);
    }
}
