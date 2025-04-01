package client;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        String response = null;
        try {
            while (true) {
                response = in.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (out != null) {
                out.close();
            }
        }

    }
}
