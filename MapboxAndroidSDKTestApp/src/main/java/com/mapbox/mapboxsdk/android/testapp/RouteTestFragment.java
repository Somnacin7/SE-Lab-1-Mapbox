package com.mapbox.mapboxsdk.android.testapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.util.JSONUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.MapView;
import com.squareup.okhttp.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Branden on 2/5/2016.
 */
public class RouteTestFragment extends Fragment
{

    private MapView mv;

    private LatLng destination;
    public static final String LAT_KEY = "latitude";
    public static final String LNG_KEY = "longitude";

    public static RouteTestFragment newInstance(double lat, double lng)
    {
        RouteTestFragment frag = new RouteTestFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(LAT_KEY, lat);
        bundle.putDouble(LNG_KEY, lng);
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locate_me_test, container, false);
        mv = (MapView) view.findViewById(R.id.locateMeMapView);
        mv.setUserLocationEnabled(true);

        mv.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);
        mv.setUserLocationRequiredZoom(12);

        Bundle bundle = getArguments();
        double lat = bundle.getDouble(LAT_KEY);
        double lng = bundle.getDouble(LNG_KEY);

        new PaintRoute().execute(new LatLng(lat, lng),mv.getUserLocation());

        // Use this for debug since gps location isn't working with emulator
        /*
        LatLng latlong2 = new LatLng(39.157518,-84.736773);
        mv.setCenter(latlong2);
        mv.setZoom(5);
        new PaintRoute().execute(latlong1,latlong2);
        */

        return view;
    }

    class PaintRoute extends AsyncTask<LatLng,Void,JSONObject>
    {
        // Get JSON from api
        @Override
        protected JSONObject doInBackground(LatLng... params) {
            JSONObject json = new JSONObject();
            try {
                // Get json from mapbox directions api
                String waypoints = Double.toString(params[0].getLongitude()) + "," + Double.toString(params[0].getLatitude()) + ";" +  Double.toString(params[1].getLongitude()) + "," + Double.toString(params[1].getLatitude());
                json = DataLoadingUtils.loadJSONFromUrl("https://api.mapbox.com/v4/directions/mapbox.driving/" + waypoints + ".json?access_token=pk.eyJ1IjoiYnJhbmRpbyIsImEiOiJjaWs1d3Qwa3EwMGJycDdrczZnbGRpdW83In0.jUQ6sl6aS2r-t23XFKOQ7Q");
                return json;
            } catch (Exception e) {

                e.printStackTrace();

            }
            return json;
        }

        // show dialog and draw route
        // TODO: make code prettier
        @Override
        protected void onPostExecute(JSONObject json) {
            try
            {
                final String featureCollectionString = "{  \"type\": \"FeatureCollection\", \"features\": [ ";
                final String feature = "{\"type\":\"feature\",\"geometry\":";
                final String origin = JSONUtils.optString(json, "origin");
                final String destination = JSONUtils.optString(json, "destination");
                final JSONArray routes = json.getJSONArray("routes");

                if (routes.length() > 1)
                {
                    List<String> routesText = new ArrayList<>();
                    for (int i = 0; i < routes.length(); i++)
                    {
                        int distInMeters = Integer.parseInt(JSONUtils.optString(routes.getJSONObject(i), "distance"));

                        routesText.add(String.format("Route " + (i + 1) + "\t %1$.1f miles", distInMeters * .000621371));
                    }

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("Choose Route")
                            .setItems(routesText.toArray(new CharSequence[routesText.size()]), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    try
                                    {
                                        FeatureCollection featureCollection = (FeatureCollection) GeoJSON.parse(featureCollectionString + destination + "," + feature + JSONUtils.optString(routes.getJSONObject(0), "geometry") + "}" + "," + origin + "] }");
                                        ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(featureCollection, null);
                                        for (Object obj : uiObjects)
                                        {
                                            if (obj instanceof Marker)
                                            {
                                                mv.addMarker((Marker) obj);
                                            } else if (obj instanceof PathOverlay)
                                            {
                                                mv.getOverlays().add((PathOverlay) obj);
                                            }
                                        }
                                        if (uiObjects.size() > 0)
                                        {
                                            mv.invalidate();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .create();
                    dialog.show();
                }
                else
                {
                    try
                    {
                        FeatureCollection featureCollection = (FeatureCollection) GeoJSON.parse(featureCollectionString + destination + "," + feature + JSONUtils.optString(routes.getJSONObject(0), "geometry") + "}" + "," + origin + "] }");
                        ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(featureCollection, null);
                        for (Object obj : uiObjects)
                        {
                            if (obj instanceof Marker)
                            {
                                mv.addMarker((Marker) obj);
                            } else if (obj instanceof PathOverlay)
                            {
                                mv.getOverlays().add((PathOverlay) obj);
                            }
                        }
                        if (uiObjects.size() > 0)
                        {
                            mv.invalidate();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }
}
