package com.example.sihuserapp.report;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sihuserapp.ApiUtils;
import com.example.sihuserapp.Interfaces.InitialPingInterface;
import com.example.sihuserapp.Objects.PingBody;
import com.example.sihuserapp.Objects.PingResponse;
import com.example.sihuserapp.R;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneReportActivity extends AppCompatActivity  {
    private static final int REQUEST_PERMISSION_READ_EXTERNAL = 2;
    private static String TAG="TAG Phonereport";
    public static String waterStructure;
    InitialPingInterface mAPIService;
    PingBody pingBody;
    Integer id;
    private SimpleLocation location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_report);
        initializeVariables();
    }
    private void initializeVariables(){
        FloatingActionButton nextButton=findViewById(R.id.fab);
        final EditText phoneNumber=findViewById(R.id.phonenumber);
        final EditText queryText=findViewById(R.id.query);
        final TextView lattitudeView=findViewById(R.id.lattitude);
        final TextView longitudeView=findViewById(R.id.longitude);
        final EditText issueType=findViewById(R.id.issue_type);
        location = new SimpleLocation(this);
        int locationPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            String requirePermission[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(PhoneReportActivity.this, requirePermission, REQUEST_PERMISSION_READ_EXTERNAL);
        }

        if (!location.hasLocationEnabled()) SimpleLocation.openSettings(this);
        location.setListener(new SimpleLocation.Listener() {
            public void onPositionChanged() {
                String latitudeText="Latitude: "+String.valueOf(location.getLatitude());
                String longitudeText="Longitude: "+String.valueOf(location.getLongitude());
                lattitudeView.setText(latitudeText);
                longitudeView.setText(longitudeText);
            }
        });
        id=new Integer(-127);

        mAPIService = ApiUtils.getAPIService();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number= phoneNumber.getText().toString();
                String query = queryText.getText().toString();
                String issue = issueType.getText().toString();
                final String latitude = location==null? "12.751":String.valueOf(location.getLatitude());
                final String longitude = location==null? "80.126":String.valueOf(location.getLongitude());
                Log.v("TAG_Phone", number+" "+query+ " "+ waterStructure);
                if(number.length()!=10)
                    Toast.makeText(getApplicationContext(),"Phone Number is too short", Toast.LENGTH_LONG).show();
                else if(query.length()<20){
                    Toast.makeText(getApplicationContext(),"The explanation is too short", Toast.LENGTH_LONG).show();
                }
                else{
                    pingBody = new PingBody(query, latitude, longitude, number, issue);
                    sendPost(pingBody, number,query,latitude,longitude);
                }
            }
        });

    }
    private void pingIsSuccessful(Intent intent, Integer id){
        if(id!=null){
            Log.v(TAG,id.toString());
            if(id.compareTo(-1)>0) {
                startActivity(intent);
            }
        }
    }
    public void sendPost(final PingBody pingBody, final String number, final String query, final String latitude, final String longitude) {
        mAPIService.uploadFile(pingBody).enqueue(new Callback<PingResponse>() {
            @Override
            public void onResponse(Call<PingResponse> call, Response<PingResponse> response) {
                if(response.isSuccessful()) {
                    if(response.body().getId()!=null){
                        Integer id;
                        Log.v(TAG,response.body().toString());
                        id = response.body().getId();
                        SetId(id);
                        Intent intent = new Intent(PhoneReportActivity.this, ReportActivity.class);
                        intent.putExtra("phone", number);
                        intent.putExtra("query", query);
                        intent.putExtra("Latitude: ", latitude);
                        intent.putExtra("Longitude: ", longitude);
                        intent.putExtra("id", id);
                        pingIsSuccessful(intent, id);
                    }
                    //showResponse(response.body().toString());
                    Log.v(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<PingResponse> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }

    public void SetId(Integer id){
        this.id=id;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!location.hasLocationEnabled()) SimpleLocation.openSettings(this);
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }
    /*public void showResponse(String response) {
        if(mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);
    }*/
}
