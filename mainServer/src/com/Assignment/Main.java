package com.Assignment;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {

            MainServer mainServer = new MainServer(6000, 5);
            mainServer.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
