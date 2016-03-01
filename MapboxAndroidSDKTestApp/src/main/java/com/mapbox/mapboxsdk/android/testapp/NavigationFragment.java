package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NavigationFragment extends Fragment implements View.OnClickListener
{

    private MapView mapView;
    private List<Marker> markers;

    private static final String ARG_ADDRESS = "address";

    // TODO: Rename and change types of parameters
    private String address;
    private LatLng latLng;


    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param address Parameter 1.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(String address) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString(ARG_ADDRESS);
        }

        markers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_markers, container, false);

        InputMethodManager inputManager =
                (InputMethodManager) getActivity().getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Setup Map
        mapView = (MapView) view.findViewById(R.id.markersMapView);

        addMarkerFromAddress(getGeoResults(address).get(0));

        for (Marker m : markers) {
            m.setDescOnClickListener(this);
        }

        return view;
    }

    /* Searches for and returns a List<Address> of the search results for String address  */
    private List<Address> getGeoResults(String address) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.US);
        List<Address> geoResults = new ArrayList<>();
        try {
            // Get the location from the string address
            while (geoResults.size() == 0) {
                // While loop to execute until connection go through.
                geoResults = geocoder.getFromLocationName(address, 1);
            }

            if (geoResults.size()>0) {
                Address address1 = geoResults.get(0);
                latLng = new  LatLng(address1.getLatitude(), address1.getLongitude());
                mapView.setCenter(latLng);
                mapView.setZoom(14);

                // Make and add marker
                Marker marker = new Marker(mapView, address1.getAddressLine(0),((address1.getLocality() != null) ? address1.getLocality() : "") + ", " +
                        ((address1.getAdminArea() != null) ? address1.getLocality() : "") , latLng);
                marker.setSubDescription("<a href='#'>Navigate Here!</a>");
                marker.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "marker-stroked", "FF0000"));
                mapView.addMarker((marker));
                markers.add(marker);
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return geoResults;
    }

    /* Takes an address as input and makes a marker out of the address. */
    private void addMarkerFromAddress(Address address) {
        latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mapView.setCenter(latLng);
        mapView.setZoom(14);
        String line2 = ((address.getLocality() != null) ? address.getLocality() : "") + ", " +
                ((address.getAdminArea() != null) ? address.getAdminArea() : "");

        //Make marker and add to map.
        Marker marker = new Marker(mapView, address.getAddressLine(0), line2, latLng);
        marker.setSubDescription(getResources().getString(R.string.navigate_link));
        marker.setIcon(new Icon(getActivity(), Icon.Size.LARGE, getResources().getString(R.string.marker_type), getResources().getString(R.string.marker_color)));
        mapView.addMarker(marker);
        markers.add(marker);
    }

    @Override
    public void onClick(View v)
    {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, RouteTestFragment.newInstance(latLng.getLatitude(), latLng.getLongitude()))
                .commit();
    }
}
