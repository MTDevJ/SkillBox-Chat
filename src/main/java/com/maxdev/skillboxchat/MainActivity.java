package com.maxdev.skillboxchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static Context appContext;

    private static String myName = "";
    RecyclerView chatWindow;
    private MessageController controller;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.appContext = getApplicationContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = input.getText().toString();
                server.sendName(myName);
            }
        });
        builder.show();

        chatWindow = findViewById(R.id.chatWindow);

        controller = new MessageController();

        controller.setIncomingLayout(R.layout.incoming_message)
                .setOutgoingLayout(R.layout.outcomimg_message)
                .setMessageTextId(R.id.messgeText)
                .setMessageTimeId(R.id.messageDate)
                .setUserNameId(R.id.userName)
                .appendTo(chatWindow,this);

        final EditText inputMessage = findViewById(R.id.inputMessage);
        Button sendButton = findViewById(R.id.sendMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputMessage.getText().toString();
                controller.addMessage(
                        new MessageController.Message(text, myName, true)
                );
                inputMessage.setText("");
                server.sendMessage(text);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controller.addMessage(
                                new MessageController.Message(pair.second, pair.first, false)
                        );
                    }
                });
            }
        });
        server.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        server.disconnect();
    }

    public static Context getAppContext() {
        return MainActivity.appContext;
    }


}
