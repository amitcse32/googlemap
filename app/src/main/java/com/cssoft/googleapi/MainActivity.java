package com.cssoft.googleapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.cssoft.googleapi.clasess.RootClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {


    GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MainActivity.this.googleMap=googleMap;

                googleMap.addMarker(new MarkerOptions().position(new LatLng(30.7046,76.7179)).title("Mohali").draggable(true));

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                        String location=marker.getPosition().latitude+","+marker.getPosition().longitude;
                        getDataFromServer(location);
                    }
                });



                getData();





            }
        });


    }


    public void getData()
    {

        EditText editText=findViewById(R.id.editTextSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //getDataFromServer();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /*editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {

                        case KeyEvent.KEYCODE_ENTER:
                            getDataFromServer();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }

        });*/
    }

    private void getDataFromServer(String location)
    {
        EditText editText=findViewById(R.id.editTextSearch);
        String value=editText.getText().toString();





        Retrofit retrofit=new Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build();
        Call<RootClass> request=retrofit.create(RetrofitInterface.class).getDataFromGoogle(value,location);
        request.enqueue(new Callback<RootClass>() {
            @Override
            public void onResponse(Call<RootClass> call, Response<RootClass> response) {

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(30.7046,76.7179)).title("Mohali").draggable(true).);

                for (int i=0;i<response.body().results.size();i++)
                {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(response.body().results.get(i).geometry.location.lat,response.body().results.get(i).geometry.location.lng)).title(response.body().results.get(i).name));
                }




            }

            @Override
            public void onFailure(Call<RootClass> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



    public interface RetrofitInterface{
        @GET("maps/api/place/nearbysearch/json?radius=500&key=AIzaSyA1WyJF469HuVEg9TlA2fN3C_1i-qqgsVE")
        Call<RootClass> getDataFromGoogle(@Query("keyword")String key,@Query("location")String location);
    }


}
