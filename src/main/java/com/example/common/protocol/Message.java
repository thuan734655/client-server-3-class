package com.example.common.protocol;

public class Message<T> {
    private String action; // e.g., create, read_all, update, delete, ping, broadcast
    private T data;        // payload for requests/responses
    private String status; // ok / error
    private String error;  // error message if any

    public Message() {}

    public Message(String action, T data) {
        this.action = action;
        this.data = data;
    }

    public Message(String action, T data, String status, String error) {
        this.action = action;
        this.data = data;
        this.status = status;
        this.error = error;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
