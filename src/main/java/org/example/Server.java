package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 12345;
    private static int clientCounter = 1;
    private static final List<ClientHandler> activeClients = new ArrayList<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[SERVER] Сервер запущений на порту: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientName = "client-" + clientCounter++;
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientName);
                activeClients.add(clientHandler);
                pool.execute(clientHandler);
                System.out.println("[SERVER] " + clientName + " успішно підключився");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        activeClients.remove(clientHandler);
        System.out.println("[SERVER] " + clientHandler.getClientName() + " відключився");
    }
}

class ClientHandler implements Runnable {

    private Socket clientSocket;
    private String clientName;
    private LocalDateTime connectionTime;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket, String clientName) {
        this.clientSocket = clientSocket;
        this.clientName = clientName;
        this.connectionTime = LocalDateTime.now();
    }

    @Override
    public void run() {
        try (var in = new java.util.Scanner(clientSocket.getInputStream())) {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Ви підключені як " + clientName);
            while (in.hasNextLine()) {
                String command = in.nextLine();
                if (command.equalsIgnoreCase("exit")) {
                    Server.removeClient(this);
                    break;
                }
                out.println("Невідома команда: " + command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDateTime getConnectionTime() {
        return connectionTime;
    }
}