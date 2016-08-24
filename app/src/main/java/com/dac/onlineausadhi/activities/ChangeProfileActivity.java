package com.dac.onlineausadhi.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dac.onlineausadhi.classes.FontManager;
import com.dac.onlineausadhi.fragments.DialogFragmentEditProfile;
import com.dac.onlineausadhi.onlineaushadhilin.R;

public class ChangeProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView fullname;
    private TextView btnfullname;
    private TextView email;
    private TextView btnEmail;
    private TextView wardNo;
    private TextView btnWard;
    private TextView street;
    private TextView btnStreet;
    private TextView place;
    private TextView btnPlace;
    private TextView houseNo;
    private TextView btnHouse;
    private TextView district;
    private TextView btnDistrict;
    private TextView zone;
    private TextView btnZone;
    private TextView landLine;
    private TextView btnLandLine;
    private TextView mobileNo;
    private TextView btnMobile;
    SharedPreferences sharedPref;
    private String name = "fullName";
    private String email_id = "email";
    private String ward_no = "ward_no";
    private String house_no = "house_no";
    private String street_name = "street_name";
    private String place_name = "place_name";
    private String district_name = "district";
    private String zone_name = "zone";
    private String landline_number = "landline_number";
    private String mobile_number = "mobile_number";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        sharedPref = getSharedPreferences(getString(R.string.sharedPref), 0);
        toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }

        findViewsById();
        setTextViews();
        onClickEvents();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:  //other menu items if you have any
                //add any action here
                this.finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickEvents() {
        btnfullname.setOnClickListener(this);

        btnEmail.setOnClickListener(this);

        btnWard.setOnClickListener(this);

        btnHouse.setOnClickListener(this);

        btnStreet.setOnClickListener(this);

        btnPlace.setOnClickListener(this);

        btnDistrict.setOnClickListener(this);

        btnZone.setOnClickListener(this);

        btnLandLine.setOnClickListener(this);

        btnMobile.setOnClickListener(this);
    }

    public void setTextViews() {
        fullname.setText(sharedPref.getString("fullName", ""));
        email.setText(sharedPref.getString("email", ""));
        wardNo.setText(sharedPref.getString("ward_no", ""));
        street.setText(sharedPref.getString("street_name", ""));
        houseNo.setText(sharedPref.getString("house_no", ""));
        place.setText(sharedPref.getString("place_name", ""));
        district.setText(sharedPref.getString("district", ""));
        zone.setText(sharedPref.getString("zone", ""));
        landLine.setText(sharedPref.getString("landline_number", ""));
        mobileNo.setText(sharedPref.getString("mobile_number", ""));
    }

    private void findViewsById() {
        Typeface mTypeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        fullname = (TextView) findViewById(R.id.fullName);
        email = (TextView) findViewById(R.id.email);
        wardNo=(TextView) findViewById(R.id.wardNumber);
        street = (TextView) findViewById(R.id.streetName);
        houseNo = (TextView) findViewById(R.id.houseNumber);
        place = (TextView) findViewById(R.id.place);
        district = (TextView) findViewById(R.id.district);
        zone = (TextView) findViewById(R.id.zone);
        landLine = (TextView) findViewById(R.id.landLineNumber);
        mobileNo = (TextView) findViewById(R.id.mobileNumber);
        btnfullname = (TextView) findViewById(R.id.btnFullname);
        btnfullname.setTypeface(mTypeface);
        btnEmail = (TextView) findViewById(R.id.btnEmail);
        btnEmail.setTypeface(mTypeface);
        btnWard = (TextView) findViewById(R.id.btnWard);
        btnWard.setTypeface(mTypeface);
        btnHouse = (TextView) findViewById(R.id.btnHouse);
        btnHouse.setTypeface(mTypeface);
        btnStreet = (TextView) findViewById(R.id.btnStreet);
        btnStreet.setTypeface(mTypeface);
        btnPlace = (TextView) findViewById(R.id.btnPlace);
        btnPlace.setTypeface(mTypeface);
        btnDistrict = (TextView) findViewById(R.id.btnDistrict);
        btnDistrict.setTypeface(mTypeface);
        btnZone = (TextView) findViewById(R.id.btnZone);
        btnZone.setTypeface(mTypeface);
        btnLandLine = (TextView) findViewById(R.id.btnLandLine);
        btnLandLine.setTypeface(mTypeface);
        btnMobile = (TextView) findViewById(R.id.btnMobile);
        btnMobile.setTypeface(mTypeface);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnFullname:
                DialogFragmentEditProfile editFullname =
                        DialogFragmentEditProfile.newInstance("Change Full Name", fullname.getText().toString(), name);
                editFullname.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnEmail:
                DialogFragmentEditProfile editEmail =
                        DialogFragmentEditProfile.newInstance("Change Email", email.getText().toString(), email_id);
                editEmail.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnHouse:
                DialogFragmentEditProfile editHouse =
                        DialogFragmentEditProfile.newInstance("Change House Number", houseNo.getText().toString(), house_no);
                editHouse.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnWard:
                DialogFragmentEditProfile editWard =
                        DialogFragmentEditProfile.newInstance("Change Ward Number", wardNo.getText().toString(), ward_no);
                editWard.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnStreet:
                DialogFragmentEditProfile editStreet =
                        DialogFragmentEditProfile.newInstance("Change Street Name", street.getText().toString(), street_name);
                editStreet.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnPlace:
                DialogFragmentEditProfile editPlace =
                        DialogFragmentEditProfile.newInstance("Change Place Name", place.getText().toString(), place_name);
                editPlace.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnDistrict:
                DialogFragmentEditProfile editDistrict =
                        DialogFragmentEditProfile.newInstance("Change District", district.getText().toString(), district_name);
                editDistrict.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnZone:
                DialogFragmentEditProfile editZone =
                        DialogFragmentEditProfile.newInstance("Change Zone", zone.getText().toString(), zone_name);
                editZone.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnLandLine:
                DialogFragmentEditProfile editLandLine =
                        DialogFragmentEditProfile.newInstance("Change LandLine Number", landLine.getText().toString(), landline_number);
                editLandLine.show(getSupportFragmentManager(), "fragmentDialog");
                break;
            case R.id.btnMobile:
                DialogFragmentEditProfile editMobile =
                        DialogFragmentEditProfile.newInstance("Change Mobile Number", mobileNo.getText().toString(), mobile_number);
                editMobile.show(getSupportFragmentManager(), "fragmentDialog");
                break;


        }

    }


}
