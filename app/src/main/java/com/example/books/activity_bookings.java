package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class activity_bookings extends AppCompatActivity {

    DatabaseHelper myDB;

    public void doList(View v) {
        LinearLayout l = findViewById(R.id.BookingView);
        Cursor c = myDB.doQuery("SELECT * from clients");
        while (c.moveToNext()) {
            Button t = new Button(this);
            @SuppressLint("Range") String s = c.getString(c.getColumnIndex("name")) +", " + c.getString(c.getColumnIndex("day"))+ ", "+ "R" +c.getInt(c.getColumnIndex("amount"));
            t.setText(s);
            l.addView(t);
        }
        c.close();
        v.setEnabled(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB=new DatabaseHelper(this, "app");
        setContentView(R.layout.activity_bookings);
    }





}