package client.terminal;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2025;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(in.readUTF());

            while (true) {
                System.out.println("Enter command (register/login/update/exit): ");
                String command = console.readLine().toLowerCase();

                if (command.equals("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                out.writeUTF(command);

                // Handle user input based on command
                if (command.equals("register")) {
                    System.out.print("Enter email: ");
                    out.writeUTF(console.readLine());
                    System.out.print("Enter password: ");
                    out.writeUTF(console.readLine());
                    System.out.print("Register as admin? (yes/no): ");
                    out.writeUTF(console.readLine().equalsIgnoreCase("yes") ? "true" : "false");
                } else if (command.equals("login")) {
                    System.out.print("Enter email: ");
                    out.writeUTF(console.readLine());
                    System.out.print("Enter password: ");
                    out.writeUTF(console.readLine());
                } else if (command.equals("update")) {
                    System.out.print("Enter email: ");
                    out.writeUTF(console.readLine());
                    System.out.print("Enter old password: ");
                    out.writeUTF(console.readLine());
                    System.out.print("Enter new password: ");
                    out.writeUTF(console.readLine());
                } else {
                    System.out.println("Invalid command!");
                    continue;
                }

                // Read server.server response
                System.out.println("Server: " + in.readUTF());


            }
        } catch (SocketException e) {
            System.err.println("Connection was closed by the server.server: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
