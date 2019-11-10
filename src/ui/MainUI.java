package ui;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.JVault;

public class MainUI extends Application {
	
	private static final Logger LOGGER = Logger.getLogger(MainUI.class.getSimpleName());
	
	private static final String APP_TITLE = "JVault";
	private static final double HEIGHT_SCENE = 200;
	private static final double WIDTH_SCENE = 600;
	private static final double MIN_HEIGHT_STAGE = 200;
	private static final double MIN_WIDTH_STAGE = 600;
	private static final double MAX_HEIGHT_STAGE = 200;
	private static final double MAX_WIDTH_STAGE = 600;
	private static final double OPENING_X = 500;
	private static final double OPENING_Y = 500;
	
	private Button buttonEncrypt;
	private Button buttonDecrypt;
	private Pane rootPane;
	private Stage mainStage;
	private Scene scene;
	
	private File file;

	@Override
	public void start(Stage primaryStage) throws Exception {
		LOGGER.info("Starting MainUI.....");
		initStage(primaryStage);
		
	}
	
	public static void main(String[] args) {
		LOGGER.info("Launching MainUI......");
		launch(args);
	}
	
	private void initStage(Stage primaryStage) {
		
		LOGGER.info("Initializing components of MainUI......");
		
		mainStage = primaryStage;
		buttonEncrypt = new Button("Encrypt");
		buttonDecrypt = new Button("Decrypt");
		rootPane = new StackPane();
		
		VBox vBox = new VBox();
		TextFlow textFlow = new TextFlow();
		Text part1 = new Text("Welcome to ");
		Text part2 = new Text("JVault");
		part2.setStyle("-fx-font-weight: bold");
		Text part3 = new Text(" - a file vault using Password-based Encryption");
		textFlow.getChildren().addAll(part1, part2, part3);
		textFlow.setTextAlignment(TextAlignment.CENTER);
		vBox.getChildren().add(textFlow);
		VBox.setMargin(textFlow, new Insets(0,0,20,0));
		
		
		HBox hBox = new HBox();
		hBox.getChildren().add(buttonEncrypt);
		hBox.getChildren().add(buttonDecrypt);
		hBox.setAlignment(Pos.CENTER);
		HBox.setMargin(buttonEncrypt, new Insets(0,20,0,0));
		HBox.setMargin(buttonDecrypt, new Insets(0,0,0,20));
		vBox.getChildren().add(hBox);
		VBox.setMargin(hBox, new Insets(20,0,0,0));
		vBox.setAlignment(Pos.CENTER);
		
		addOnClickListeners();
		
		rootPane.getChildren().add(vBox);
		scene = new Scene(rootPane, WIDTH_SCENE, HEIGHT_SCENE);
		mainStage.setScene(scene);
		mainStage.setX(OPENING_X);
		mainStage.setY(OPENING_Y);
		mainStage.setMinHeight(MIN_HEIGHT_STAGE);
		mainStage.setMinWidth(MIN_WIDTH_STAGE);
		mainStage.setMaxHeight(MAX_HEIGHT_STAGE);
		mainStage.setMaxWidth(MAX_WIDTH_STAGE);
		mainStage.setTitle(APP_TITLE);
		mainStage.show();
	}
	
	private void addOnClickListeners() {
		LOGGER.info("Adding OnClick Listeners");
		if (buttonEncrypt != null && buttonDecrypt != null) {
			buttonEncrypt.setOnAction((e) -> {
				openEncryptDialog();
			});
		}
		if (buttonDecrypt != null && buttonDecrypt != null) {
			buttonDecrypt.setOnAction((e) -> {
				openDecryptDialog();
			});
		}
	}
	
