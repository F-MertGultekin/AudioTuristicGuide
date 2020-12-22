package com.example.currentplacedetailsonmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;

import android.widget.RadioGroup;
import android.widget.ScrollView;
import java.util.ArrayList;

public class VisitedPlaces extends AppCompatActivity {
    RadioGroup radioGroup;
    ScrollView scrollView;
    ArrayList<String> visitedMarkers = new ArrayList<>();
    ArrayList<String> unvisitedMarkers = new ArrayList<>();

    ArrayList<CheckBox> checkboxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_places);

        ArrayList<String> markerNames = getIntent().getStringArrayListExtra("marker_names");

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle(" Visited Destinations");
        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#000000"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);
        for (int i = 0; i < markerNames.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(i);
            checkBox.setTextColor(Color.BLACK);
            checkBox.setText(markerNames.get(i));

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            radioGroup.addView(checkBox, params);
            checkboxes.add(checkBox);
        }


    }
    public void onBackButtonClick(){
        Intent intent = new Intent(VisitedPlaces.this, MapsActivityCurrentPlace.class);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
    public void onSubmitButtonClick(){
        for (int i = 0; i < checkboxes.size(); i++) {
            boolean isChecked = checkboxes.get(i).isChecked();
            if (isChecked) {
                visitedMarkers.add((String) checkboxes.get(i).getText());
            } else {
                unvisitedMarkers.add((String) checkboxes.get(i).getText());
            }
        }

        Intent intent = new Intent(VisitedPlaces.this, MapsActivityCurrentPlace.class);
        Bundle bundle = new Bundle();

        bundle.putStringArrayList("unvisitedMarkers", unvisitedMarkers);
        bundle.putStringArrayList("visitedMarkers", visitedMarkers);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.visited_places_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackButtonClick();
                return true;
            case R.id.submit:
                onSubmitButtonClick();
                return true;

        }
        return true;
    }
}