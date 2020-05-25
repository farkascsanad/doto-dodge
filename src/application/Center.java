package application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Center extends BorderPane {

	private Button start;

	private Label mousePosi = new Label();

	private Button testMousePosition;
	private int x1, x2, y1, y2;

	private TextField tx1 = new TextField("1920");
	private TextField tx2 = new TextField("100");
	private TextField ty1 = new TextField("1080");
	private TextField ty2 = new TextField("10");

	private ImageView imageView = new ImageView();
	private ImageView imageViewReference = new ImageView();

	private Label result = new Label();
	private boolean goingToFind = false;
	private Label resultString = new Label();

	public Center() {
		start = new Button("Start");
		start.setOnAction(event -> buttonSaveClicked());
		testMousePosition = new Button("Mouse posi test(10s)");

		testMousePosition.setOnAction(event -> testMouse());
		HBox hb_btn = new HBox(start, testMousePosition);
		this.setCenter(hb_btn);

		VBox v = new VBox();
		v.getChildren().add(mousePosi);
		v.getChildren().add(new HBox(tx1, ty1));
		v.getChildren().add(new HBox(tx2, ty2));
		this.setTop(v);

		HBox images1 = new HBox(new Label("R :"), imageViewReference);
		HBox images2 = new HBox(new Label("C :"), imageView);
		HBox images3 = new HBox(new Label("OpenCV"), result,resultString);
		VBox vimages = new VBox(images1, images2, images3);
		images3.setSpacing(1);
		this.setBottom(vimages);
	}

	private Object testMouse() {
		// TODO Auto-generated method stub

		mousePosi.setText("Mi az anyádat nyomkodol");
		testMousePosition.setDisable(true);

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// for (int i = 0; i < 100 * 100; i++) {
		// try {
		// PointerInfo a = MouseInfo.getPointerInfo();
		// Point b = a.getLocation();
		// int x = (int) b.getX();
		// int y = (int) b.getY();
		// Platform.runLater(new Runnable() {
		// @Override
		// public void run() {
		// mousePosi.setText(x + " " + y);
		// }
		// });
		// Thread.sleep(10);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }).start();

		return null;
	}

	private Object buttonSaveClicked() {
		// TODO Auto-generated method stub
		System.out.println("cicaicai");
		try {
			takePrintSCreen();
		} catch (HeadlessException | IOException | AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void testPrint() throws IOException, AWTException {
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rectangle = new Rectangle(resolution);
		Robot robot = new Robot();
		System.out.println(rectangle.toString());
		BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
		// g.drawImage(bufferedImage.getScaledInstance(bufferedImage.getWidth(),
		// bufferedImage.getHeight(), Image.SCALE_DEFAULT), 0, 0, null);
		File out = new File("image.png");
		ImageIO.write(bufferedImage, "png", out);
	}

	private void handleOpenCVResult(double result) {
		if(goingToFind){
			return;
		}
		if (result > 0.95) {
			resultString.setText("OK");
			return;
		}
		goingToFind = true;
		resultString.setText("Volt kilengés!");
		Sound.playSound();

	}

	private void takePrintSCreen() throws IOException, HeadlessException, AWTException {

		try {
			Thread.sleep(3500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {

				boolean defaultDone = false;
				while (true) {
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					double width = screenSize.getWidth();
					double height = screenSize.getHeight();

					x1 = Integer.parseInt(tx1.getText());
					x2 = Integer.parseInt(tx2.getText());
					y1 = Integer.parseInt(ty1.getText());
					y2 = Integer.parseInt(ty2.getText());

					BufferedImage image;
					try {
						image = new Robot().createScreenCapture(new Rectangle(x1, y1));

						// boolean write = ImageIO.write(image, "png", new
						// File("screenshot.png"));
						// System.out.println(width + " +" + height);

//						image = image.getSubimage(x1 - x2, y1 - x2, x2, x2);
//						image = image.getSubimage(0, (int) x2 / 2, (int) x2 / 2, (int) x2 / 2);
						image = image.getSubimage(1818, 1011, 35, 35);
						System.out.println(x1 + " " + y1 + ":" + x2);

						boolean write = ImageIO.write(image, "png", new File("screenshot.png"));
						if (!defaultDone) {
							ImageIO.write(image, "png", new File("default_screenshot.png"));
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							ImageIO.write(image, "png", os);
							InputStream is = new ByteArrayInputStream(os.toByteArray());

							Image i = new Image(is);
							defaultDone = true;
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									imageViewReference.setImage(i);
								}
							});
						}

						// HistogramMatcher.histo("screenshot.png");
						double compare = HistogramMatcher.compare("screenshot.png", "default_screenshot.png");

						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ImageIO.write(image, "png", os);
						InputStream is = new ByteArrayInputStream(os.toByteArray());

						Image i = new Image(is);

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								imageView.setImage(i);
								result.setText("" + compare);
								handleOpenCVResult(compare);
								if (Math.random() < 0.1) {
									testMousePosition.setText(Kotorek.generateString());
								}

							}
						});
						Thread.sleep(100);
						// break;
					} catch (AWTException | InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
}
