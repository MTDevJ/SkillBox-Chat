package com.maxdev.skillboxchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static Context appContext;

    private static String myName = "";
    RecyclerView chatWindow;
    private MessageController controller;
    private Server server;

    public static Context getAppContext() {
        return MainActivity.appContext;
    }

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
        }, new Consumer<Pair<String, Integer>>() {
            @Override
            public void accept(final Pair<String, Integer> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_view, (ViewGroup) findViewById(R.id.toastView));

                        Toast toast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);

                        TextView text = layout.findViewById(R.id.toastMessageView);
                        text.setText(pair.first + " присоединился к чату!");
                        toast.setGravity(Gravity.BOTTOM, 0, 150);
                        toast.setView(layout);
                        toast.show();

                        LayoutInflater inflaterCounter = getLayoutInflater();
                        View layoutCounter = inflaterCounter.inflate(R.layout.activity_main, (ViewGroup) findViewById(R.id.linearLayout2));

                        TextView counter = layoutCounter.findViewById(R.id.users_counter);
                        counter.setText(getResources().getString(R.string.usersCounter) + pair.second);

                    }
                });
            }
        }, new Consumer<Pair<String, Integer>>() {
            @Override
            public void accept(final Pair<String, Integer> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.activity_main, (ViewGroup) findViewById(R.id.linearLayout2));

                        TextView counter = layout.findViewById(R.id.users_counter);
                        counter.setText(getResources().getString(R.string.usersCounter) + pair.second);

                    }
                });
            }
        }
        );
        server.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        server.disconnect();
    }

}
