package com.laodev.dwbrouter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProductModel {

    public String id = "";
    public String productId = "";
    public String categoryId = "";
    public double price = 0;
    public int quantity = 0;
    public String name = "";
    public String imageUrl = "";
    public String value = "";
    public Bitmap bitmap = null;

    public ProductModel() {
        id = "";
        productId = "";
        categoryId = "";
        price = 0;
        quantity = 0;
        name = "";
        imageUrl = "";
        value = "";
        bitmap = null;
    }

    public ProductModel(JSONObject json) {
        try {
            id = json.getString("id");
        } catch (JSONException e) {
            id = "";
        }
        try {
            productId = json.getString("productId");
        } catch (JSONException e) {
            productId = "";
        }
        try {
            categoryId = json.getString("categoryId");
        } catch (JSONException e) {
            categoryId = "";
        }
        try {
            price = json.getDouble("price");
        } catch (JSONException e) {
            price = 0.0;
        }
        try {
            quantity = json.getInt("quantity");
        } catch (JSONException e) {
            quantity = 0;
        }
        try {
            name = json.getString("name");
        } catch (JSONException e) {
            name = "";
        }
        try {
            value = json.getJSONArray("selectedOptions").getJSONObject(0).getString("value");
        } catch (JSONException e) {
            value = "";
        }
        try {
            imageUrl = json.getString("imageUrl");
        } catch (JSONException e) {
            imageUrl = "";
        }
    }

}
