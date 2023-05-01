package com.example.cmpsc475projectwarrenroadcapgabele;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DecisionActivity extends AppCompatActivity
{
    public SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceStete){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        changeTheme();
        super.onCreate(savedInstanceStete);
        setContentView(R.layout.activity_choose);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ImageView orderpic = (ImageView)findViewById(R.id.orderpic);
        TextView orderpictext = (TextView) findViewById(R.id.orderpictext);
        ImageView historypic = (ImageView) findViewById(R.id.historypic);
        TextView historytext = (TextView) findViewById(R.id.historytext);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    public void changeTheme(){
        boolean preferenceValue = sharedPref.getBoolean("dark_theme_checkbox", false);
        if(preferenceValue){
            Log.d("Changing Theme", "Dark");
            setTheme(R.style.AppTheme_Dark);
        }
        else{
            Log.d("Changing Theme", "Light");
            setTheme(R.style.AppTheme);
        }
    }
    public void openSettings(MenuItem item){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startOrder(View view){
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);

    }
    public void viewHistory(View view){
        Intent intent = new Intent(this, ViewOrdersActivity.class);
        startActivity(intent);
    }

    public void showMenu(View view){
        Intent intent = new Intent(this, ViewActivity.class);
        startActivity(intent);
    }
}