	private void openEncryptDialog() {
		Stage dialogEncryptionStage = new Stage();
		dialogEncryptionStage.initStyle(StageStyle.UTILITY);
		dialogEncryptionStage.initModality(Modality.WINDOW_MODAL);
		dialogEncryptionStage.initOwner(mainStage);
		dialogEncryptionStage.setTitle(APP_TITLE);
		
		// Parent Layout
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		
		// The layout that contains only Browse file option
		HBox labelBrowse = new HBox();
		Label label = new Label("Please select a file to encrypt");
		label.setWrapText(true);
		
		Button browseFileButton = new Button("Browse");
		browseFileButton.setOnAction((e) -> {
			FileChooser fileChooser = new FileChooser();
			file = fileChooser.showOpenDialog(dialogEncryptionStage);
			if (file != null && file.exists()) {
				label.setText("Selected File: " + file.getAbsolutePath());
				label.requestLayout();
			} else {
				LOGGER.info("No file selected");
			}
			
		});
		labelBrowse.getChildren().addAll(label, browseFileButton);
		label.setMaxWidth(150);
		HBox.setMargin(label, new Insets(0,10,0,0));
		HBox.setMargin(browseFileButton, new Insets(0,0,0,10));
		labelBrowse.setAlignment(Pos.CENTER);
		vBox.getChildren().add(labelBrowse);
		
		// The layout that contains password field
		HBox passwordHBox = new HBox();
		passwordHBox.setAlignment(Pos.CENTER);
		Label passwordLabel = new Label("Password");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Please enter a password to encrypt");
		passwordHBox.getChildren().addAll(passwordLabel, passwordField);
		HBox.setMargin(passwordLabel, new Insets(0,10,0,0));
		HBox.setMargin(passwordField, new Insets(0,0,0,10));
		vBox.getChildren().addAll(passwordHBox);
		VBox.setMargin(passwordHBox, new Insets(20,0,0,0));
		
		//The layout that contains final action buttons
		HBox buttonsHBox = new HBox();
		buttonsHBox.setAlignment(Pos.CENTER);
		Button encrypt = new Button("Encrypt");
		Button cancel = new Button("Cancel");
		
		encrypt.setOnAction((e) -> {
			if (file == null || !file.exists()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Missing File");
				alert.initOwner(dialogEncryptionStage);
				alert.setContentText("No file selected. Please select a valid file");
				alert.showAndWait();
				return;
			}
			String password = passwordField.getText();
			if (password == null || password.equals("") || password.equals(" ") || password.length() == 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("No Password set");
				alert.initOwner(dialogEncryptionStage);
				alert.setContentText("No Password typed. Please type a password");
				alert.showAndWait();
				return;
			}
			
			LOGGER.info("Starting encryption");
			try {
				JVault.encrypt(file, password);
				LOGGER.info("Successfully encrypted the file: " + file.getName());
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("Successful Encryption");
				alert.initOwner(dialogEncryptionStage);
				alert.setContentText("Successfully encrypted selected file");
				Optional<ButtonType> result = alert.showAndWait();
				
				if (!result.isPresent() || result.get().equals(ButtonType.OK)) {
					file = null;
					dialogEncryptionStage.close();
				}
				
			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
					| IOException e1) {
				// TODO Auto-generated catch block
				LOGGER.info("Something went wrong");
				e1.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Unhandled Error");
				alert.initOwner(dialogEncryptionStage);
				alert.setContentText("Something went wrong while encrypting. Please check the logs");
				alert.showAndWait();
				return;
			}
			
		});
		
		cancel.setOnAction((e) -> {
			if (dialogEncryptionStage != null) {
				dialogEncryptionStage.close();
				file = null;
			}
		});
		buttonsHBox.getChildren().addAll(encrypt, cancel);
		HBox.setMargin(encrypt, new Insets(0,10,0,0));
		HBox.setMargin(cancel, new Insets(0,0,0,10));
		vBox.getChildren().add(buttonsHBox);
		VBox.setMargin(buttonsHBox, new Insets(20,0,0,0));
		
		Scene scene = new Scene(vBox, 500,200);
		
		dialogEncryptionStage.setScene(scene);
		dialogEncryptionStage.showAndWait();
		
	}
	
