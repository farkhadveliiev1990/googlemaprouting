package com.laodev.dwbrouter.model;

import com.laodev.dwbrouter.util.AppConst;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonModel {

    public String name = "";
    public String phone = "";
    public String postalCode = "";
    public String stateOrProvinceCode = "";
    public String stateOrProvinceName = "";
    public String street = "";
    public String city = "";
    public String countryCode = "";
    public String countryName = "";

    public String lat = "0.0";
    public String lng = "0.0";

    public String region = "";

    public PersonModel() {
        name = "";
        phone = "";
        postalCode = "";
        stateOrProvinceCode = "";
        stateOrProvinceName = "";
        street = "";
        city = "";
        countryCode = "";
        countryName = "";
        region = "";
    }

    public PersonModel(JSONObject json) {
        try {
            name = json.getString("name");
        } catch (JSONException e) {
            name = "";
        }

        try {
            postalCode = json.getString("postalCode");
            if (postalCode.length() > 4) {
                for (RegionModel regionModel: AppConst.gAllRegions) {
                    String sub = postalCode.substring(0, 4);
                    for (String region: regionModel.postal) {
                        if (sub.equals(region)) {
                            this.region = regionModel.title;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            postalCode = "";
            region = AppConst.gAllRegions.get(0).title;
        }

        try {
            stateOrProvinceCode = json.getString("stateOrProvinceCode");
        } catch (JSONException e) {
            stateOrProvinceCode = "";
        }
        try {
            stateOrProvinceName = json.getString("stateOrProvinceName");
        } catch (JSONException e) {
            stateOrProvinceName = "";
        }
        try {
            street = json.getString("street");
        } catch (JSONException e) {
            street = "";
        }
        try {
            city = json.getString("city");
        } catch (JSONException e) {
            city = "";
        }
        try {
            countryCode = json.getString("countryCode");
        } catch (JSONException e) {
            countryCode = "";
        }
        try {
            countryName = json.getString("countryName");
        } catch (JSONException e) {
            countryName = "";
        }
        try {
            phone = json.getString("phone");
        } catch (JSONException e) {
            phone = "";
        }
    }

}
