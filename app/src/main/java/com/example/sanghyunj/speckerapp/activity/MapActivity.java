package com.example.sanghyunj.speckerapp.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Response.GetMarkerResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.MarkerPosition;
import com.example.sanghyunj.speckerapp.retrofit.Response.SPKMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap mGoogleMap;
    private Api mApi;
    private HashMap<String, Marker> mHashMarkers;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mApi = Api.retrofit.create(Api.class);
        mProgressBar = new ProgressBar(this);
        mProgressBar.setIndeterminate(true);
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
        mGoogleMap = googleMap;

        mGoogleMap.setOnCameraIdleListener(this);

        // Add a marker in Sydney and move the camera
        LatLng seoul = new LatLng(37.566535, 126.97796919999996);
        // mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("SPKMarker in Sydney"));
        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 20f));
    }

    @Override
    public void onCameraIdle() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mHashMarkers = new HashMap<>();
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        FirebaseAuth.getInstance().getCurrentUser().getToken(true)
                .addOnCompleteListener((@NonNull Task<GetTokenResult> task) -> {
                    if (!task.isSuccessful()) return;
                    String token = task.getResult().getToken();
                    double latitudeA = Math.min(bounds.northeast.latitude, bounds.southwest.latitude);
                    double longitudeA = Math.min(bounds.northeast.longitude, bounds.southwest.longitude);
                    double latitudeB = Math.max(bounds.northeast.latitude, bounds.southwest.latitude);
                    double longitudeB = Math.max(bounds.northeast.longitude, bounds.southwest.longitude);
                    mApi.getMarker(token, latitudeA, longitudeA, latitudeB, longitudeB)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe((GetMarkerResponse response) -> {
                                if (!response.getResult().equals("ok")) {
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    return;
                                }
                                ArrayList<SPKMarker> markers = response.getMarkers();
                                for (int i = 0; i < markers.size(); i++) {
                                    SPKMarker spkmarker = markers.get(i);
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    MarkerPosition position = spkmarker.getPosition();
                                    markerOptions.position(new LatLng(position.getLatitude(), position.getLongitude()))
                                            .title(spkmarker.getTitle())
                                            .snippet(spkmarker.getSnippet())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                    Marker marker = mGoogleMap.addMarker(markerOptions);
                                    mHashMarkers.put(Long.toString(spkmarker.getTimestamp()), marker);
                                }
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            });
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                    e.printStackTrace();
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                });
    }
}
