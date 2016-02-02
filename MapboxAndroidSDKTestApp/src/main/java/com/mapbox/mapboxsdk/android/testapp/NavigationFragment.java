package com.mapbox.mapboxsdk.android.testapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NavigationFragment extends Fragment {

    private MapView mapView;

    private static final String ARG_ADDRESS = "address";

    // TODO: Rename and change types of parameters
    private String address;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_markers, container, false);

        // Setup Map
        mapView = (MapView) view.findViewById(R.id.markersMapView);

        addMarker(address);

        return view;
    }

    private void addMarker(String address)
    {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.US);
        List<Address> geoResults = new ArrayList<>();
        try {
            // Get the location from the string address
            while (geoResults.size()==0) {

                geoResults = geocoder.getFromLocationName(address, 1);
            }
            if (geoResults.size()>0) {
                Address address1 = geoResults.get(0);
                LatLng latlng = new  LatLng(address1.getLatitude(), address1.getLongitude());
                mapView.setCenter(latlng);
                mapView.setZoom(14);

                String line2 = address1.getLocality() + ", " + address1.getAdminArea();// + '\n' + Html.fromHtml("<a href='#'>Navigate Here</a>");

                // Make and add marker
                Marker marker = new Marker(mapView, address1.getAddressLine(0), line2, latlng);
                marker.setIcon(new Icon(getActivity(), Icon.Size.LARGE, "marker-stroked", "FF0000"));
                mapView.addMarker((marker));

            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}
