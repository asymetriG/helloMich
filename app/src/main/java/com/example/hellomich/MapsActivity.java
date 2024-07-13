package com.example.hellomich;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hellomich.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ActivityResultLauncher<String> permissionLauncher;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    LocationManager locationManager;
    User currentUser = User.getCurrentUser();
    boolean isActiveSession;
    boolean againJoin;
    String senderEmail;
    String receiverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
            if (addresses != null) {
                String cityName = addresses.get(0).getLocality();
                return cityName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doWhatIWant() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Case 1 (Sıfırdan session açma)
        if (!isActiveSession) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Map<String, Object> data = new HashMap<>();

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.addMarker(new MarkerOptions().position(latLng).title(senderEmail + ", " + getCityName(latLng.latitude, latLng.longitude)));

            data.put("senderEmail", senderEmail);
            data.put("receiverEmail", receiverEmail);
            data.put("createdAt", FieldValue.serverTimestamp());
            data.put("isActive", true);
            data.put("senderLang", location.getLatitude());
            data.put("receiverLang", 1000.0);
            data.put("senderLong", location.getLongitude());
            data.put("receiverLong", 1000.0);
            String docName = senderEmail + "-" + receiverEmail;
            String senderText = "Sender : " + senderEmail;
            String receiverText = "Receiver : " + receiverEmail;
            binding.senderTextView.setText(senderText);
            binding.recieverTextView.setText(receiverText);

            firebaseFirestore.collection("sessions").document(docName).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(MapsActivity.this, "Data Setted", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            if (againJoin) {

                String docname = senderEmail + "-" + receiverEmail;
                final double[] senderLang = new double[1];
                final double[] senderLong = new double[1];


                firebaseFirestore.collection("sessions").document(docname).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot ds = task.getResult();
                            Map<String, Object> data = ds.getData();
                            senderLang[0] = (double) data.get("senderLang");
                            senderLong[0] = (double) data.get("senderLong");

                            String senderText = "Sender : " + senderEmail;
                            String receiverText = "Receiver : " + receiverEmail;
                            binding.senderTextView.setText(senderText);
                            binding.recieverTextView.setText(receiverText);


                            LatLng latLng = new LatLng(senderLang[0], senderLong[0]);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            mMap.addMarker(new MarkerOptions().position(latLng).title(senderEmail + ", " + getCityName(senderLang[0], senderLong[0])));
                        }
                    }
                });

            } else {

                String docname = senderEmail + "-" + receiverEmail;
                final double[] senderLang = new double[1];
                final double[] senderLong = new double[1];

                firebaseFirestore.collection("sessions").document(docname).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot ds = task.getResult();
                            Map<String, Object> data = ds.getData();
                            data.replace("isActive",false);

                            senderLang[0] = (double) data.get("senderLang");
                            senderLong[0] = (double) data.get("senderLong");

                            LatLng latLng = new LatLng(senderLang[0], senderLong[0]);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(senderEmail + ", " + getCityName(senderLang[0], senderLong[0])));

                            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }

                            Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            LatLng newLatLang = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLang));
                            mMap.addMarker(new MarkerOptions().position(newLatLang).title(receiverEmail + ", " + getCityName(newLatLang.latitude, newLatLang.longitude)));

                            data.replace("receiverLang",myLocation.getLatitude());
                            data.replace("receiverLong",myLocation.getLongitude());

                            String senderText = "Sender : " + senderEmail;
                            String receiverText = "Receiver : " + receiverEmail;
                            binding.senderTextView.setText(receiverText);
                            binding.recieverTextView.setText(senderText);


                            firebaseFirestore.collection("sessions").document(docname).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MapsActivity.this,"Session Sonlandı",Toast.LENGTH_LONG).show();
                                }
                            });

                            firebaseFirestore.collection("sessions").document(docname).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MapsActivity.this,"Session Silindi",Toast.LENGTH_SHORT).show();
                                }
                            });

                            firebaseFirestore.collection("oldSessions").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MapsActivity.this,"Session oldSessionsa eklendi",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Intent intent = getIntent();
        if (intent.getBooleanExtra("showOldSession", false)) {
            Toast.makeText(MapsActivity.this,"bura girdi",Toast.LENGTH_SHORT).show();
            String acreatedAt = intent.getStringExtra("createdAt");
            String areceiverEmail = intent.getStringExtra("receiverEmail");
            String asenderEmail = intent.getStringExtra("senderEmail");
            double areceiverLang = intent.getDoubleExtra("receiverLang", 0.0);
            double areceiverLong = intent.getDoubleExtra("receiverLong", 0.0);
            double asenderLang = intent.getDoubleExtra("senderLang", 0.0);
            double asenderLong = intent.getDoubleExtra("senderLong", 0.0);

            binding.recieverTextView.setText("Sender : " + areceiverEmail);
            binding.senderTextView.setText("Receiver : " + asenderEmail);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Toast.makeText(MapsActivity.this,asenderLang + " " + asenderLong,Toast.LENGTH_SHORT).show();

            LatLng latLng1 = new LatLng(asenderLang, asenderLong);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
            mMap.addMarker(new MarkerOptions().position(latLng1).title(asenderEmail + ", " + getCityName(latLng1.latitude, latLng1.longitude)));

            LatLng latLng2 = new LatLng(areceiverLang, areceiverLong);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
            mMap.addMarker(new MarkerOptions().position(latLng2).title(areceiverEmail + ", " + getCityName(latLng2.latitude, latLng2.longitude)));

        } else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(),"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Request Permission
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                } else {
                    //Request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }

            } else {

                doWhatIWant();
            }
        }

    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean isGranted) {
                if (isGranted) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    doWhatIWant();
                    Toast.makeText(MapsActivity.this,"Sorun yok",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapsActivity.this, "Permission needed for location", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
