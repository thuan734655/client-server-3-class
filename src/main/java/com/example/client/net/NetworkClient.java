package com.example.client.net;

import com.example.common.protocol.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkClient {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread listenerThread;
    private final Gson gson = new Gson();

    private Consumer<Message<?>> onMessage;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setOnMessage(Consumer<Message<?>> onMessage) {
        this.onMessage = onMessage;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        listenerThread = new Thread(this::listenLoop, "ServerListener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Message<?> msg = gson.fromJson(line, Message.class);
                if (onMessage != null) onMessage.accept(msg);
            }
        } catch (Exception ignored) {
        }
    }

    public synchronized void send(Object message) {
        writer.println(message);
    }

    public <T> void sendMessage(Message<T> msg) {
        send(gson.toJson(msg));
    }
}
