package ua.com.aveshcher.whatsaroundandroid.activity;

import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import ua.com.aveshcher.whatsaroundandroid.R;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;
import ua.com.aveshcher.whatsaroundandroid.request.RequestManager;
import ua.com.aveshcher.whatsaroundandroid.service.GPSService;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public final static String CATEGORY = "CATEGORY";
    private String category;
    private int radius;
    private int refreshTime;
    private BroadcastReceiver broadcastReceiver;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Integer.valueOf(sharedPref.getString("search_radius", "494"));
        refreshTime = Integer.valueOf(sharedPref.getString("refresh_time", "120"));
//        Toast.makeText(getApplicationContext(),
//                "search_radius: " + radius, Toast.LENGTH_LONG).show();
        locationTextView = (TextView) findViewById(R.id.location_text_view);

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
//                    Toast.makeText(getApplicationContext(),"recieved smth", Toast.LENGTH_LONG).show();
//                    locationTextView.append("\n" +intent.getExtras().get("latitude") + " " + intent.getExtras().get("longitude"));
                    HashSet<Place> diffPlaces = (HashSet<Place>) intent.getExtras().get("new_places");
                    String info = "";
                    for(Place p : diffPlaces){
                        info += p.getName() + " " + p.getAddress() + "\n";
                    }
                    locationTextView.append("\n" + info);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void findPlaces(View view) {

        RequestManager requestManager = new RequestManager();
        requestManager.receiveJSON(new RequestManager.VolleyCallback() {
            @Override
            public void onSuccess(Set<Place> places) {
                HashSet<Place> places1 = (HashSet<Place>) places;
                Log.d("MAIN_ACTIVITY", "PLACES FROM INTENT: " + places1.size());
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("new_places", places1);
                startActivity(intent);
            }
        }, getApplicationContext(),50.5233873,30.6165221,radius,category);
    }

    public void monitorPlaces(View view) {
        Intent intent = new Intent(this, GPSService.class);
        intent.putExtra("CATEGORY",category);
        startService(intent);
    }

    public void selectCategory(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if(checked){
            Button findButton = (Button) findViewById(R.id.find_places_button);
            Button monitorButton = (Button) findViewById(R.id.monitor_places_button);
            findButton.setEnabled(true);
            monitorButton.setEnabled(true);
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
