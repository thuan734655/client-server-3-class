package com.example.client;

import com.example.client.net.NetworkClient;
import com.example.client.ui.MainFrame;

import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 5555;
        NetworkClient client = new NetworkClient(host, port);
        client.connect();
        SwingUtilities.invokeLater(() -> new MainFrame(client).setVisible(true));
    }
}
