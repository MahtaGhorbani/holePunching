package com.Assignment;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ServerThread implements Runnable {
    String name;
    BufferedReader reader;
    BufferedWriter writer;
    Socket socket;
    Map<String,Client> clients ;


    ServerThread(String name, Socket socket,HashMap clients) throws IOException {
        this.socket = socket;
        this.name = name;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clients = clients;
    }

    @Override
    public void run() {
        System.out.println(name + " started");
        boolean exit = false;
        //CLIENT_REGISTR;myuserName;IP;Port
        //GET_ADDRESS;RequestUsername
        while (true){
            try {
                System.out.println(name + " is READY TO GET PACKET");
                String msg = reader.readLine();
                System.out.println(name + " < " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " - " + msg);

                switch (msg.split(";")[0]) {
                    case "GET_ADDRESS":
                        String requested = msg.split(";")[1];
                        System.out.println(name + " GetAddress of "+ requested);
                        if(clients.containsKey(requested)){
                            String address = clients.get(msg.split(";")[1]).getAddresses().get(0).getAddress();
                            String port = String.valueOf(clients.get(msg.split(";")[1]).getAddresses().get(0).getPort());
                            writer.write(msg.split(";")[1] +":"+ address + ":" + port + "\n");
                            writer.flush();
                        }else{
                            writer.write("not found"+"\n");
                            writer.flush();
                        }
                        break;

                    case "CLIENT_REGISTR":
                        System.out.println(name + " Registration of " + msg.split(";")[1]);
                        clientRegistration( msg.split(";")[1], msg.split(";")[2], msg.split(";")[3]);
                        writer.write("Welcome "+msg.split(";")[1]+"\n");
                        writer.flush();
                        break;

                    case "EXIT":
                        exit = true;
                        break;
                }
            } catch (Exception e) {
                System.out.println("packet cannot be received \n" +e);
            }
            if (exit){
                break;
            }
        }
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void clientRegistration(String clientName, String address, String port) {
        Address newAddress = new Address(address, port);
        Client client = new Client();
        client.putAddress(newAddress);

        clients.put(clientName, client);

        System.out.println(clientName + " - registered");
    }


}

