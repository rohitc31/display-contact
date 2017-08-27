package rohitc.contact_display;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String[] contNames;
    String[] contLatitudes;
    String[] contLongitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Intent intent = getIntent();

        contNames = intent.getStringArrayExtra("com.contactdownload.Names");
        contLatitudes = intent.getStringArrayExtra("com.contactdownload.Latitudes");
        contLongitudes = intent.getStringArrayExtra("com.contactdownload.Longitudes");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng cont1 = new LatLng(Integer.parseInt(contLatitudes[0])/1000000, Integer.parseInt(contLongitudes[0])/1000000);
        mMap.addMarker(new MarkerOptions().position(cont1).title(contNames[0]));
        LatLng cont2 = new LatLng(Integer.parseInt(contLatitudes[1])/1000000, Integer.parseInt(contLongitudes[1])/1000000);
        mMap.addMarker(new MarkerOptions().position(cont2).title(contNames[1]));
        LatLng cont3 = new LatLng(Integer.parseInt(contLatitudes[2])/1000000, Integer.parseInt(contLongitudes[2])/1000000);
        mMap.addMarker(new MarkerOptions().position(cont3).title(contNames[2]));
        LatLng cont4 = new LatLng(Integer.parseInt(contLatitudes[3])/1000000, Integer.parseInt(contLongitudes[3])/1000000);
        mMap.addMarker(new MarkerOptions().position(cont4).title(contNames[3]));
        LatLng cont5 = new LatLng(Integer.parseInt(contLatitudes[4])/1000000, Integer.parseInt(contLongitudes[4])/1000000);
        mMap.addMarker(new MarkerOptions().position(cont5).title(contNames[4]));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cont1));
    }
}
