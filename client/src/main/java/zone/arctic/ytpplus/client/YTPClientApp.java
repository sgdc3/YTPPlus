package zone.arctic.ytpplus.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class YTPClientApp extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage bigBoy) throws Exception {
        Pane mainPane = FXMLLoader.load(YTPClientApp.class.getClassLoader().getResource("FXML.fxml"));
        bigBoy.setScene(new Scene(mainPane));
        bigBoy.show();
        bigBoy.setTitle("YTP+ [beta]");
        bigBoy.setResizable(false);
        // THIS SUCKS!
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        bigBoy.setX((screenBounds.getWidth() - bigBoy.getWidth()) / 2); 
        bigBoy.setY((screenBounds.getHeight() - bigBoy.getHeight()) / 2);  
    }
}
