package inc.sguard.android.com.sguardinc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import inc.sguard.android.com.sguardinc.Model.Guard;

public class UserPickUpLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;
    Button confirm;
    FirebaseUser user;
    DatabaseReference reference;
    AutocompleteSupportFragment pick_up_location, drop_off_location;
    PlacesClient placesClient;
    LatLng pickup = null, dropoff = null;
    ProgressDialog progressDialog;
    String pickup_address = null, dropoff_address = null;
    String distance = "";
    double fare = 0.0, event = 0.0;
    String duration = "";
    ImageView imageView;

    RecyclerView recyclerView;
    ArrayList<Guard> list;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pick_up_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initToolBar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Guard guard = list.get(position);

                final String userid = user.getUid();
                reference = FirebaseDatabase.getInstance().getReference("User").child(userid).child("Events").child("Current");
                final SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Current_Booking", pref.getString("Current_Booking", null));
                hashMap.put("Current_Booking_Number", pref.getString("Current_Booking_Number", null));
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference = FirebaseDatabase.getInstance().getReference("User").child(userid).child("Pickup_Location");
                            Log.i("User id", userid);
                            Log.i("Lat Long", mMap.getCameraPosition().target.toString());

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("pickup_latitude", pref.getString("pickup_latitude", null));
                            hashMap.put("pickup_longitude", pref.getString("pickup_longitude", null));
                            hashMap.put("dropoff_latitude", pref.getString("dropoff_latitude", null));
                            hashMap.put("dropoff_longitude", pref.getString("dropoff_longitude", null));
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reference = FirebaseDatabase.getInstance().getReference("Guard").child(guard.getId()).child("Bookings");
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("Current_Booking", pref.getString("Current_Booking", null));
                                        hashMap.put("Current_Booking_Number", pref.getString("Current_Booking_Number", null));
                                        hashMap.put("Current_book_user", userid);
                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), guard.getName() + " is selected!", Toast.LENGTH_SHORT).show();
                                                   /* Intent intent = new Intent(UserPickUpLocation.this, UserPickUpLocation.class);
                                                    startActivity(intent);
                                                    finish();*/
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Failed Problem Occured", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed Problem Occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        user = FirebaseAuth.getInstance().getCurrentUser();

        imageView = (ImageView) findViewById(R.id.imageview);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        pick_up_location = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.pick_up_location);
        pick_up_location.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        pick_up_location.setHint("Pickup Location");
        pick_up_location.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                confirm.setText(getResources().getString(R.string.confirm));
                pickup = place.getLatLng();
                String address = String.valueOf(place.getAddress());
                Log.i("Place", "From: " + place.getAddress());//get place details here
                pick_up_location.setText(address);
                pickup_address = address;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Error", "An error occurred: " + status);
            }
        });

        drop_off_location = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.drop_off_location);
        drop_off_location.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        drop_off_location.setHint("Dropoff Location");
        drop_off_location.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                confirm.setText(getResources().getString(R.string.confirm));
                // TODO: Get info about the selected place.
                dropoff = place.getLatLng();
                String address = String.valueOf(place.getAddress());
                Log.i("Place", "From: " + place.getAddress());//get place details here
                drop_off_location.setText(address);
                dropoff_address = address;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Error", "An error occurred: " + status);
            }
        });

        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirm.getText().toString().equals(getResources().getString(R.string.confirm))) {
                    if (pickup != null && dropoff != null) {
                        progressDialog = new ProgressDialog(UserPickUpLocation.this);
                        progressDialog.setMessage("Searching for best route...");
                        progressDialog.show();

                        mMap.clear();
                        // mMap.addMarker(new MarkerOptions().position(pickup).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map)));
                        // mMap.addMarker(new MarkerOptions().position(dropoff).title("Dropoff Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pickup));

                        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("pickup_latitude", String.valueOf(pickup.latitude));
                        editor.putString("pickup_longitude", String.valueOf(pickup.longitude));
                        editor.putString("dropoff_latitude", String.valueOf(dropoff.latitude));
                        editor.putString("dropoff_longitude", String.valueOf(dropoff.longitude));
                        editor.commit();

                        String url = getUrl(pickup, dropoff, "mode=driving");
                        Log.d("onMapClick", url.toString());
                        UserPickUpLocation.FetchUrl FetchUrl = new FetchUrl();
                        FetchUrl.execute(url);

                /*final String userid = user.getUid();
                reference = FirebaseDatabase.getInstance().getReference("User").child(userid).child("Pickup_Location");
                Log.i("User id", userid);
                Log.i("Lat Long", mMap.getCameraPosition().target.toString());

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("pickup_latitude", String.valueOf(pickup.latitude));
                hashMap.put("pickup_longitude", String.valueOf(pickup.longitude));
                hashMap.put("dropoff_latitude", String.valueOf(dropoff.latitude));
                hashMap.put("dropoff_longitude", String.valueOf(dropoff.longitude));
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String url = getUrl(pickup, dropoff, "mode=driving");
                            Log.d("onMapClick", url.toString());
                            UserPickUpLocation.FetchUrl FetchUrl = new FetchUrl();
                            FetchUrl.execute(url);
                            // Toast.makeText(getApplicationContext(), "Successfully Done", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed Problem Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter pickup and dropoff location", Toast.LENGTH_SHORT).show();
                    }
                } else if (confirm.getText().toString().equals(getResources().getString(R.string.proceed))) {
                    reference = FirebaseDatabase.getInstance().getReference().child("Guard");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list = new ArrayList<Guard>();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Guard p = dataSnapshot1.getValue(Guard.class);
                                list.add(p);
                            }
                            adapter = new MyAdapter(UserPickUpLocation.this, list);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            toolbar.setTitle(R.string.guard_gallery);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(UserPickUpLocation.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.enter_pickup_loc);

        this.setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.INVISIBLE);
            toolbar.setTitle(R.string.enter_pickup_loc);
        } else {
            Intent intent = new Intent(UserPickUpLocation.this, UserBookOrder.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        imageView.setVisibility(View.INVISIBLE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().isIndoorLevelPickerEnabled();
        mMap.getUiSettings().isTiltGesturesEnabled();
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().isMapToolbarEnabled();
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            UserPickUpLocation.ParserTask parserTask = new ParserTask();
            System.out.println("Display Route" + result);
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("Route ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            LatLng cameraposition = null;
            if (result.size() == 0) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserPickUpLocation.this);//, R.style.AppCompatAlertDialogStyle
                builder.setMessage("No route found, please try again.")
                        .setCancelable(true);
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {
                        distance = (String) point.get("distance");
                        if (point.get("distance").substring(0, point.get("distance").length() - 3).contains(",")) {
                            String filter[] = point.get("distance").substring(0, point.get("distance").length() - 3).split(",");
                            fare = (int) Double.parseDouble(filter[0] + filter[1]);
                            fare = (int) ((fare / 1.609) * 0.25) + 0.80;
                            fare = Math.round((double) fare + 0.80);
                           /* if (fare > 100)
                                fare = 100;*/
                        } else {
                            fare = (int) Double.parseDouble(point.get("distance").substring(0, point.get("distance").length() - 3));
                            fare = (int) ((fare / 1.609) * 0.25);
                            fare = Math.round((double) fare + 0.80);
                            /*if (fare > 100)
                                fare = 100;*/
                        }
                        Log.i("Plotting", "distance" + distance);
                        Log.i("Plotting", "distance value" + fare);
                        continue;
                    } else if (j == 1) {
                        duration = (String) point.get("duration");
                        Log.i("Plotting", "duration" + duration);
                        if (point.get("duration").contains("mins")) {
                            String s[] = point.get("duration").split(" ");
                            double hour = Integer.parseInt(s[0]);
                            double mins = Integer.parseInt(s[2]) / 60;
                            hour = hour + mins;
                            event = Math.round(hour * 5.0);
                            if (event > 100)
                                event = 100;

                        } else {
                            String s[] = point.get("duration").split(" ");
                            double hour = Integer.parseInt(s[0]);
                            event = Math.round(hour * 5.0);
                            if (event > 100)
                                event = 100;
                        }

                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }
            if (lineOptions != null) {
                Log.d("onPostExecute Lines", "onPostExecute lineoptions decoded");
                LinearLayout distanceMarkerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.distance_marker_layout, null);

                distanceMarkerLayout.setDrawingCacheEnabled(true);
                distanceMarkerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                distanceMarkerLayout.layout(0, 0, distanceMarkerLayout.getMeasuredWidth(), distanceMarkerLayout.getMeasuredHeight());
                distanceMarkerLayout.buildDrawingCache(true);

                TextView positionDistance = (TextView) distanceMarkerLayout.findViewById(R.id.positionDistance);
                positionDistance.setText(distance);
                TextView durationtext = (TextView) distanceMarkerLayout.findViewById(R.id.duration);
                durationtext.setText(duration);
                TextView faretext = (TextView) distanceMarkerLayout.findViewById(R.id.fare);

                durationtext.setText(String.valueOf(fare) + " $");
                positionDistance.setText(String.valueOf(event) + " $");

                Bitmap flagBitmap = Bitmap.createBitmap(distanceMarkerLayout.getDrawingCache());
                distanceMarkerLayout.setDrawingCacheEnabled(false);

                imageView.setImageBitmap(flagBitmap);
                imageView.setVisibility(View.VISIBLE);

                mMap.addPolyline(lineOptions);

                /*mMap.addMarker(new MarkerOptions().position(points.get(0)).title(pickup_address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map)));
                mMap.addMarker(new MarkerOptions().position(points.get(points.size() - 1)).title(dropoff_address).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map))).showInfoWindow();
                */
                mMap.addMarker(new MarkerOptions().position(points.get(0)).title(pickup_address));
                mMap.addMarker(new MarkerOptions().position(points.get(points.size() - 1)).title(dropoff_address)).showInfoWindow();
                mMap.getUiSettings().setCompassEnabled(true);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(pickup);
                builder.include(dropoff);
                LatLngBounds bound = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 300));
                confirm.setText(getResources().getString(R.string.proceed));
                Log.d("onPostExecute", "all done");
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getUrl(LatLng origin, LatLng dest, String Mode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = Mode;
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_maps_key);
        return url;
    }

}
