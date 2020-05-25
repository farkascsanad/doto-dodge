package application;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	// YOUTUBE VIDEO
	// https://youtu.be/I3ZeN_SvzkM?t=479
	@Override
	public void start(Stage primaryStage) {
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			BorderPane root = new Center();
			Scene scene = new Scene(root, 250, 250);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.setAlwaysOnTop(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
