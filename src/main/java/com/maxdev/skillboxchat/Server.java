package com.maxdev.skillboxchat;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import androidx.core.util.Pair;
import androidx.core.util.Consumer;


public class Server {
    //VARIABLES
    private WebSocketClient client;
    private Map<Long, String> names = new ConcurrentHashMap<>();
    private Consumer<Pair<String, String>> onMessageReceived;

    //CONSTRUCTOR
    public Server(Consumer<Pair<String, String>> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }
    //METHODS
    public void connect() {
        URI addr;
        try {
            Log.i("Server"," connect to uri: " + MainActivity.getAppContext().getString(R.string.serverUri) );
            addr = new URI(MainActivity.getAppContext().getString(R.string.serverUri));
            Log.i("Server"," uri: " + addr );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;

        }
        client = new WebSocketClient(addr) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("SERVER", "Connected to server");
            }

            @Override
            public void onMessage(String json) {
                int type = Protocol.getType(json);
                if (type == Protocol.MESSAGE) {
                    displayIncoming(Protocol.unpackMessage(json));
                }
                if (type == Protocol.USER_STATUS) {
                    updateStatus(Protocol.unpackStatus(json));
                }
                Log.i("SERVER", "Got a message: " + json);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("SERVER", "Connection closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.e("SERVER", "onError", ex);
            }
        };

        client.connect();
    }

    public void disconnect() {
        client.close();
    }

    public void sendMessage(String text) {
        Protocol.Message mess = new Protocol.Message(text);
        if (client != null && client.isOpen()) {
            client.send(Protocol.packMessage(mess));
        }
    }

    public void sendName(String name) {
        Protocol.UserName userName = new Protocol.UserName(name);
        if (client != null && client.isOpen()) {
            client.send(Protocol.packName(userName));
        }
    }

    private void updateStatus(Protocol.UserStatus status) {
        Protocol.User u = status.getUser();
        if (status.isConnected()) {
            names.put(u.getId(), u.getName());
        } else {
            names.remove(u.getId());
        }
    }

    private void displayIncoming(Protocol.Message message) {
        String name = names.get(message.getSender());
        if (name == null) {
            name = "Unnamed";
        }

        onMessageReceived.accept(
                new Pair<>(name, message.getEncodedText())
        );
    }

    //CLASSES
}
