package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Main extends Application {
    public static List<String> esDic;
    public static List<String> enDic;

    private static final Thread thread = new Thread(() -> {
        File file = new File(new File("src/dictionary/spanish.txt").getAbsolutePath());
        File enFile = new File(new File("src/dictionary/english.txt").getAbsolutePath());

        esDic = SpellChecker.lda(file);
        enDic = SpellChecker.lda(enFile);

        if (esDic == null || enDic == null) {
            System.err.println("Dictionary is NULL.");
            System.exit(1);
        } else {
            System.out.println("Hello there :D");
        }
    });

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //root.setCursor(new ImageCursor(new Image("/cursor_PNG32.png"), 0, 0));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 400));
        thread.join();
        primaryStage.show();
    }

    public static void main(String[] args) {
        thread.start();
        launch(args);
    }
}
