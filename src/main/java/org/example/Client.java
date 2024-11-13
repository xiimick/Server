package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_ADDRESS = "10.10.40.65";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             Scanner in = new Scanner(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("[CLIENT] Підключено до сервера");
            new Thread(() -> {
                while (in.hasNextLine()) {
                    System.out.println(in.nextLine());
                }
            }).start();

            while (true) {
                String command = scanner.nextLine();
                out.println(command);
                if (command.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}