	private void openDecryptDialog() {
		Stage dialogDecryptionStage = new Stage();
		dialogDecryptionStage.initStyle(StageStyle.UTILITY);
		dialogDecryptionStage.initModality(Modality.WINDOW_MODAL);
		dialogDecryptionStage.initOwner(mainStage);
		dialogDecryptionStage.setTitle(APP_TITLE);
		
		// Parent Layout
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.CENTER);
		
		// The layout that contains only Browse file option
		HBox labelBrowse = new HBox();
		Label label = new Label("Please select a file to decrypt");
		label.setWrapText(true);
		
		Button browseFileButton = new Button("Browse");
		browseFileButton.setOnAction((e) -> {
			FileChooser fileChooser = new FileChooser();
			file = fileChooser.showOpenDialog(dialogDecryptionStage);
			if (file != null && file.exists()) {
				label.setText("Selected File: " + file.getAbsolutePath());
				label.requestLayout();
			} else {
				LOGGER.info("No file selected");
			}
			
		});
		labelBrowse.getChildren().addAll(label, browseFileButton);
		label.setMaxWidth(150);
		HBox.setMargin(label, new Insets(0,10,0,0));
		HBox.setMargin(browseFileButton, new Insets(0,0,0,10));
		labelBrowse.setAlignment(Pos.CENTER);
		vBox.getChildren().add(labelBrowse);
		
		// The layout that contains password field
		HBox passwordHBox = new HBox();
		passwordHBox.setAlignment(Pos.CENTER);
		Label passwordLabel = new Label("Password");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Please enter the password to decrypt");
		passwordHBox.getChildren().addAll(passwordLabel, passwordField);
		HBox.setMargin(passwordLabel, new Insets(0,10,0,0));
		HBox.setMargin(passwordField, new Insets(0,0,0,10));
		vBox.getChildren().addAll(passwordHBox);
		VBox.setMargin(passwordHBox, new Insets(20,0,0,0));
		
		//The layout that contains final action buttons
		HBox buttonsHBox = new HBox();
		buttonsHBox.setAlignment(Pos.CENTER);
		Button encrypt = new Button("Decrypt");
		Button cancel = new Button("Cancel");
		
		encrypt.setOnAction((e) -> {
			if (file == null || !file.exists()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Missing File");
				alert.initOwner(dialogDecryptionStage);
				alert.setContentText("No file selected. Please select a valid file");
				alert.showAndWait();
				return;
			}
			String password = passwordField.getText();
			if (password == null || password.equals("") || password.equals(" ") || password.length() == 0) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("No Password set");
				alert.initOwner(dialogDecryptionStage);
				alert.setContentText("No Password typed. Please type a password");
				alert.showAndWait();
				return;
			}
			
			LOGGER.info("Starting decryption");
			try {
				JVault.decrypt(file, password);
				LOGGER.info("Successfully decrypted the file: " + file.getName());
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("Successful decryption");
				alert.initOwner(dialogDecryptionStage);
				alert.setContentText("Successfully decrypted selected file");
				Optional<ButtonType> result = alert.showAndWait();
				
				if (!result.isPresent() || result.get().equals(ButtonType.OK)) {
					file = null;
					dialogDecryptionStage.close();
				}
				
			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
					| IOException e1) {
				// TODO Auto-generated catch block
				LOGGER.info("Something went wrong");
				e1.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Unhandled Error");
				alert.initOwner(dialogDecryptionStage);
				alert.setContentText("Something went wrong while decrypting. Please check the logs");
				alert.showAndWait();
				return;
			}
			
		});
		
		cancel.setOnAction((e) -> {
			if (dialogDecryptionStage != null) {
				dialogDecryptionStage.close();
				file = null;
			}
		});
		buttonsHBox.getChildren().addAll(encrypt, cancel);
		HBox.setMargin(encrypt, new Insets(0,10,0,0));
		HBox.setMargin(cancel, new Insets(0,0,0,10));
		vBox.getChildren().add(buttonsHBox);
		VBox.setMargin(buttonsHBox, new Insets(20,0,0,0));
		
		Scene scene = new Scene(vBox, 500,200);
		
		dialogDecryptionStage.setScene(scene);
		dialogDecryptionStage.showAndWait();
	}

}
