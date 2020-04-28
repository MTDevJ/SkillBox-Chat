package com.maxdev.skillboxchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    RecyclerView chatWindow;
    Button sendButton;
    EditText inputMessage;

    MessageController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatWindow = findViewById(R.id.chatWindow);
        sendButton = findViewById(R.id.sendMessage);
        inputMessage = findViewById(R.id.inputMessage);

        controller = new MessageController();

        controller.setIncomingLayout(R.layout.message)
                .setOutgoingLayout(R.layout.message)
                .setMessageTextId(R.id.messgeText)
                .setMessageTimeId(R.id.messageDate)
                .setUserNameId(R.id.userId)
                .appendTo(chatWindow,this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputMessage.getText().toString().isEmpty())
                    controller.addMessage(
                            new MessageController.Message(
                                    inputMessage.getText().toString(),
                                    "Max",
                                    true
                            )
                    );
                inputMessage.setText("");
            }
        });
    }
}
