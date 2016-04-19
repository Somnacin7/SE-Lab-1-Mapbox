package com.mapbox.mapboxsdk.android.testapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment {

    private EditText address;
    private MapView mapView;
    private Button searchButton;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param address Parameter 1.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String address) {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        address = (EditText)view.findViewById(R.id.navigationAddressBox);

        searchButton = (Button)view.findViewById(R.id.navigationSearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // This is where search function stuff goes.
                String userInput = address.getText().toString();


                if (isInputValid(userInput))
                {
                    //check to see if what we get back is what we sent.
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.US);
                    List<Address> geoResults = new ArrayList<>();
                    try {
                        while (geoResults.size() == 0) {
                            geoResults = geocoder.getFromLocationName(address.getText().toString(), 1);
                        }
                        if (geoResults.size() > 0) {
                            Address address1 = geoResults.get(0);
                            String whatWeGotBack = ((address1.getAddressLine(0) != null) ? address1.getAddressLine(0) : "" ) + ", " + ((address1.getLocality() != null) ? address1.getLocality() : "") + ", " +
                                    ((address1.getAdminArea() != null) ? address1.getAdminArea() : "") + " " + ((address1.getPostalCode() != null) ? address1.getPostalCode() : "");

                            Toast.makeText(getActivity().getApplicationContext(),
                                    "You wanted to navigate to: " + userInput + "\nYou are being navigated to: " + whatWeGotBack,
                                    Toast.LENGTH_LONG).show();
                                Fragment frag = NavigationFragment.newInstance(userInput);

                                // Insert the fragment by replacing any existing fragment
                                FragmentManager fragm = getActivity().getSupportFragmentManager();
                                fragm.beginTransaction()
                                        .replace(R.id.content_frame, frag)
                                        .commit();



                        }
                    }
                    catch(Exception e) {
                        System.out.print(e.getMessage());
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Address must be alphanumeric, and a length between 1 and 50",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;

    }

    private boolean isInputValid(String input)
    {
        boolean isAlphanumeric = true;
        for (int i = 0; i < input.length(); i++)
        {
            if (!(Character.isLetterOrDigit(input.charAt(i)) ||
                    input.charAt(i) == ' ' ||
                    input.charAt(i) == ',' ||
                    input.charAt(i) == '.' ||
                    input.charAt(i) == '-'
            ))
                isAlphanumeric = false;
        }

        return isAlphanumeric && (input.length() > 0 && input.length() <= 50);
    }
}
