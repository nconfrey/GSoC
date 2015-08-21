import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.sound.sampled.*;
import java.util.jar.*;
import javax.net.ssl.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
 
public final class FileHostingServer {
ArrayList<String> listSong = new ArrayList<String>();
ArrayList<PrintWriter> userList;
 
private class ClientHandler implements Runnable {
BufferedReader reader;
SSLSocket sock;
public ClientHandler(SSLSocket clientSocket, PrintWriter writer) {
    try {
        userList = new ArrayList<PrintWriter>();
        userList.add(writer);
        sock = clientSocket;
        InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(isReader);
    }
    catch(Exception ffaf) {ffaf.printStackTrace();}
 
}
public void run() {
String message = null;
    try {
        while((message = reader.readLine()) != null) {
            if (message.equals("requestSongs")) {
                System.out.println("Sending...");
                    System.out.println("listSong.size() = " + listSong.size());
                    for (String x : listSong) {
                    System.out.println("Sent : " + x);
                    sendUser(x, userList);
                    }
                System.out.println("Stopped.");
                sendUser("Stop", userList);
            }
            //else if (message.startsWith("Request ")) {

            message = "C:\\Users\\Nick\\Music\\Youtubles\\RoadTrip\\Lift.wav";//message.replaceFirst("Request ", "");
            
            Path path = Paths.get(message);
            byte[] data = Files.readAllBytes(path);

            /* 
            File file = new File(message);
            InputStream fileStream = new FileInputStream(file);
            System.out.println("Requested : " + message);
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
            System.out.println("TOO LARGE - ERROR");
            }
            byte[] bytes = new byte[(int)length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=fileStream.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
            }
            if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+ file.getName());
            }
            fileStream.close();
            */
            String value = new String(data);
            sendUser("FileUpload", userList);
            //sendUser(value, userList);
            
            sendUser("Done", userList);
            //}
        }
    }
    catch (Exception ffa) {ffa.printStackTrace();}
}
}
public static void main(String args[]) {
new FileHostingServer().go();
}
 
private void go() {
try {
File songList = new File("SongList.txt");
String song = null;
FileReader songReader = new FileReader(songList);
    BufferedReader songStream = new BufferedReader(songReader);
while ((song = songStream.readLine()) != null) {
    System.out.println(song);
    listSong.add(song);
}
}
catch (Exception uin) {uin.printStackTrace();}
 
try {
SSLServerSocketFactory ssocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
SSLServerSocket serverSock = (SSLServerSocket) ssocketFactory.createServerSocket(5001);
final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
serverSock.setEnabledCipherSuites(enabledCipherSuites);
    while (true) {
    SSLSocket fileSocket = (SSLSocket) serverSock.accept();
    PrintWriter writer = new PrintWriter(fileSocket.getOutputStream());
    Thread t = new Thread(new ClientHandler(fileSocket, writer));
        t.start();
    }
}
catch (Exception ef) {
ef.printStackTrace();
}
}
 
private void sendUser(String message, ArrayList array) {
Iterator it = array.iterator();
try {
PrintWriter writer = (PrintWriter) it.next();
writer.println(message);
writer.flush();
}
catch (Exception exaf) {
exaf.printStackTrace();
}
}
 
}