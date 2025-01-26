package client.backend;

import java.io.*;
import java.net.Socket;

public class CredentialClient {
    private String serverAddress;
    private int serverPort;

    public CredentialClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String register(String email, String password, boolean isAdmin) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeUTF("register");
            out.writeUTF(email);
            out.writeUTF(password);
            out.writeUTF(isAdmin ? "true" : "false");
            return in.readUTF();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String login(String email, String password) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeUTF("login");
            out.writeUTF(email);
            out.writeUTF(password);
            return in.readUTF();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}

