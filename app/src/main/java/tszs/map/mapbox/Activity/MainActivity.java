package tszs.map.mapbox.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.URI;

import tszs.map.mapbox.R;
import tszs.map.mapbox.tszs.map.mapbox.util.PermissionUtil;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private MapView mapView;
    int REQUEST_CONTACTS = 127;
    private static final String GEOJSON_SOURCE_ID = "GEOJSONFILE";
    private  Bundle savedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, REQUEST_CONTACTS);
        }
        else
        {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CONTACTS)
        {
            if (PermissionUtil.verifyPermissions(grantResults))
            {
                init();
            }
        }
    }

    private void init()
    {
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                createGeoJsonSource(style);
                addPolygonLayer(style);
//                addPointsLayer(style);
            }
        });
    }

    private void createGeoJsonSource(@NonNull Style loadedMapStyle) {
        try {
            // Load data from GeoJSON file in the assets folder
            loadedMapStyle.addSource(new GeoJsonSource(GEOJSON_SOURCE_ID,
                    new URI("asset://china.geojson")));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void addPolygonLayer(@NonNull Style loadedMapStyle) {
        // Create and style a FillLayer that uses the Polygon Feature's coordinates in the GeoJSON data
        FillLayer countryPolygonFillLayer = new FillLayer("polygon", GEOJSON_SOURCE_ID);
        countryPolygonFillLayer.setProperties(
                PropertyFactory.fillColor(Color.RED),
                PropertyFactory.fillOpacity(.4f));
        countryPolygonFillLayer.setFilter(eq(literal("$type"), literal("Polygon")));
        loadedMapStyle.addLayer(countryPolygonFillLayer);
    }

    private void addPointsLayer(@NonNull Style loadedMapStyle) {
        // Create and style a CircleLayer that uses the Point Features' coordinates in the GeoJSON data
        CircleLayer individualCirclesLayer = new CircleLayer("points", GEOJSON_SOURCE_ID);
        individualCirclesLayer.setProperties(
                PropertyFactory.circleColor(Color.YELLOW),
                PropertyFactory.circleRadius(3f));
        individualCirclesLayer.setFilter(eq(literal("$type"), literal("Point")));
        loadedMapStyle.addLayer(individualCirclesLayer);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
