package com.example.melchy.camera2apiexampleusingtempfile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;


public class MainActivity extends AppCompatActivity {
    public Button btnGen;
    public TextView result;
    public EditText etInput;
    public EditText etInput2;
    public int intputValue,intputValue2;
    String fnResult;
    ProgressDialog mProgressDialog;
    private BroadcastReceiver brodcastReceiver;

    private String dateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGen = (Button)findViewById(R.id.btnGenerate);
        /*result = (TextView)findViewById(R.id.tvResult);
        etInput = (EditText)findViewById(R.id.etInput);
        etInput2 = (EditText)findViewById(R.id.etInput2);*/
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Randomizer");

        if(!requesrPermission()){

        }

    }

    private void runLocService() {
        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);
    }

    private boolean requesrPermission() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(brodcastReceiver != null){
            unregisterReceiver(brodcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(brodcastReceiver == null){
            brodcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //result.setText(""+intent.getExtras().get("latitude"));

                    //myLocations.dateTimeValues((String) intent.getExtras().get("timeDate"));
                    /*myLocations.myLat((Double) intent.getExtras().get("latitude"));
                    myLocations.myLong((Double) intent.getExtras().get("longitude"));*/
                    Map mWaypoints = new HashMap();
                    String key = myRef.push().getKey();
                    mWaypoints.put("Date and Time",  intent.getExtras().get("timeDate"));
                    mWaypoints.put("latitude",  intent.getExtras().get("latitude"));
                    mWaypoints.put("longitude", intent.getExtras().get("longitude"));
                    mWaypoints.put("id",1);

                    /*Map mWayPointsMap = new HashMap();
                    mWayPointsMap.put(key, mWaypoints);*/

                    Map mParent = new HashMap();
                    mParent.put("Location_Info", mWaypoints);


                    //myRef.push().setValue(mParent);
                    myRef.child("MyLocation").setValue(mParent);

                    //conRef.setValue(""+intent.getExtras().get("coordinates"));

                }
            };
        }

        registerReceiver(brodcastReceiver,new IntentFilter("location_update"));
    }


    // Write a message to the database
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference conRef = myRef.child("MyLocation");
    @Override
    protected void onStart() {
        super.onStart();

        conRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // String strVal = dataSnapshot.getValue(String.class);
               // result.setText(strVal);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
       // DatabaseReference newRef = myRef.child("MyLocation").push();


    }


    public void GenerateValue(View v){
        runLocService();
      /*  String strNum = etInput.getText().toString();
        String strNum2 = etInput2.getText().toString();
        result.setText("Generate Numbers: ");
        if(TextUtils.isEmpty(strNum)){
            etInput.setError("Please Don't Leave it blank");
            Toast.makeText(this,"Please Input a Number!",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(strNum2) || strNum2 == "0"){
            etInput2.setError("Please Don't Leave it blank or no value higher than max value");
            Toast.makeText(this,"Please Input a Number!",Toast.LENGTH_LONG).show();
        }
        else{
            intputValue2 = Integer.parseInt(etInput2.getText().toString());
            intputValue = Integer.parseInt(etInput.getText().toString());
            if(intputValue2 < intputValue) {
                Log.d("Values", intputValue + "");
                mProgressDialog.show();
                mProgressDialog.setMessage("Please Wait Generating Numbers...");
                new getResult().execute();
            }else{
                etInput2.setError("Please Don't Leave it blank or no value higher than max value");
                Toast.makeText(this,"Please Input a Number!",Toast.LENGTH_LONG).show();
            }
        }*/
    }

    public ArrayList<Integer> GenerateNum(int size){
        ArrayList<Integer> listNum = new ArrayList<Integer>(size);

        //Fill ArrayList
        for(int i = 1; i<=size; i++){
            listNum.add(i);
        }
         Collections.shuffle(listNum);
        return listNum;
    }
    private class getResult extends AsyncTask<String, String, ArrayList<Integer>> {

        @Override
        protected ArrayList<Integer> doInBackground(String... strings) {
            ArrayList<Integer> listNum =  GenerateNum(intputValue);
            return listNum;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> listNum) {
            super.onPostExecute(listNum);
            fnResult = "";
            for(int i = 0; i<=intputValue2 - 1; i++){
                result.append(listNum.get(i) +", ");

            }
            fnResult = result.getText().toString();
            conRef.setValue(fnResult);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

            }else {
                requesrPermission();
            }
        }
    }
}

