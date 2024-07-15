package com.example.hellomich;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hellomich.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ActivityResultLauncher<String> permissionLauncher;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private LocationManager locationManager;
    private boolean isActiveSession;
    private boolean againJoin;
    private String senderEmail;
    private String receiverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

        Intent intent = getIntent();
        isActiveSession = intent.getBooleanExtra("isActiveSession", false);
        senderEmail = intent.getStringExtra("senderEmail");
        receiverEmail = intent.getStringExtra("receiverEmail");
        againJoin = intent.getBooleanExtra("againJoin", false);
    }

    private String getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            System.out.println("City not found");
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    private void handleSession() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location == null) {
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.addMarker(new MarkerOptions().position(latLng).title(senderEmail + ", " + getCityName(latLng.latitude, latLng.longitude)));

        Map<String, Object> data = new HashMap<>();
        data.put("senderEmail", senderEmail);
        data.put("receiverEmail", receiverEmail);
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("isActive", true);
        data.put("senderLang", location.getLatitude());
        data.put("receiverLang", 1000.0);
        data.put("senderLong", location.getLongitude());
        data.put("receiverLong", 1000.0);
        String docName = senderEmail + "-" + receiverEmail;

        binding.senderTextView.setText("Sender: " + senderEmail);
        binding.recieverTextView.setText("Receiver: " + receiverEmail);

        firebaseFirestore.collection("sessions").document(docName).set(data).addOnSuccessListener(unused ->
                Toast.makeText(MapsActivity.this, "Data Setted", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();

        if (intent.getBooleanExtra("showOldSession", false)) {

            String acreatedAt = intent.getStringExtra("createdAt");
            String areceiverEmail = intent.getStringExtra("receiverEmail");
            String asenderEmail = intent.getStringExtra("senderEmail");
            double areceiverLang = intent.getDoubleExtra("receiverLang", 0.0);
            double areceiverLong = intent.getDoubleExtra("receiverLong", 0.0);
            double asenderLang = intent.getDoubleExtra("senderLang", 0.0);
            double asenderLong = intent.getDoubleExtra("senderLong", 0.0);

            binding.recieverTextView.setText("Sender: " + areceiverEmail);
            binding.senderTextView.setText("Receiver: " + asenderEmail);



            LatLng latLng1 = new LatLng(asenderLang, asenderLong);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
            mMap.addMarker(new MarkerOptions().position(latLng1).title(asenderEmail + ", " + getCityName(latLng1.latitude, latLng1.longitude)));


            LatLng latLng2 = new LatLng(areceiverLang, areceiverLong);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
            mMap.addMarker(new MarkerOptions().position(latLng2).title(areceiverEmail + ", " + getCityName(latLng2.latitude, latLng2.longitude)));

        } else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Permission needed", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Give Permission", view -> permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else {
                handleSession();
            }
        }
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                handleSession();
                Toast.makeText(MapsActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapsActivity.this, "Permission needed for location", Toast.LENGTH_LONG).show();
            }
        });
    }
}
