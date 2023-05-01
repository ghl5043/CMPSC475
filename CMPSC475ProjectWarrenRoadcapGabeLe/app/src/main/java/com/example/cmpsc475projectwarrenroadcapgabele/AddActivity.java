package com.example.cmpsc475projectwarrenroadcapgabele;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenu;
import com.example.cmpsc475projectwarrenroadcapgabele.db.RestaurantMenuDatabase;

public class AddActivity extends AppCompatActivity {
    public SharedPreferences sharedPref;
    EditText name_text;
    EditText description_text;
    EditText price_text;
    private int menu_id;
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        changeTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        menu_id = getIntent().getIntExtra("menu_id", -1);
        name_text = (EditText) findViewById(R.id.name_item);
        description_text = (EditText) findViewById(R.id.describe_item);
        price_text = (EditText) findViewById(R.id.price_item);
        price_text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        price_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.isEmpty()) return;

                // check if the input has more than two decimal places
                int decimalIndex = text.indexOf(".");
                if (decimalIndex >= 0 && text.length() - decimalIndex > 3) {
                    // remove the extra decimal places
                    editable.replace(decimalIndex + 3, text.length(), "");
                }
            }
        });
        if (savedInstanceState == null) {
            if (menu_id != -1) {
                RestaurantMenuDatabase.getMenu(menu_id, menu -> {
                    name_text.setText(menu.item_name);
                    description_text.setText(menu.description);
                    price_text.setText(String.valueOf(menu.price));

                });
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_activity, menu);
        if (menu_id == -1) {
            setTitle("Add Item");
        }
        else {
            menu.getItem(1).setIcon(R.drawable.deletepic);
            menu.getItem(1).setTitle("Delete");
            setTitle("Edit Item");
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case(R.id.cancel):
                if(menu_id!=-1){
                    ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog();
                    confirmDialog.show(getSupportFragmentManager(), "deletionConfirmation");
                }
                else {
                    finish();
                }
                return true;
            case(R.id.submit):
                updateDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    public void deleteRecord() {
        RestaurantMenuDatabase.delete(menu_id);
    }
    private void updateDatabase() {
        RestaurantMenu menu = new RestaurantMenu(menu_id == -1?0:menu_id,
                name_text.getText().toString(),
                description_text.getText().toString(),
                Double.parseDouble(price_text.getText().toString())
                );
        if (menu_id == -1) {
            RestaurantMenuDatabase.insert(menu);
        } else {
            RestaurantMenuDatabase.update(menu);
        }
        finish(); // Quit activity
    }

    public static class ConfirmDeleteDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Delete the Menu Item?")
                    .setMessage("You will not be able to undo the deletion!")
                    .setPositiveButton("Delete",
                            (dialog,id) -> {
                                ((AddActivity) getActivity()).deleteRecord();
                                getActivity().finish();
                            })
                    .setNegativeButton("Return to menu list",
                            (dialog, id) -> getActivity().finish());
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    positiveButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    negativeButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                }
            });
            return dialog;
        }
    }

}
