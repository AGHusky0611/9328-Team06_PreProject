import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class server {
    private static final  String CREDENTIALS = "datafiles/credits.txt";
    private static final int PORT = 2025;
    private Map<String, String> credentials;

    public server(){
        credentials = new HashMap<>();
        loadCredentials();
    }

    public static void main(String[] args) {
        server server = new server();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String connectionTime = LocalDateTime.now().toString();
                System.out.println("Client connected at " + connectionTime);

                new Thread(() -> handleClient(clientSocket, connectionTime)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, String connectionTime) {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

            out.writeUTF("SUCCESS: Connected to server at " + connectionTime);

            String command;
            while ((command = in.readUTF()) != null) {
                switch (command.toLowerCase()) {
                    case "register":
                        handleRegister(in, out);
                        break;
                    case "login":
                        handleLogin(in, out);
                        break;
                    case "update":
                        handleUpdate(in, out);
                        break;
                    default:
                        out.writeUTF("ERROR: Invalid command!");
                }
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        }
    }

    private void handleRegister(DataInputStream in, DataOutputStream out) throws IOException {
        String email = in.readUTF();
        String password = in.readUTF();
        String isAdmin = in.readUTF(); // Expect "true" or "false"

        if (credentials.containsKey(email)) {
            out.writeUTF("ERROR: Email already registered!");
        } else {
            String hashedPassword = hashPassword(password);
            credentials.put(email, hashedPassword + ":" + isAdmin);
            saveCredentials();
            out.writeUTF("SUCCESS: Registered successfully!");
        }
    }

    private void handleLogin(DataInputStream in, DataOutputStream out) throws IOException {
        String email = in.readUTF();
        String password = in.readUTF();

        String hashedPassword = hashPassword(password);
        if (credentials.containsKey(email)) {
            String[] values = credentials.get(email).split(":");
            if (values[0].equals(hashedPassword)) {
                out.writeUTF("SUCCESS: Login successful!");
            } else {
                out.writeUTF("ERROR: Invalid email or password!");
            }
        } else {
            out.writeUTF("ERROR: Invalid email or password!");
        }
    }

    private void handleUpdate(DataInputStream in, DataOutputStream out) throws IOException {
        String email = in.readUTF();
        String oldPassword = in.readUTF();
        String newPassword = in.readUTF();

        String hashedOldPassword = hashPassword(oldPassword);
        if (credentials.containsKey(email)) {
            String[] values = credentials.get(email).split(":");
            if (values[0].equals(hashedOldPassword)) {
                String hashedNewPassword = hashPassword(newPassword);
                credentials.put(email, hashedNewPassword + ":" + values[1]);
                saveCredentials();
                out.writeUTF("SUCCESS: Password updated successfully!");
            } else {
                out.writeUTF("ERROR: Invalid old password!");
            }
        } else {
            out.writeUTF("ERROR: Email not found!");
        }
    }

    private boolean isAdmin(String email) {
        if (credentials.containsKey(email)) {
            String[] values = credentials.get(email).split(":");
            return values.length == 2 && values[1].equals("true");
        }
        return false;
    }

    private void loadCredentials() {
        File file = new File(CREDENTIALS);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        credentials.put(parts[0], parts[1] + ":" + parts[2]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCredentials() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS))) {
            for (Map.Entry<String, String> entry : credentials.entrySet()) {
                String[] values = entry.getValue().split(":");
                writer.write(entry.getKey() + ":" + values[0] + ":" + values[1]);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}

