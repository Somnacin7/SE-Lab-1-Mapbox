package com.mapbox.mapboxsdk.android.testapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.IOException;
import java.util.*;
import java.util.Random;


public class NavigationTestFragment extends Fragment{

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_markers, container, false);

        // TODO: Get this address from feature 1
        String address = "544 Aston View Ln, Cleves, OH 45002";
        // Get latlong from address
        LatLng latlong = StringAdressToLatLong(address);

        // Setup Map
        mapView = (MapView) view.findViewById(R.id.markersMapView);
        mapView.setCenter(latlong);
        mapView.setZoom(14);

        // Make and add marker
        Marker marker = new Marker(mapView, "Martin Luther King", "Martin Luther King", latlong);
        marker.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "marker-stroked", "FF0000"));
        mapView.addMarker((marker));

        return view;
    }

    LatLng StringAdressToLatLong(String stringAdress)
    {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.US);
        List<Address> geoResults = new ArrayList<Address>();
        try {
            // Get the location from the string address
            while (geoResults.size()==0) {

                geoResults = geocoder.getFromLocationName(stringAdress, 1);
            }
            if (geoResults.size()>0) {
                return new LatLng(geoResults.get(0).getLatitude(),geoResults.get(0).getLongitude());
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return null;
    }
}
