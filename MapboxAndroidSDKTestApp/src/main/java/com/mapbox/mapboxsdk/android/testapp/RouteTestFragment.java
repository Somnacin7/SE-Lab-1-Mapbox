package com.mapbox.mapboxsdk.android.testapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        destination = new LatLng(lat, lng);

        //TODO  get this from first part of feature 4
//        LatLng latlong1 = new LatLng(38.91, -77.03);

        new PaintRoute().execute(destination,mv.getUserLocation());

        // Use this for debug since I gps location isn't working with emulator
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
        @Override
        protected JSONObject doInBackground(LatLng... params) {
            JSONObject json = new JSONObject();
            try {

                // Get json from mapbox directions api
                String waypoints = Double.toString(params[0].getLongitude()) + "," + Double.toString(params[0].getLatitude()) + ";" +  Double.toString(params[1].getLongitude()) + "," + Double.toString(params[1].getLatitude());
                String url = "https://api.mapbox.com/v4/directions/mapbox.driving/" + waypoints + ".json?access_token=pk.eyJ1IjoiYnJhbmRpbyIsImEiOiJjaWs1d3Qwa3EwMGJycDdrczZnbGRpdW83In0.jUQ6sl6aS2r-t23XFKOQ7Q";
                json = DataLoadingUtils.loadJSONFromUrl(url);
                return json;

            } catch (Exception e) {

                e.printStackTrace();

            }
            return json;

        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try
            {
                ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(JsonToFeatureCollection(jsonObject), null);
                for (Object obj : uiObjects) {
                    if (obj instanceof Marker) {
                        mv.addMarker((Marker) obj);
                    } else if (obj instanceof PathOverlay) {
                        mv.getOverlays().add((PathOverlay) obj);
                    }
                }
                if (uiObjects.size() > 0) {
                    mv.invalidate();
                }
            }
            catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }


        // Creates GeoJSON FeatureCollection from the json returned from mapbox directions api
        FeatureCollection JsonToFeatureCollection(JSONObject json) throws JSONException {

            String featureCollectionString = "{  \"type\": \"FeatureCollection\", \"features\": [ ";
            String feature = "{\"type\":\"feature\",\"geometry\":";
            String origin = JSONUtils.optString(json, "origin");
            String destination = JSONUtils.optString(json, "destination");
            JSONArray routes = json.getJSONArray("routes");

            //TODO feature5 pick the route from menu. To get distance I think it is JSONUtils.optString(routes.getJSONObject(i),"distance")
            String route = JSONUtils.optString(routes.getJSONObject(0), "geometry");

            try
            {
                FeatureCollection featureCollection = (FeatureCollection) GeoJSON.parse(featureCollectionString + destination + "," + feature + route + "}" + "," + origin + "] }");
                return featureCollection;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
