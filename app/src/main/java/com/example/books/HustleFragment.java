package com.example.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HustleFragment extends Fragment {

    DatabaseHelper myDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hustle, container, false);

        myDB = new DatabaseHelper(getActivity(), "app");

        Button button = (Button) view.findViewById(R.id.btn_submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText d = getActivity().findViewById(R.id.et_date2);
                EditText t = getActivity().findViewById(R.id.et_total);
                if((t.getText().toString().equals("")) || d.getText().toString().equals("")){

                }
                else {
                    String name = "Hustle";
                    String amount = t.getText().toString();
                    String date = d.getText().toString();
                    String[] vals = {name, date, amount};
                    myDB.doUpdate("Insert into clients(name, day, amount) values (?,?,?);", vals);
                    t.setText("");
                    d.setText("");
                }
            }
        });

        return view;
    }
}
