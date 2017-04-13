package ua.com.aveshcher.whatsaroundandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import ua.com.aveshcher.whatsaroundandroid.R;
import ua.com.aveshcher.whatsaroundandroid.dto.Place;

import java.util.HashSet;

public class PlaceDetailsActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView distanceTextView;
    private ImageView placeImage;
    private TextView ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        nameTextView = (TextView) findViewById(R.id.name_text_view);
        addressTextView = (TextView) findViewById(R.id.address_text_view);
        distanceTextView = (TextView) findViewById(R.id.distance_text_view);
        placeImage = (ImageView) findViewById(R.id.place_image);
        ratingTextView = (TextView) findViewById(R.id.rating_text_view);

        Intent intent = getIntent();
        Place place = (Place) intent.getExtras().get("place");
        Picasso.with(this).load(place.getPhotoUrl())
                .into(placeImage);

        nameTextView.setText(place.getName());
        addressTextView.setText(place.getAddress());
        distanceTextView.setText(String.valueOf(place.getDistance()));
        ratingTextView.setText(String.valueOf(place.getRating()));
    }
}
