package com.example.sanghyunj.speckerapp.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.adapter.TeamListAdapter;
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

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mGoogleMap;
    private ListView mTeamListView;
    // private RecyclerView mTeamRecyclerView;
    private TeamListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Api mApi;
    private ArrayList<SPKMarker> mArrayListMarkers = new ArrayList<>();
    // private HashMap<String, Marker> mHashMarkers;
    private HashMap<Marker, Integer> mReverseHash;
    private ProgressBar mProgressBar;
    private boolean mIdleOn = true;
    private boolean mAnimateCameraByItemTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTeamListView = (ListView) findViewById(R.id.teamlist);

        // mTeamRecyclerView = (RecyclerView) findViewById(R.id.teamlist);
        /* mTeamRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        }); */
        mAdapter = new TeamListAdapter(getApplicationContext(), mArrayListMarkers);
        mTeamListView.setAdapter(mAdapter);
        // mTeamRecyclerView.setAdapter(mAdapter);
        // mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // mTeamRecyclerView.setLayoutManager(mLinearLayoutManager);

        mTeamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SPKMarker item = mArrayListMarkers.get(position);
                LatLng latlng = new LatLng(item.getPosition().getLatitude(), item.getPosition().getLongitude());
                mAnimateCameraByItemTouch = true;
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
            }
        });

        /*
        mTeamRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                View view = rv.findChildViewUnder(e.getX(), e.getY());
                if (view == null) return;
                int position = rv.getChildAdapterPosition(view);
                SPKMarker item = mArrayListMarkers.get(position);
                LatLng latlng = new LatLng(item.getPosition().getLatitude(), item.getPosition().getLongitude());
                mAnimateCameraByItemTouch = true;
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        */

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
        mGoogleMap.setOnMapClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng seoul = new LatLng(37.566535, 126.97796919999996);
        // mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("SPKMarker in Sydney"));
        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 20f));

    }

    // gms coordinate bound (keyword searchteam)

    @Override
    public void onCameraIdle() {
        if (mAnimateCameraByItemTouch) {
            mAnimateCameraByItemTouch = false;
            return;
        }
        if (!mIdleOn) return;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mArrayListMarkers = new ArrayList<>();
        mAdapter.setItems(mArrayListMarkers);
        // mAdapter.setItems(new ArrayList<>());
        mReverseHash = new HashMap<>();
        // mHashMarkers = new HashMap<>();
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
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    Marker marker = mGoogleMap.addMarker(markerOptions);
                                    mArrayListMarkers.add(spkmarker);
                                    // mAdapter.addItem(spkmarker);
                                    mReverseHash.put(marker, i);
                                    // mHashMarkers.put(Long.toString(spkmarker.getTimestamp()), marker);
                                }
                                mAdapter.setItems(mArrayListMarkers);
                                mAdapter.notifyDataSetChanged();
                                // mTeamRecyclerView.getAdapter().notifyDataSetChanged();
                                // Log.d("Markers", "adapter size: " + mTeamRecyclerView.getAdapter().getItemCount());
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            });
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                    e.printStackTrace();
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                });
    }

    @Override
    public void onMapClick(LatLng latlng) {
        mIdleOn ^= true;
        mTeamListView.setVisibility(mIdleOn ? ListView.VISIBLE : ListView.GONE);
        // mTeamRecyclerView.setVisibility(mIdleOn ? RecyclerView.VISIBLE : RecyclerView.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // int position = mReverseHash.get(marker);
        // mArrayListMarkers.get(position)
        // return true;
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
