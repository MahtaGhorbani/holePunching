package com.Assignment;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    ServerSocket serverSocket;
    int port;
    ExecutorService pool;
    Map<String,Client> clients ;


    public MainServer(int port,int threadNum) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(threadNum);
        clients = new HashMap<>();
    }

    public void run() throws IOException {
        while (true) {
            System.out.println("Server Socket starts listening");
            Socket connectionSocket = serverSocket.accept();
            ServerThread serverThread = new ServerThread("S1",connectionSocket, (HashMap) clients);
            pool.execute(serverThread);
        }
    }

}
