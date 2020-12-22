// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;


 /* if distance between current location and selected marker is less then 1000 meter
 * Audio can be playable.
 * Purpose of the this condition is, provide an audio guide when user go to destination without turist guide as person.
 * People can have listener from museum. But this is not preferable due to corona virus or hygiene condition for some people.
 * We are giving chance to listen those audio guide from personel device.
 * Payment plan can be generable after a while.
 * For example premium plan:
 * All audios can be reachable without distance restriction
 * This is just an idea
 */


//  REFERENCES
 /* To get map and get location,googles MapsActivityCurrentPlace tutorial was used as reference.
  * https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial


  */
//Important
 //API Keys and Restrictions
/* 2 api keys was used in this project

 * 1.to get current location
 *   this api is avaliable only android applications
 *   selected api's are  Geocoding API
                        Geolocation API
                        Distance Matrix API
                        Directions API
                        Maps Elevation API
                        Maps Embed API
                        Maps SDK for Android
                        Places API
                        Roads API

                        ***just Maps SDK for Android ,Geocoding API and Geolocation API are using inside project
                        ***sha key finger print belongs to my personal computer
 *
 *
 * 2. to get direction
 * its restriction is ip addresses.
 **************** IMPORTANT ********************
 * When you test the project ,  IPv4 or IPv6  address should be added.
 * http://console.cloud.google.com/

Selected api's are  Directions API
                    Geocoding API
                    Geolocation API


 */

// Important
/* Current location is now shown in my android virtual device (Nexus 5 API 30).
   My location is also not visible google maps application inside avd.There is a problem with avd.
   But when i tested it on real device,it shows the correct current location
*/

