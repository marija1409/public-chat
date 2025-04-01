package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    public static final int PORT = 3001;

    public static void main(String[] args) throws IOException {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket("localhost", PORT);
            String username = "";
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String input, a, b;
            Scanner scanner = new Scanner(System.in);

            ClientThread clientThread = new ClientThread(socket);
            Thread thread = new Thread(clientThread);
            thread.start();

            while(true){
                if (username.length() < 1){
                    System.out.println("Please enter username: ");
                    input = scanner.nextLine();
                    while (input.length() < 1){
                        System.out.println("Username cant be blank");
                        input = scanner.nextLine();
                    }
                    username = input;
                    out.println(input);
                }else{
                    input = scanner.nextLine();
                    while (input.length() < 1){
                        input = scanner.nextLine();
                    }
                    out.println(">" + input);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        }
    }
}
