package tszs.map.mapbox.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.TileSet;


import java.net.URI;

import tszs.map.mapbox.R;
import tszs.map.mapbox.tszs.map.mapbox.util.PermissionUtil;
import tszs.map.mapbox.tszs.map.mapbox.util.ShapeFileDataEditServerImp;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;

public class MainActivity extends AppCompatActivity
{
    private MapView mapView;
    int REQUEST_CONTACTS = 127;
    private static final String GEOJSON_SOURCE_ID = "GEOJSONFILE";
    MapboxMap mMapboxMap;
    Style mStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
//            保存全局地图对象
            this.mMapboxMap = mapboxMap;
            this.mStyle = style;
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CONTACTS)
        {
            if (PermissionUtil.verifyPermissions(grantResults))
            {
                createGeoJsonSource(mStyle);
                addPolygonLayer(mStyle);
            }
        }
    }

    private void createGeoJsonSource(@NonNull Style loadedMapStyle) {
        try
        {
            loadedMapStyle.addSource(new GeoJsonSource(GEOJSON_SOURCE_ID,
                    new URI("asset://custom.json")));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void addPolygonLayer(@NonNull Style loadedMapStyle)
    {
        FillLayer countryPolygonFillLayer = new FillLayer("polygon", GEOJSON_SOURCE_ID);
        countryPolygonFillLayer.setProperties(
                PropertyFactory.fillColor(Color.RED),
                PropertyFactory.fillOpacity(.4f));
        countryPolygonFillLayer.setFilter(eq(literal("$type"), literal("Polygon")));
        loadedMapStyle.addLayer(countryPolygonFillLayer);
    }


    /**
     * 加载Geojson数据
     * @param v
     */
    public void loadGeoJsonData(View v)
    {
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
            createGeoJsonSource(mStyle);
            addPolygonLayer(mStyle);
        }
    }

    /**
     * 加载Shape数据
     * @param v
     */
    public void addShape(View v)
    {
        ShapeFileDataEditServerImp dataEditServerImp = new ShapeFileDataEditServerImp();
        try
        {
            String geojson  = dataEditServerImp.queryFeature2("asset://CHN_adm3.shp","");
            GeoJsonSource jsonSource = new GeoJsonSource("SHAPE_FILE",geojson);
            mStyle.addSource(jsonSource);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 加载WMS服务
     * @param v
     */
    public void addWMSServer(View v)
    {
        mStyle.addSource(new RasterSource("web-map-source", new TileSet("tileset", "http://129.211.11.95:8066/geoserver/njdn/wms?service=WMS&version=1.1.0&request=GetMap&layers=njdn%3Adk_4326&bbox=118.28830779400002%2C31.159332734000134%2C118.2945880627997%2C31.166442932892814&width=678&height=768&srs=EPSG%3A4326"), 256));
        if (mStyle.getLayer("tunnel-street-minor-low") != null)
        {
            mStyle.addLayerBelow(new RasterLayer("web-map-layer", "web-map-source"), "tunnel-street-minor-low");
        }
        else
        {
            mStyle.addLayer(new RasterLayer("web-map-layer", "web-map-source"));
        }
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
