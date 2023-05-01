package com.example.cmpsc475projectwarrenroadcapgabele;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences sharedPref;
    private EditText usernameText;
    private EditText passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        changeTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    public void login(View view){
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        if((username.equals("Warren") && password.equals("password")) || (username.equals("Gabe") && password.equals("password"))){
            Intent intent = new Intent(this, DecisionActivity.class);
            usernameText.setText("");
            passwordText.setText("");
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Incorrect Username/Password", Toast.LENGTH_SHORT).show();
        }
    }
    public void openSettings(MenuItem item){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        changeTheme();
        setContentView(R.layout.activity_main);
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
