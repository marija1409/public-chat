package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerThread extends Thread {
    public static List<String> users = Collections.synchronizedList(new ArrayList<>());
    public static List<String> history = Collections.synchronizedList(new ArrayList<>());

    private Socket socket;
    private ArrayList<ServerThread> clients;
    private BufferedReader in;
    private PrintWriter out;
    private String[] nono = {"verybadword", "evenworseword", "unimaginableyuckiness"};
    private String filteredMessage = "";
    private String formattedMessage = "";
    private String username = "";
    private char[] arr;
    private String[] splited;

    public ServerThread(Socket socket, ArrayList<ServerThread> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String fromClient = in.readLine();
                System.out.println(fromClient);

                if (fromClient.startsWith(">")) {
                    fromClient = fromClient.substring(1);
                    System.out.println(fromClient);

                    for (String forbiddenWord : nono) {
                        if (fromClient.contains(forbiddenWord)) {
                            filteredMessage = "";
                            splited = fromClient.split("\\s+");
                            System.out.println("Filtering message...");

                            for (String s : splited) {
                                if (s.contains(forbiddenWord)) {
                                    arr = s.toCharArray();
                                    for (int i = 1; i < arr.length - 1; i++) {
                                        arr[i] = '*';
                                    }
                                    s = String.valueOf(arr);
                                }
                                filteredMessage += s + " ";
                            }
                            fromClient = filteredMessage.trim();
                        }
                    }

                    formattedMessage = java.time.LocalDateTime.now() + " - " + username + ": " + fromClient;
                    history.add(formattedMessage);
                    System.out.println("Current history: " + history);

                    for (ServerThread st : clients) {
                        if (!st.username.isEmpty()) {
                            st.out.println(formattedMessage);
                        }
                    }
                } else {
                    while (users.contains(fromClient)) {
                        out.println("ERROR- Username already exists. Please enter new username: ");
                        fromClient = in.readLine();
                        if (fromClient.startsWith(">")) {
                            fromClient = fromClient.substring(1);
                        }
                    }
                    username = fromClient;
                    users.add(fromClient);
                    System.out.println("Current users: " + users);

                    for (ServerThread st : clients) {
                        st.out.println(fromClient + " joined the server");
                    }
                    out.println("Welcome to public chat room!");

                    if (history.size() > 100) {
                        history.remove(0);
                    }
                    for (String message : history) {
                        out.println(message);
                    }
                }
                System.out.println("Server received new client");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}