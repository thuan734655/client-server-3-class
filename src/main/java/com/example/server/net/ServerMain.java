package com.example.server.net;

import com.example.common.model.Tenant;
import com.example.common.protocol.Message;
import com.example.server.controller.TenantController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMain {
    public static final int PORT = 5555;

    private final TenantController controller = new TenantController();
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        new ServerMain().start();
    }

    public void start() throws Exception {
        System.out.println("Server starting on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler, "ClientHandler-" + socket.getPort()).start();
            }
        }
    }

    private void broadcastUpdate(String updateType) {
        Message<Object> msg = new Message<>("broadcast", Map.of("type", updateType));
        String json = gson.toJson(msg);
        for (ClientHandler ch : clients) {
            ch.send(json);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final BufferedReader reader;
        private final PrintWriter writer;

        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());
        }

        void send(String json) {
            writer.println(json);
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    handleMessage(line);
                }
            } catch (Exception e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
                clients.remove(this);
                System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
            }
        }

        private void handleMessage(String json) {
            try {
                Type mapType = new TypeToken<Message<Object>>() {}.getType();
                Message<Object> msg = gson.fromJson(json, mapType);
                String action = msg.getAction();
                switch (action) {
                    case "read_all" -> {
                        List<Tenant> tenants = controller.listTenants();
                        Message<List<Tenant>> resp = new Message<>("read_all", tenants, "ok", null);
                        send(gson.toJson(resp));
                    }
                    case "create" -> {
                        Tenant t = gson.fromJson(gson.toJson(msg.getData()), Tenant.class);
                        Tenant created = controller.createTenant(t);
                        Message<Tenant> resp = new Message<>("create", created, "ok", null);
                        send(gson.toJson(resp));
                        broadcastUpdate("created");
                    }
                    case "update" -> {
                        Tenant t = gson.fromJson(gson.toJson(msg.getData()), Tenant.class);
                        boolean ok = controller.updateTenant(t);
                        Message<Map<String, Object>> resp = new Message<>("update", Map.of("success", ok), ok ? "ok" : "error", ok ? null : "Update failed");
                        send(gson.toJson(resp));
                        if (ok) broadcastUpdate("updated");
                    }
                    case "delete" -> {
                        Map<?,?> m = gson.fromJson(gson.toJson(msg.getData()), Map.class);
                        int id = ((Number)m.get("user_id")).intValue();
                        boolean ok = controller.deleteTenant(id);
                        Message<Map<String, Object>> resp = new Message<>("delete", Map.of("success", ok), ok ? "ok" : "error", ok ? null : "Delete failed");
                        send(gson.toJson(resp));
                        if (ok) broadcastUpdate("deleted");
                    }
                    case "ping" -> {
                        Message<String> resp = new Message<>("pong", "pong", "ok", null);
                        send(gson.toJson(resp));
                    }
                    default -> {
                        Message<String> resp = new Message<>(action, null, "error", "Unknown action");
                        send(gson.toJson(resp));
                    }
                }
            } catch (Exception e) {
                Message<String> err = new Message<>("error", null, "error", e.getMessage());
                send(gson.toJson(err));
            }
        }
    }
}
