/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serversideimagesharer;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
/**
 *
 * @author Family
 */
public class ServerSideImageSharer {
    private static final int PORT = 1234;
    private static DatagramSocket datagramSocket;
    private static DatagramPacket inPacket, outPacket;
    private static byte[] buffer;
    static BufferedImage BImage = null;
    static ArrayList<BufferedImage> ImageArrayList = new ArrayList<>();
   
//    public static void main(String[] args) {
//        
//    }
    
    public static void init(){
        System.out.println("Starting the server now...\n");
        try {
            //Step 1.This is how you open a port.
            datagramSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            System.out.println("Couldn't connect to port.");
            System.exit(1);
        }
        
        try {
            String messageIn, messageOut;
            int numMessages = 0;
            //This while loop is created new DatagramPacket 
            //objects and receiving them as they come in
            do {
                //Step 2. Creating a buffer.
                buffer = new byte[65000]; 
                //Step 3. Creating a packet.
                inPacket = new DatagramPacket(
                        buffer, buffer.length); 
                //Step 4. The datagram (server)socket takes the packet here.
                datagramSocket.receive(inPacket);	
                
                //Step 5. InetAddress holds the inPacket address
                InetAddress clientAddress
                        = inPacket.getAddress();
                //Step 5. Client port is an int.
                //These functions are taking port 1234, 
                //and the address where the server is created.
                int clientPort = inPacket.getPort();		
                
                //messageIn is the string message created by 
                //inPacket's getData function. We should probably try to recieve
                //an image instead of a string. maybe even store it into an array
                //or make an array of images for every user object.
                buffer = inPacket.getData();
                byteToFile("imgFromClient.jpg",inPacket.getData()); 
//                messageIn = new String(inPacket.getData(), 0,
//                        inPacket.getLength());	//Step 6.

                System.out.println("Buffered Image received.");
                numMessages++;
                //ImageArrayList.add(BImage);
                byte[] outArrayBytes = Arrays.copyOfRange(inPacket.getData(), 0, 33000);
//                messageOut = ("Message " + numMessages
//                        + ": " + messageIn);
                //This datagram packet is being created as a resposnse to the client.
                //The server and client program are connected to same port
                //and thereby communicating.
                //The client will recieve the outPacket because the destination
                //port and address are a part of the header.
                outPacket = new DatagramPacket(
                        outArrayBytes,
                        outArrayBytes.length,
                        clientAddress,
                        clientPort);		//Step 7.
                //Step 8.
                //Now, we send the packet.
                datagramSocket.send(outPacket);	
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally //If exception thrown, close connection.
        {
            System.out.println("\n* Closing connection... *");
            datagramSocket.close();				//Step 9.
        }
    }
    
     public static void byteToFile(String fname,byte[] rawBytes) 
    {

            try {
                 FileOutputStream fos = new FileOutputStream(fname);
                 fos.write(rawBytes);
                 fos.close();
           }
          catch(FileNotFoundException ex)   {
                 System.out.println("FileNotFoundException : " + ex);
          }
         catch(IOException ioe)  {
                 System.out.println("IOException : " + ioe);
          }

    }
    
    
}