public class MapsActivityCurrentPlace extends AppCompatActivity
        implements OnMapReadyCallback  {

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    /*Keeps the tracks which the distance to current location is less then 1 km
    * playableAudios can be just one MediaPlayer variable
    * it has only one element.
    */
    private ArrayList<MediaPlayer> playableAudios = new ArrayList<MediaPlayer>();


    /* lstSource elements are adding in ReadingData method. It has the name of the turistic-historic destinations.
    *  To adapt listView's order, lstSource has created.
    *  it has same elements with markerNames
    */
    private ArrayList<String>  lstSource=new ArrayList<String>();


    /*markerNames arraylist is using in new intent (VisitedPlaces).
    * This is sended to another activity.
    *
    */
    private ArrayList<String> markerNames= new ArrayList<>();


    //markerList has Marker objects that i created to arrange places.
    private ArrayList<Markers> markersList= new ArrayList<>();

    /*mediaList has all the mediaPlayer objects which are audios for just one language.
    * I found some mp3 in English,Turkish and German.
    * When user select a language in settings,old or default mediaplayer removed in list
    * then selected languages audios is loaded to mediaList. It should be dynamic.Because markers or locations can be added or removed of the data
     */
    private ArrayList<MediaPlayer> mediaList= new ArrayList<>();

    //textViewDistance shows distance between current location and selected marker
    private TextView textViewDistance;


    private ListView listView;
    private SearchView searchView;

    //showMarkerButton show all the markers which database or txt file have.
    private Button showMarkerButton;
    //playAudioButton avaliable only when marker selected and this markers distance to user is less then 1km
    private Button playAudioButton;

    //getDirectionButton brings the direction between selected marker and current location.
    //There are some modes to draw route. I used only car mode.
    private Button getDirectionButton;


    //Polyline declared in global to remove when user request another direction.
    private Polyline polylineFinal;

    //routedMarker is the selected marker. Program uses it in getDirection method.
    private LatLng[] routedMarker = new LatLng[1];

    //coordinates gives Basmane meydanı.
    private LatLng CenterIzmir = new LatLng(38.423733, 27.142826);


    /*to keep settings like language and map style.
    *
    */
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref;

    // mapId become 0 when map style is not dark or its derivatives
    //and it become 1 when map style is dark,aubergine or night
    private int mapId=0;

    //onActivityResult method check if data is coming from second activity (Visited place) or not.
    private int LAUNCH_SECOND_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load settings when application run
         sharedPref = this.getPreferences(Context.MODE_PRIVATE);
         editor = sharedPref.edit();


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.current_place_menu, menu);
        return true;
    }

    //Settings of the application
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* try - catch block was used to control FileNotFoundException.
        *  data reading inside ReadingFile() method and this method calls from languageSelection(not only calls from here)
        **first 3 case checks language,other 5 checks map style,last one steer to VisitedPlaces activity
         */
        switch (item.getItemId()){
            case R.id.Turkish:
                try {
                    languageSelection("turkish");
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            case R.id.English:
                try {
                    languageSelection("english");

                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            case R.id.Deutsch:
                try {
                    languageSelection("deutsch");

                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            /* there are 5 option.
            *  if map id is 0, then text color of the listview become black,
            *  if map id is 1, then text color of the listview become red,
            *
            * this code block save the string value with mapStyle key and after terminating the application.
            * editor.putString("mapStyle","silvermap");
                editor.commit();
            *
            * map style xml files created up to https://mapstyle.withgoogle.com/ this link.
            *
            * in every case, code returns true not to check other cases
             */
            case R.id.silver:
                MapStyleOptions silverMapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.silvermap);
                map.setMapStyle(silverMapStyleOptions);
                editor.putString("mapStyle","silvermap");
                editor.commit();
                mapId=0;
                return true;
            case R.id.retro:
                MapStyleOptions retroMapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.retromap);
                map.setMapStyle(retroMapStyleOptions);
                editor.putString("mapStyle","retromap");
                editor.commit();
                mapId=0;
                return true;
            case R.id.night:
                MapStyleOptions nightMapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.nightmap);
                map.setMapStyle(nightMapStyleOptions);
                editor.putString("mapStyle","nightmap");
                editor.commit();
                mapId=1;
                return true;
            case R.id.dark:
                MapStyleOptions darkMapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.darkmap);
                map.setMapStyle(darkMapStyleOptions);
                editor.putString("mapStyle","darkmap");
                editor.commit();
                mapId=1;
                return true;
            case R.id.aubergine:
                MapStyleOptions aubergineMapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.auberginemap);
                map.setMapStyle(aubergineMapStyleOptions);
                editor.putString("mapStyle","auberginemap");
                editor.commit();
                mapId=1;
                return true;

            //steer to VisitedPlaces activity
            case R.id.visited:
                selectVisitedPlaces();
                checkMediaIsPlaying();
                return true;
        }

        return true;
    }

    /* When new language selected. All the routes,markers is deleting and created once again.
     *
     * check media player pause if media is playing.then clear playableAudio arraylist content
     *
     * releaseMediaPlayer() method stop and release all the audio inside mediaList arraylist.All audios exist inside mediaList arraylist
     *
     * Data from txt files is reading once again.
     *
     * selected language string value is saved with key language.
     */
    public void languageSelection(String language) throws FileNotFoundException {
        map.clear();
        checkMediaIsPlaying();
        releaseMediaPlayer();
        playAudioButton.setEnabled(false);
        getDirectionButton.setEnabled(false);
        readingData(language);
        showMarkerButton.setEnabled(true);
        editor.putString("language",language);
        //                key,      value
        editor.commit();
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        //zoom in zoom out button enabled
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setPadding(0, 0, 0, 600);


        //Load settings
        //map layout preference
        String mapStyle = sharedPref.getString("mapStyle","");
        if(!mapStyle.equals("")) {
            MapStyleOptions aubergineMapStyleOptions = MapStyleOptions.loadRawResourceStyle(this,  getResources().getIdentifier(mapStyle,"raw",getPackageName()));
            map.setMapStyle(aubergineMapStyleOptions);
        }
        //language preference as default
        String language=sharedPref.getString("language","turkish");
        try {
            releaseMediaPlayer();
            readingData(language);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        listView=(ListView)findViewById(R.id.listVieww);
        searchView=(SearchView)findViewById(R.id.searchView);


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make  buttons invisible.
                showMarkerButton.setVisibility(View.INVISIBLE);
                playAudioButton.setVisibility(View.INVISIBLE);
                getDirectionButton.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);

                //delete route from the map if exist.
                if(polylineFinal!=null) {
                    polylineFinal.remove();
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if(newText!=null&&!newText.isEmpty()){
                    List<String> lstFound=new ArrayList<String>();
                    for(String item:lstSource){
                        if(item.contains(newText)){
                            lstFound.add(item);
                        }
                    }
                    //if map id is 0 ,that is map is light then listview textcolor could be black-grey
                    if(mapId==0){
                        ArrayAdapter adapter=new ArrayAdapter(MapsActivityCurrentPlace.this, R.layout.quantum_greyblack1000,R.id.grey_black,lstFound);
                         listView.setAdapter(adapter);
                    }
                    //if map id is 1 ,that is map is dark then listview textcolor could be orange
                    if(mapId==1){
                        ArrayAdapter adapter=new ArrayAdapter(MapsActivityCurrentPlace.this, R.layout.quantum_deeporange,R.id.orange,lstFound);
                        listView.setAdapter(adapter);
                    }
                }
                else{
                    if(mapId==0){
                        ArrayAdapter adapter=new ArrayAdapter(MapsActivityCurrentPlace.this, R.layout.quantum_greyblack1000,R.id.grey_black,lstSource);
                        listView.setAdapter(adapter);
                    }
                    if(mapId==1){
                        ArrayAdapter adapter=new ArrayAdapter(MapsActivityCurrentPlace.this, R.layout.quantum_deeporange,R.id.orange,lstSource);
                        listView.setAdapter(adapter);
                    }
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //buttons becoming visible ,keybord become invisible
                showMarkerButton.setVisibility(View.VISIBLE);
                playAudioButton.setVisibility(View.VISIBLE);
                getDirectionButton.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        //leftmost button showMarkerButton
        showMarkerButton=(Button)findViewById(R.id.button1);
        showMarkerButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               try {
                   //allMarkersVisible() method returns latlng
                   routedMarker =allMarkersVisible(map);

               } catch (FileNotFoundException e) {
                   e.printStackTrace();
               }
           }
       });



        playAudioButton=(Button)findViewById(R.id.btnPlay);
        getDirectionButton=(Button)findViewById(R.id.button);
        getDirectionButton.setEnabled(false);

        getDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clears if route exist to draw new one
                if(polylineFinal!=null)
                {
                    polylineFinal.remove();
                }

                getDirection();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //search clicked list view item
                onStringMatch(parent,position);
                playAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playButtonListener();
                    }
                });

            }
        });
    }

    //Searchs clicked listView item then make selected marker visible
    public void onStringMatch(AdapterView<?> parent,int position){
        LatLng camLatLng = null;

        for (int i = 0; i < markersList.size(); i++){

            //gets selected item as string
            String str=(String) parent.getItemAtPosition(position);

            //if strings match
            if(str.equalsIgnoreCase(markersList.get(i).getName()))
            {

                LatLng place=markersList.get(i).getPosition();
                camLatLng=place;
                routedMarker[0]=place;

                //make getDirectionButton clickable
                getDirectionButton.setEnabled(true);
                //make selected marker visible
                markersList.get(i).getMarker().setVisible(true);

                //get current location
                final LatLng currentLocation=new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                //calculate distance between current location and selected marker
                calculateDistance(currentLocation,place);
                setVisibilityOfMarkers(true,null,place);

                final LatLng[] selectedMarker = new LatLng[1];
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        markerListener(marker,currentLocation,playableAudios,selectedMarker);
                        return false;
                    }
                });
            }
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(camLatLng, 10));
    }
    public void getDirection()
    {

        LatLng mLocation=new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        LatLng destination = new LatLng(routedMarker[0].latitude, routedMarker[0].longitude);
        setVisibilityOfMarkers(true,mLocation,destination);


        List<LatLng> path = new ArrayList();
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("!!!!!!!!!!!!!!!!!!!!!!!For the security concern api key is deleted !!!!!!!!!!!!!!!!!!!!!!!")
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, String.valueOf(mLocation.latitude)+", "+String.valueOf(mLocation.longitude), String.valueOf(routedMarker[0].latitude)+", "+String.valueOf(routedMarker[0].longitude));
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];

                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];

                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();

                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            polylineFinal= map.addPolyline(opts);


        }


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CenterIzmir, 8));


    }
    public void setVisibilityOfMarkers(boolean visibility,LatLng origin,LatLng destination)
    {

        for (int i = 0; i <markersList.size(); i++) {

            if(visibility)
            {
                //sets visible only origin and destination.Actually makes visible only selected marker
                if(markersList.get(i).getPosition().equals(origin)||markersList.get(i).getPosition().equals(destination)){
                    markersList.get(i).getMarker().setVisible(true);
                }
                else
                    {
                        markersList.get(i).getMarker().setVisible(false);
                    }
            }
            else
            {   //makes selected marker invisible
                //i am not sure but this code block  never runs

                if(markersList.get(i).getPosition().equals(origin)||markersList.get(i).getPosition().equals(destination))
                {
                    markersList.get(i).getMarker().setVisible(false);
                }
            }
        }
    }

    //calculates bird fly distance between two location
    public float calculateDistance(LatLng origin,LatLng destination)
    {
        float[] oneDistance = new float[1];
        Location.distanceBetween(origin.latitude, origin.longitude,
                destination.latitude, destination.longitude,
                oneDistance);

        return oneDistance[0];
    }

    //sets the maps camero to center izmir(basmane)
    //all the markes visible with zoom 8
    public void setCameraToIzmir()
    {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(CenterIzmir)      // Sets the center of the map to Mountain View
                .zoom(8)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void playButtonListener()
    {
        //starts and pauses audios if conditions provided

        if (!playableAudios.isEmpty()) {
            if (playableAudios.get(playableAudios.size() - 1).isPlaying()) {


                //when playing audio,other buttons is not avaliable to use
                showMarkerButton.setEnabled(true);
                getDirectionButton.setEnabled(true);

                playableAudios.get(playableAudios.size() - 1).pause();

                //Change button background
                playAudioButton.setBackgroundResource(R.drawable.play);


            } else {
                //when user pause audio,other buttons is  avaliable to use
                showMarkerButton.setEnabled(false);
                getDirectionButton.setEnabled(false);

                playableAudios.get(playableAudios.size() - 1).start();

                //Change button background
                playAudioButton.setBackgroundResource(R.drawable.pause);

            }
        }

    }
    public void checkMediaIsPlaying()
    {
        //to not get error code(-38,0) which means calling pause when audio playing this method has coded.
        if(!playableAudios.isEmpty())
        {
            if (playableAudios.get(0).isPlaying()) {
                playableAudios.get(0).pause();
            }

            playableAudios.removeAll(playableAudios);
            playAudioButton.setBackgroundResource(R.drawable.play);

        }
    }
    public void releaseMediaPlayer()
    {
        //when language changed by user, audios in this languages will not use anymore. So audios stopped and released
        int index=mediaList.size()-1;
        playableAudios.removeAll(playableAudios);
        while(!mediaList.isEmpty()){
            mediaList.get(index).stop();
            mediaList.get(index).release();
            mediaList.remove(index);
            index--;
        }

    }

    /*markerListener called
     * 1. when showMarkerButton clicked and any marker selected(clicked)
     * 2. when any location search and click on it.
     * 3. when visited places selected and click one of the visited or unvisited marker
     */
    public void markerListener(Marker marker,LatLng currentLocation,ArrayList<MediaPlayer> playableAudios,LatLng[] selectedMarker){

        checkMediaIsPlaying();
        int index=-1;
        while(true)
        {
            index++;
            if (marker.equals(markersList.get(index).getMarker())) {

                //distance between current location and selected marker will be shown on the screen
                float dstnc=calculateDistance(marker.getPosition(),currentLocation);
                textViewDistance = findViewById(R.id.textView1);
                textViewDistance.setText(markersList.get(index).getName());


                selectedMarker[0] =markersList.get(index).getMarker().getPosition();
                //user select one marker then user can request direction
                getDirectionButton.setEnabled(true);


                /* if distance between current location and selected marker is less then 1000 meter
                 * Audio can be playable.
                 * Purpose of the this condition is, provide an audio guide when user go to destination without turist guide as person.
                 * People can have listener from museum. But this is not preferable due to corona virus or hygiene condition for some people.
                 * We are giving chance to listen those audio guide from personel device.
                 * Payment plan can be generable after a while.
                 * For example premium plan:
                 * All audios can be reachable without distance restriction
                 * This is just an idea
                 */
                if (dstnc < 1000) {
                    if (playableAudios.isEmpty())
                    {
                        playableAudios.add(markersList.get(index).getSound());
                        playAudioButton.setEnabled(true);
                    }
                    else
                    {
                        if (!playableAudios.get(playableAudios.size() - 1).isPlaying())
                        {
                            playableAudios.add(markersList.get(index).getSound());
                            playAudioButton.setEnabled(true);
                        }
                    }
                }
                else{
                    playAudioButton.setEnabled(false);
                }
                break;
            }
        }
    }
    public LatLng[] allMarkersVisible(final GoogleMap map) throws FileNotFoundException {
        //on ShowMarkerButton clicked,all the markers will be visible

        //if there exist an route delete it
        if(polylineFinal!=null) {
            polylineFinal.remove();
        }

        getDirectionButton.setEnabled(false);
        final LatLng currentLocation=new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());


        //on ShowMarkerButton clicked,all the markers will be visible
        for (int i = 0; i <markersList.size() ; i++) {

            markersList.get(i).getMarker().setVisible(true);
            
        }
        setCameraToIzmir();

        final LatLng[] selectedMarker = new LatLng[1];
        //call marker listener
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    markerListener(marker,currentLocation,playableAudios,selectedMarker);
                    return true;
                }
            });
        //call play audio listener avaliable
        playAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playButtonListener();
                }
            });
            return  selectedMarker;
    }

    /*new activity starts with extras
     * extra is all the markers name.
     * markerNames is a string arraylist
     */
    public void selectVisitedPlaces()
    {

        Intent intent = new Intent(getApplicationContext(), VisitedPlaces.class);
        intent.putStringArrayListExtra("marker_names", markerNames);
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);


    }

    //Lets suppose there are more than one activity in this application.request code will say the which activity is this.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1

            //code of the visitedPlaces activity is LAUNCH_SECOND_ACTIVITY =1
            if (requestCode == LAUNCH_SECOND_ACTIVITY&&data!=null) {
                if (resultCode == Activity.RESULT_OK) {
                    //visitedPlaces activity returns visitedmarkers and unvisited markers.
                    //user should select places that user have already gone
                    //to get extras ,bundle has used.

                    Bundle bundle = data.getExtras();
                    ArrayList<String> visitedPlaces=bundle.getStringArrayList("visitedMarkers");
                    ArrayList<String> unvisitedPlaces=bundle.getStringArrayList("unvisitedMarkers");
                    onNewIntent(visitedPlaces,unvisitedPlaces);
                }
            }
        }


        /*
           make visited places markers color red but it is invisible until showMarkerButton click.
           make unvisited places markers color green

           This method is a kind of search method
         */
    protected void onNewIntent(ArrayList<String> visitedPlaces,ArrayList<String> unvisitedPlaces)
    {

        for (int allmarkers = 0; allmarkers <markersList.size() ; allmarkers++) {

            boolean flag =false;
            for (int j = 0; j <visitedPlaces.size() ; j++) {
                if(visitedPlaces.get(j).equalsIgnoreCase(markersList.get(allmarkers).getName())) {
                    markersList.get(allmarkers).getMarker().setVisible(false);
                    markersList.get(allmarkers).getMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    flag=true;
                    break;
                }
            }
            if(!flag) {
                for (int j = 0; j < unvisitedPlaces.size(); j++) {
                    if (unvisitedPlaces.get(j).equalsIgnoreCase(markersList.get(allmarkers).getName())) {
                        markersList.get(allmarkers).getMarker().setVisible(true);
                        markersList.get(allmarkers).getMarker().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        getDirectionButton.setEnabled(true);
                        break;
                    }
                }
            }
        }
        final LatLng currentLocation=new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        final LatLng[] selectedMarker = new LatLng[1];


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                routedMarker[0]=marker.getPosition();
                markerListener(marker,currentLocation,playableAudios,selectedMarker);
                return false;
            }
        });
        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonListener();
            }
        });
    }

    /*  readingData() method call at the beginning of the program
        if user change language,for example from english to turkish
        program will read turkish.txt file then arrange markers , audios up to this file

       * txt file *
       destination_name - coordinates - audio of the place

       spliterator is the -.

       coordinates also should be split by comma (,)

       ***
       markers are added in this method to arraylist

     */
    public void readingData(String language) throws FileNotFoundException {
        //old markerNames and MarkerList should be removed.Otherwise program suppose there are more marker than data have.
        markerNames.removeAll(markerNames);
        markersList.removeAll(markersList);


        String data="";
        StringBuffer stringBuffer=new StringBuffer();

        //to get int R.raw.english or R.raw.turkish ...
        int file=getResources().getIdentifier(language,"raw",getPackageName());

        InputStream inputStream=this.getResources().openRawResource(file);

        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        if(inputStream!=null)
        {
            try {
                while((data=bufferedReader.readLine())!=null){
                    stringBuffer.append(data);
                    String[] divideString=data.split("-");
                    lstSource.add(divideString[0]);
                    LatLng position=parseCoordinate(divideString[1]);
                    MediaPlayer mp3= MediaPlayer.create(this,  getResources().getIdentifier(divideString[2],"raw",getPackageName()));
                    mediaList.add(mp3);
                    arrangeMarkers(position,divideString[0],mp3);
                    markerNames.add(divideString[0]);
                }
                inputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    //split coordinates and convert it string to float
    public LatLng parseCoordinate(String coordinate)
    {
        String[] latlng=(coordinate.split(","));
        float latitude= Float.parseFloat(latlng[0]);
        float longitude= Float.parseFloat(latlng[1]);
        LatLng place=new LatLng(latitude,longitude);
        return place;
    }

    //arrangeMarkers() method add marker in map
    public void arrangeMarkers(LatLng position,String name,MediaPlayer sound)
    {

        Marker mapMarker=map.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .visible(false)
                .title("Marker in "+name));

        Markers marker =new Markers(position, name, sound,mapMarker);
        markersList.add(marker);
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }



    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
