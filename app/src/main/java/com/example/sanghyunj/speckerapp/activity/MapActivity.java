package com.example.sanghyunj.speckerapp.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.adapter.TeamListAdapter;
import com.example.sanghyunj.speckerapp.retrofit.Api;
import com.example.sanghyunj.speckerapp.retrofit.Body.SearchTeamBody;
import com.example.sanghyunj.speckerapp.retrofit.Response.GetMarkerResponse;
import com.example.sanghyunj.speckerapp.retrofit.Response.MarkerPosition;
import com.example.sanghyunj.speckerapp.retrofit.Response.SPKMarker;
import com.example.sanghyunj.speckerapp.retrofit.Response.SearchTeamResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private Api mApi;

    private EditText mEditTextSearch;
    private Button mImageButtonSearch;
    private GoogleMap mGoogleMap;
    private ListView mTeamListView;
    private TeamListAdapter mAdapter;
    private ArrayList<SPKMarker> mArrayListMarkers = new ArrayList<>();
    private HashMap<String, Marker> mReverseHash;
    private Marker mLastClickedMarker = null;
    private ProgressDialog mProgressDialog;
    private boolean mIdleOn = true;
    private boolean mAnimateCameraByItemTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            finish();
        }

        mApi = Api.retrofit.create(Api.class);

        mEditTextSearch = (EditText) findViewById(R.id.et_search);
        mImageButtonSearch = (Button) findViewById(R.id.button_search);
        /*
        mImageButtonSearch.setOnClickListener((View view) -> {
            mFirebaseUser.getToken(true)
                    .addOnCompleteListener((@NonNull Task<GetTokenResult> task) -> {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        String keyword = mEditTextSearch.getText().toString();
                        mApi.searchTeam(token, new SearchTeamBody(keyword))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe((SearchTeamResponse response) -> {
                                    response.getTeams();
                                });
                    })
                    .addOnFailureListener((@NonNull Exception e) -> {

                    });
        });
        */

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTeamListView = (ListView) findViewById(R.id.teamlist);

        mAdapter = new TeamListAdapter(getApplicationContext(), mArrayListMarkers);
        mTeamListView.setAdapter(mAdapter);

        mTeamListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            SPKMarker item = mArrayListMarkers.get(position);
            LatLng latlng = new LatLng(item.getPosition().getLatitude(), item.getPosition().getLongitude());
            Marker marker = mReverseHash.get(Double.toHexString(latlng.latitude) + Double.toHexString(latlng.longitude));
            if (mLastClickedMarker != null) mLastClickedMarker.hideInfoWindow();
            mLastClickedMarker = marker;
            mAnimateCameraByItemTouch = true;
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
            marker.showInfoWindow();
        });

        mApi = Api.retrofit.create(Api.class);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng seoul = new LatLng(37.566535, 126.97796919999996);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f));
    }

    // gms coordinate bound (keyword searchteam)

    @Override
    public void onCameraIdle() {
        if (mAnimateCameraByItemTouch) {
            mAnimateCameraByItemTouch = false;
            return;
        }
        if (!mIdleOn) return;
        mProgressDialog.setTitle("팀 마커를 불러오는 중...");
        mProgressDialog.show();
        mLastClickedMarker = null;
        mArrayListMarkers = new ArrayList<>();
        mAdapter.setItems(mArrayListMarkers);
        mReverseHash = new HashMap<>();
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
                                    mProgressDialog.dismiss();
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
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    Marker marker = mGoogleMap.addMarker(markerOptions);
                                    mArrayListMarkers.add(spkmarker);
                                    String key = Double.toHexString(marker.getPosition().latitude) + Double.toHexString(marker.getPosition().longitude);
                                    mReverseHash.put(key, marker);
                                }
                                mAdapter.setItems(mArrayListMarkers);
                                mAdapter.notifyDataSetChanged();
                                mProgressDialog.dismiss();
                            });
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                    e.printStackTrace();
                    mProgressDialog.dismiss();
                });
    }

    @Override
    public void onMapClick(LatLng latlng) {
        mIdleOn ^= true;
        mTeamListView.setVisibility(mIdleOn ? ListView.VISIBLE : ListView.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mAnimateCameraByItemTouch = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
