/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageproject;

import serversideimagesharer.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static serversideimagesharer.ServerSideImageSharer.byteToFile;

/**
 *
 * @author Family
 */
public class ImageProject extends Application {
    private static DatagramSocket datagramSocket;
    private static DatagramPacket inPacket;
    private static byte[] buffer;
    @Override
    public void start(Stage primaryStage) {
        
        
        Label FNameLabel = new Label("First Name:");
        TextField FNameField = new TextField();
        Label LNameLabel = new Label("Last Name:");
        TextField LNameField = new TextField();
        Label IDLabel = new Label("ID:");
        TextField IDField = new TextField();
        Label emailLabel = new Label("email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("password:");
        TextField passwordField = new TextField();
        
        Label confirmLabel = new Label("Test");
        
        BufferedImage BI = null;
        
        
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        ObservableList<String> options = 
            FXCollections.observableArrayList(
            "Download from server",
            "Upload to server");
        final ComboBox comboBox = new ComboBox(options);
        
        Button UploadButton = new Button("Send Image");
        
        
        Button StartServerbtn = new Button("Start Server");
        
        UploadButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                try {
                    FileChooser myfile = new FileChooser();
                    File imageFile = myfile.showOpenDialog(primaryStage);
                    
                    BufferedImage img = ImageIO.read(imageFile);
                    ImageIO.write(img, "png", byteArr);
                    byteArr.toByteArray();
                    
                } catch (IOException ex) {
                    Logger.getLogger(ImageProject.class.getName()).log(Level.SEVERE, null, ex);
                }
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    InetAddress hostAddress = null;
                    try {
                        hostAddress = InetAddress.getLocalHost();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(ImageProject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    DatagramPacket dpOutPacket;
                    dpOutPacket = new DatagramPacket(byteArr.toByteArray(), byteArr.size(), hostAddress, 1234);
                    confirmLabel.setText("!!!!Sending packet!!!!");
                    try {
                        ds.send(dpOutPacket);
                    } catch (IOException ex) {
                        Logger.getLogger(ImageProject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (SocketException ex) {
                    Logger.getLogger(ImageProject.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    buffer = new byte[65000];
                    inPacket = new DatagramPacket(
                        buffer, buffer.length); 
                    ds.receive(inPacket);
                    buffer = inPacket.getData();
                    InetAddress clientAddress
                        = inPacket.getAddress();
                
                int clientPort = inPacket.getPort();		
                
                
                buffer = inPacket.getData();
                
                byteToFile("imgFromServer.jpg",inPacket.getData()); 
                confirmLabel.setText("!!!!!!!CYCLE COMPLETE!!!!!!!");
                } catch (IOException ex) {
                    Logger.getLogger(ImageProject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
//        StartClientbtn.setOnAction(new EventHandler<ActionEvent>() {
//            
//            @Override
//            public void handle(ActionEvent event) {
//                Thread t1 = new Thread(() -> {
//                    UDPEchoClient.init();
//                });  
//                t1.start();
//            }
//        });
        StartServerbtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                Thread t1 = new Thread(() -> {
                    ServerSideImageSharer.init();
                });  
                t1.start();
            }
        });
        
        GridPane root = new GridPane();
        root.getChildren().add(new VBox(10, 
                new HBox(10, FNameLabel,FNameField), 
                new HBox(10, LNameLabel,LNameField),
                new HBox(10, IDLabel,IDField), 
                new HBox(10, emailLabel,emailField),
                new HBox(10, passwordLabel,passwordField),
                new HBox(10, UploadButton,StartServerbtn),
                confirmLabel));
        
        Scene scene = new Scene(root, 400, 350);
        
        primaryStage.setTitle("Image Sharing Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }
    
}
