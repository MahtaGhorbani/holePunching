package com.Assignment;

import java.io.*;
import java.net.*;

public class Main {
    private static boolean isConnectionEstablished = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("main executed");

        //Emulate multiple clients
        Client client1 = new Client("TEST_CLIENT_1",65000,56000);
        Client client2 = new Client("CLIENT_TEST_2",65001,56001);

        client1.setOtherClientName("CLIENT_TEST_2");
        client2.setOtherClientName("TEST_CLIENT_1");

        new Thread(client1).start();
        new Thread(client2).start();

        //Wait while both client will check that connection is established
        Thread.sleep(2000);
        System.out.println();

        if (isConnectionEstablished) {
            client1.close();
            client2.close();
            //Emulate Chatting between two clients
            client1.sendMessageToClient("Hi Second client");
            Thread.sleep(100);
            client2.sendMessageToClient("Halo, First One");
            Thread.sleep(100);
            client1.sendMessageToClient("How are you client2");
            Thread.sleep(100);
            client2.sendMessageToClient("I'm worked fine client1, and you");
            Thread.sleep(100);
            client1.sendMessageToClient("Yes i'm worked fine also");
            Thread.sleep(100);
            client2.sendMessageToClient("cool, great work");
            Thread.sleep(100);
        }
    }
    public static class Client implements Runnable{
        private String cName;
        private int cPortUdp;
        private int cPortTcp;

        private DatagramSocket udpsocket;
        private Socket tcpsocket;

        private String otherClientName;
        private InetSocketAddress otherAddress;

        private BufferedReader reader;
        private BufferedWriter writer;

        private boolean otherFound = false;

        Client(String name,int udpPort,int tcpPort) throws IOException {
            cPortTcp = tcpPort;
            cPortUdp = udpPort;
            this.cName = name;
        }

        public void setOtherClientName(String otherClientName) {
            this.otherClientName = otherClientName;
        }

        @Override
        public void run() {
            System.out.println(cName + " run executed");

            try {
                tcpsocket = new Socket("localhost",6000, InetAddress.getByName("localhost"),cPortTcp);
                udpsocket = new DatagramSocket(cPortUdp);
                reader = new BufferedReader(new InputStreamReader(tcpsocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(tcpsocket.getOutputStream()));
                System.out.println(cName + " socket is initialized");
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                System.out.println(sendMessage("CLIENT_REGISTR;" + cName + ";"+ InetAddress.getByName("localhost")+";"+ udpsocket.getLocalPort()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            getOtherClientAddress(otherClientName);
            new Thread(new Message_Listener()).start();

            while ( !isConnectionEstablished){
                try {
                    String msg = "CHECK_CLIENT_C";
                    byte[] buffer = msg.getBytes();

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    packet.setSocketAddress(otherAddress);

                    udpsocket.send(packet);
                } catch (Exception e) {
                    System.out.println("Failed to send message \n" + e);
                }
            }
        }
        private void getOtherClientAddress(String otherClient){
            boolean notfound = true;
            System.out.println(otherFound);
            while (!otherFound ){
                String response = sendMessage("GET_ADDRESS;" + otherClient);
                //System.out.println(response);
                if(!response.equals("not found") && !response.contains("Failed to send message\n")) {
                    System.out.println(response);
                    otherFound = true;
                    String IP = response.split(":")[1].split("/")[1];
                    String Port = response.split(":")[2];
                    otherAddress = new InetSocketAddress(IP, Integer.parseInt(Port));
                }else{
                    continue;
                }
            }
        }
        private String sendMessage(String msg) {
            try {
                writer.write(msg+"\n");
                writer.flush();
                String response = reader.readLine();
                return response;
            } catch (Exception e) {
                return  ("Failed to send message\n "+ e);
            }
        }

        public void sendMessageToClient(String msg) {
            try {
                msg = cName + "::" + msg;
                byte[] buffer = msg.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                packet.setSocketAddress(otherAddress);

                udpsocket.send(packet);
            } catch (Exception e) {
                System.out.println("Failed to send message\n"+ e);
            }
        }
        public void close(){
            try {
                writer.write("EXIT\n");
                writer.flush();
                reader.close();
                writer.close();
                tcpsocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private class Message_Listener implements Runnable {

            Message_Listener(){
                System.out.println(cName+"listener initialized");
            }
            //Port Listener that will receive all data
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    udpsocket.receive(packet);
                    if (new String(packet.getData()).contains("CHECK_CLIENT_C")) {
                        isConnectionEstablished = true;
                        otherAddress = new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());
                        System.out.println("connection is established");
                    }else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("Cannot establish connection with otherClient \n"+ e);
                    return;
                }

                while(true) {
                    try {
                        buffer = new byte[1024];
                        packet = new DatagramPacket(buffer, buffer.length);
                        udpsocket.receive(packet);
                        String msg = new String(packet.getData());
                        System.out.println(cName + " < " + msg);
                    } catch (Exception e) {
                        System.out.println("Cannot get Message \n" + e);
                    }
                }
            }
        }
    }
}
