package com.thomosim.consentcoin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private TextInputEditText textInputEditText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textInputEditText = findViewById(R.id.textInputEditText);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("test");
        read();
    }

    public void write(View view) {
        String test = textInputEditText.getText().toString();
        databaseReference.push().setValue(test);
        textInputEditText.setText("");
    }

    public void read() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                textView.append("Key: " + dataSnapshot.getKey() + "   Value: " + dataSnapshot.getValue() + "\n");
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReference.addChildEventListener(childEventListener);
    }
}
