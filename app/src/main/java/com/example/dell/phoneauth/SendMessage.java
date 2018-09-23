package com.example.dell.phoneauth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessage extends AppCompatActivity {
    Button send;
    EditText phone_Number, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        send = (Button) findViewById(R.id.Send_msg_Btn);
        phone_Number = (EditText) findViewById(R.id.EditText_PhoneNumber);
        message = (EditText) findViewById(R.id.MainActivity_Message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_Num = phone_Number.getText().toString();
                String send_msg = message.getText().toString();
                try {
                    SmsManager sms = SmsManager.getDefault(); // using android
                    sms.sendTextMessage(phone_Num, null, send_msg, null, null); // adding number and text
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Sms not Send", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            };
        });
    }

}