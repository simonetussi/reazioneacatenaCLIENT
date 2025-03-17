package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import server.GameServlet;

public class ClientController {

    @FXML private URL location;
    @FXML private Button btn_play;
    @FXML private Text id_nickname_setted;
    @FXML private Text id_best_score, id_current_score, id_last_score;
    @FXML private ImageView id_green, id_red, id_yellow, id_blue;
    @FXML private ImageView id_logo;
    @FXML private AnchorPane id_background;
    @FXML private TextField lbl_nickname;

    private GameServlet gameServlet;
    
    @FXML
    void play(ActionEvent event) {
        String nickname = lbl_nickname.getText().trim();
        System.out.println("Nickname: " + nickname);
        if (checkNickname(nickname) == false) return;

        id_nickname_setted.setText(lbl_nickname.getText());
        btn_play.setVisible(false);
        lbl_nickname.setVisible(false);
        
        gameServlet.startGame();
    }

    @FXML
    void initialize() {
        gameServlet = new GameServlet(id_current_score, id_last_score, id_best_score, 
                                      id_background, id_green, id_red, id_yellow, id_blue, id_logo, lbl_nickname);
        id_green.setOnMouseClicked(_ -> gameServlet.handleColorClick(1));
        id_red.setOnMouseClicked(_ -> gameServlet.handleColorClick(2));
        id_yellow.setOnMouseClicked(_ -> gameServlet.handleColorClick(3));
        id_blue.setOnMouseClicked(_ -> gameServlet.handleColorClick(4));
    }

    public boolean checkNickname(String nickname) {
        if (nickname.isEmpty()) {
        	GameServlet.showAlert(AlertType.WARNING, "ATTENZIONE", "Errore nickname",
                    "Devi inserire un nickname per poter giocare.");
            return false;
        } else if (!nickname.matches("^[A-Za-z][A-Za-z0-9_]*[A-Za-z]$")) {
            GameServlet.showAlert(AlertType.WARNING, "ATTENZIONE", "Errore nickname",
                    "Il nickname deve iniziare e finire con una lettera e può contenere solo lettere, numeri e underscore.");
            return false;
		} else if (nickname.length() < 3 || nickname.length() > 15) {
			GameServlet.showAlert(AlertType.WARNING, "ATTENZIONE", "Errore nickname",
					"Il nickname deve essere lungo tra 3 e 15 caratteri.");
			return false;
		}
        return true;
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
    
    public void resetColor() {
        id_green.setOpacity(0.3);
        id_red.setOpacity(0.3);
        id_yellow.setOpacity(0.3);
        id_blue.setOpacity(0.3);
    }
    
    public String getChiamata(String id, String nickname, int colore) throws IOException {
        String urlString = "http://localhost:8080/reaioneaacatena/giocatore?"
                + "id=" + id
                + "&nickname=" + nickname
                + "&colore=" + colore;

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String serverResponse = response.toString();

        return serverResponse;
    }
}