package com.example.fab.nytimesapp.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.fab.nytimesapp.Article;
import com.example.fab.nytimesapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    Spinner spinner;
    EditText etdate;
    CheckBox art,sport,fashion;
    Button valider;
    Calendar calendar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        sharedPreferences = FilterActivity.this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        spinner = findViewById(R.id.OrderSpinner);
        etdate = findViewById(R.id.etDatePicker);
        valider = findViewById(R.id.btValider);
        art = findViewById(R.id.cbArt);
        sport = findViewById(R.id.cbSport);
        fashion = findViewById(R.id.cbFashion);


        //setting up etdate picker
        calendar = Calendar.getInstance();
        etdate.setText(sharedPreferences.getString("date", calendar.getTime().toString()));

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
                formatLabel(etdate);
            }
        };
        etdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(FilterActivity.this,date1,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(calendar.DAY_OF_MONTH)).show();
            }
        });


        // Setting up spinner
        spinner = findViewById(R.id.OrderSpinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.SpinArray,android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(sharedPreferences.getInt("order", 0));


        art.setChecked(sharedPreferences.getBoolean("arts", false));
        fashion.setChecked(sharedPreferences.getBoolean("fashion", false));
        sport.setChecked(sharedPreferences.getBoolean("sports", false));
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date;
                String order;
                date = etdate.getText().toString().replace("/","");

                if(spinner.getSelectedItem().toString().equals("Newest")){
                    order = "newest";
                }
                else {
                    order = "oldest";
                }
                SearchActivity.params.put("begin_date",date);
                SearchActivity.params.put("sort",order);

                String fq = "";
                if (art.isChecked()){
                    fq = fq + " \"arts\"";
                }
                else{
                    if (fq.contains(" \"arts\"")){
                        fq = fq.replace(" \"arts\"", "");
                    }
                }
                if (fashion.isChecked()){
                    fq = fq + " \"fashion & style\"";
                }
                else{
                    if (fq.contains(" \"fashion & style\"")){
                        fq = fq.replace(" \"fashion & style\"", "");
                    }
                }
                if (sport.isChecked()){
                    fq = fq + " \"sports\"";
                }
                else{
                    if (fq.contains(" \"sports\"")){
                        fq = fq.replace(" \"sports\"", "");
                    }
                }
                if(!TextUtils.isEmpty(fq)){
                    SearchActivity.params.put("fq", "news_desk:("+fq.trim()+")");
                }

                editor.putBoolean("sports", sport.isChecked());
                editor.putBoolean("fashion", fashion.isChecked());
                editor.putBoolean("arts", art.isChecked());
                editor.putString("date", etdate.getText().toString());
                editor.putInt("order", spinner.getSelectedItemPosition());
                editor.apply();

            }
        });
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                //Article article = (Article) articles.get(position);
                startActivity(intent);
            }
        });

    }

    private void formatLabel(EditText editText) {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }



}
