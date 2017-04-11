package ua.com.aveshcher.whatsaroundandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import ua.com.aveshcher.whatsaroundandroid.R;

public class MainActivity extends AppCompatActivity {
    public final static String CATEGORY = "CATEGORY";
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void findPlaces(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("CATEGORY",category);
        startActivity(intent);
    }

    public void selectCategory(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if(checked){
            Button button = (Button) findViewById(R.id.find_places_button);
            button.setEnabled(true);
        }
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.food:
                if (checked)
                    category = "food";
                    break;
            case R.id.sights:
                if (checked)
                    category = "sights";
                    break;
            case R.id.random:
                if (checked)
                    category = "random";
                break;
            case R.id.historic:
                if (checked)
                    category = "historic";
                break;
        }
    }

    public void openSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
