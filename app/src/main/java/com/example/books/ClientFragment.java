package com.example.books;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ClientFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    DatabaseHelper myDB;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client, container, false);
        myDB=new DatabaseHelper(getActivity(), "app");
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.reasons, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        Button button = (Button) view.findViewById(R.id.btn_add);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selection = spinner.getSelectedItem().toString();
                EditText n = getActivity().findViewById(R.id.et_name);
                EditText d = getActivity().findViewById(R.id.et_day);
                EditText a = getActivity().findViewById(R.id.et_amount);
                if((n.getText().toString().equals("") || a.getText().toString().equals("")) || (d.getText().toString().equals("") || selection.equals("Reason for payment"))){

                }
                else {
                    String name = n.getText().toString();
                    String amount = a.getText().toString();
                    String date = d.getText().toString();
                    String[] vals = {name, date, amount};
                    myDB.doUpdate("Insert into clients(name, day, amount) values (?,?,?);", vals);
                    a.setText("");
                    n.setText("");
                    d.setText("");
                }
            }
        });

        return view;
    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String text = parent.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
