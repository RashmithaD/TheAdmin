package in.yousee.theadmin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

import in.yousee.theadmin.util.LogUtil;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by mittu on 27-07-2016.
 */
public class LocationFragmentV2 extends DialogFragment implements OnMapReadyCallback {

    public static final String ARG_CHECK_IN = "CHECK_IN";
    public static final short CHECK_IN = 1;
    public static final short CHECK_OUT = 2;

    private TextView testText;
    private GoogleMap mMap;
    private MapView mapView;

    private int checkStatus;


    private OnFragmentInteractionListener mListener;

    public LocationFragmentV2() {

    }

    public static LocationFragmentV2 newInstance(short checkin) {
        LocationFragmentV2 fragment = new LocationFragmentV2();

        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.zOrderOnTop(true);
        Bundle args = new Bundle();

        args.putShort(ARG_CHECK_IN, checkin);
        //fragment.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHI‌​ND);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            checkStatus = getArguments().getShort(ARG_CHECK_IN);
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_Dialog);

    }

    View messageBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        testText = (TextView) view.findViewById(R.id.testText);
        messageBackground = (View) view.findViewById(R.id.message_background);
        setAnimation(messageBackground);
        mapView.getMapAsync(this);
        return view;
    }

    private void setAnimation(View view)
    {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        view.setAnimation(animation);
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public void stopListeningToLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        if(lm != null)
        {
            lm.removeUpdates(listener);
        }
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
        //mMap.setPadding(0,0,0,50);

        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.print("no permission");
        }
        else {

            mMap.setMyLocationEnabled(true);
            addPolyAreaOnMap();
        }

        LogUtil.print("Map Ready");
        implementLocationManager();
    }

    LocationManager lm;
    LocationListenerImp listener;
    public void implementLocationManager()
    {
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        listener = new LocationListenerImp();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);

    }

    private void updateMessage(boolean inside)
    {
        String textViewString = "";

        if(inside == true)
        {
            polygon.setStrokeColor(Color.argb(128, 50, 255, 50));
            polygon.setFillColor(Color.argb(128, 0, 255, 0));
            textViewString = "Registering your attendance";
            messageBackground.setBackgroundResource(R.color.green);
        }
        else
        {
            polygon.setStrokeColor(Color.argb(128, 255, 50, 50));
            polygon.setFillColor(Color.argb(128, 255, 0, 0));
            textViewString = "Please walk into the work location, your attendance will be registered automatically";
            messageBackground.setBackgroundResource(R.color.red);
        }
        testText.setText(""+textViewString);

    }


    private boolean insideWorkLocation = false;
    private LatLng currentLocation;
    private class LocationListenerImp implements android.location.LocationListener
    {

        AuthenticationThread authenticationThread = null;
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LogUtil.print("location changed :"+location.getLatitude() +" "+ location.getLongitude());
            CameraUpdate cameraUpdate;
            if( mMap.getCameraPosition() == null)
            {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
            }
            else
            {
                CameraPosition.Builder cameraPosition = new CameraPosition.Builder(mMap.getCameraPosition());
                cameraPosition.target(latLng);
                cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition.build());
            }
            mMap.animateCamera(cameraUpdate);
            currentLocation =  latLng;
            insideWorkLocation = pointInPolygon(latLng, polygon);
            if(!isAuthenticationThreadStarted)
            {
                authenticationThread =new AuthenticationThread(LocationFragmentV2.this);
            }
            if(!insideWorkLocation) // if not inside location
            {
                authenticationThread.resetTimeWaited();
            }
            updateMessage(insideWorkLocation);

        }

        boolean isAuthenticationThreadStarted = false;
        private class AuthenticationThread implements Runnable
        {
            Thread t;

            private final static int TIME_TO_WAIT = 3; //in seconds
            public  short timeWaited = 0;
            Fragment fragment;
            public  AuthenticationThread(Fragment fragment)
            {
                t = new Thread(this);
                this.fragment = fragment;
                isAuthenticationThreadStarted = true;
                t.start();
            }
            @Override
            public void run() {
                while(timeWaited <= TIME_TO_WAIT)
                {
                    try {
                        LogUtil.print("sleeping");
                        Thread.sleep(1000);
                        LogUtil.print("waking");
                        if(insideWorkLocation == false)
                        {
                            timeWaited = 0;
                        }
                        else
                        {
                            timeWaited++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //after TIME_TO_WAIT seconds: authenticated
                stopListeningToLocationUpdates();
                //any changes to be updated on UI must run on UI thread.
                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        authenticate();
                    }
                });


            }
            public void resetTimeWaited()
            {
                LogUtil.print("resetting");
                timeWaited = 0;
            }
        }



        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }


    public  void authenticate()
    {
        LogUtil.print("authenticating");
        testText.setText("authenticating");
    }
    Polygon polygon;
    public void addPolyAreaOnMap()
    {
        // Instantiates a new Polygon object and adds points to define a rectangle
        //gandhi
//        PolygonOptions rectOptions = new PolygonOptions()
//                .add(new LatLng(17.422535, 78.501770),
//                        new LatLng(17.424829, 78.502028),
//                        new LatLng(17.424727, 78.503605),
//                        new LatLng(17.426063, 78.504042),
//                        new LatLng(17.423995, 78.505869),
//                        new LatLng(17.422245, 78.504238)
//                )
//                .strokeColor(Color.RED)
//                .fillColor(Color.argb(128, 255, 0, 0))
//                ;
//        //yousee
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(17.319037, 78.527645),
                        new LatLng(17.319301, 78.527712),
                        new LatLng(17.319268, 78.527892),
                        new LatLng(17.319002, 78.527876)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.argb(128, 255, 0, 0))
                ;

        //Yousee

