package com.mapbox.mapboxsdk.android.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mapbox.mapboxsdk.views.MapView;

public class SearchFragment extends Fragment {

    private EditText address;
    private Button search;
    private MapView mapView;

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
        search = (Button)view.findViewById(R.id.navigationSearchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is where search function stuff goes.
                String userInput = address.getText().toString();

                Fragment f = NavigationFragment.newInstance(userInput);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.content_frame, f)
                        .commit();
            }
        });

        return view;

    }

    private boolean checkInput(String input)
    {


        return true;
    }
}