//        PolygonOptions rectOptions = new PolygonOptions()
//                .add(new LatLng(17.426084, 78.453917),
//                        new LatLng(17.426178, 78.454073),
//                        new LatLng(17.425977, 78.454150),
//                        new LatLng(17.425905, 78.453998)
//                )
//                .strokeColor(Color.RED)
//                .fillColor(Color.argb(128, 255, 0, 0))
//                ;
        //pochampally home
//        PolygonOptions rectOptions = new PolygonOptions()
//                .add(new LatLng(17.343039, 78.819420),
//                        new LatLng(17.343073, 78.819514),
//                        new LatLng(17.342953, 78.819593),
//                        new LatLng(17.342871, 78.819406)
//                )
//                .strokeColor(Color.RED)
//                .fillColor(Color.argb(128, 255, 0, 0))
//                ;


        // Get back the mutable Polygon
        polygon = mMap.addPolygon(rectOptions);




    }


    public boolean pointInPolygon(LatLng point, Polygon polygon) {
        // ray casting algorithm http://rosettacode.org/wiki/Ray-casting_algorithm
        int crossings = 0;
        List<LatLng> path = polygon.getPoints();
        path.remove(path.size()-1); //remove the last point that is added automatically by getPoints()

        // for each edge
        for (int i=0; i < path.size(); i++) {
            LatLng a = path.get(i);
            int j = i + 1;
            //to close the last edge, you have to take the first point of your polygon
            if (j >= path.size()) {
                j = 0;
            }
            LatLng b = path.get(j);
            if (rayCrossesSegment(point, a, b)) {
                crossings++;
            }
        }

        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    public boolean rayCrossesSegment(LatLng point, LatLng a,LatLng b) {
        // Ray Casting algorithm checks, for each segment, if the point is 1) to the left of the segment and 2) not above nor below the segment. If these two conditions are met, it returns true
        double px = point.longitude,
                py = point.latitude,
                ax = a.longitude,
                ay = a.latitude,
                bx = b.longitude,
                by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax <0 || bx <0) { px += 360; ax+=360; bx+=360; }
        // if the point has the same latitude as a or b, increase slightly py
        if (py == ay || py == by) py += 0.00000001;


        // if the point is above, below or to the right of the segment, it returns false
        if ((py > by || py < ay) || (px > Math.max(ax, bx))){
            return false;
        }
        // if the point is not above, below or to the right and is to the left, return true
        else if (px < Math.min(ax, bx)){
            return true;
        }
        // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
        else {
            double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }

    }


    private static final int REQUEST_ACCESS_LOCATION = 100;

    public boolean requestLocationPermission()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(this.getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(),ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else
        {
            LogUtil.print("requesting permission = "+REQUEST_ACCESS_LOCATION);
            requestPermissions(new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOCATION);
        }
        return false;
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LogUtil.print("onRequestPermissionResult = "+requestCode);
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            LogUtil.print("accesrequest");
            LogUtil.print("Permission granted");
            //onConnected(null);

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtil.print("Permission granted");
                //onConnected(null);

            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
