package com.laodev.dwbrouter.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    public String orderNumber = "";
    public double refundedAmount;
    public double subtotal;
    public double total;

    public String email = "";
    public String datetime = "";
    public String orderComments = "";
//    public String transactionID = "";
//    public String paymentMethod = "";
    public String paymentStatus = "";
//    public String paymentMessage = "";
    public String fulfillmentStatus = "";
    public String ipAddress = "";
    public String customerID = "";
    public String deliveryDate = "";

    public String adminNote = "";

    public PersonModel shippingPerson;
    public List<ProductModel> products = new ArrayList<>();

    public boolean isCheck = false;

    public OrderModel() {
        orderNumber = "";
        refundedAmount = 0.0;
        subtotal = 0.0;
        total = 0.0;
        email = "";
        datetime = "";
        orderComments = "";
//        transactionID = "";
//        paymentMethod = "";
        paymentStatus = "";
//        paymentMessage = "";
        fulfillmentStatus = "";
        ipAddress = "";
        customerID = "";
        deliveryDate = "";
        adminNote = "";
        shippingPerson = new PersonModel();
        products = new ArrayList<>();

        isCheck = false;
    }

    public OrderModel(JSONObject json) {
        try {
            orderNumber = json.getString("vendorOrderNumber");
        } catch (JSONException ignored) {
        }
        isCheck = false;
        try {
            refundedAmount = json.getDouble("refundedAmount");
        } catch (JSONException e) {
            refundedAmount = 0.0;
        }
        try {
            subtotal = json.getDouble("subtotal");
        } catch (JSONException e) {
            subtotal = 0.0;
        }
        try {
            total = json.getDouble("total");
        } catch (JSONException e) {
            total = 0;
        }
        try {
            email = json.getString("email");
        } catch (JSONException e) {
            email = "";
        }
        try {
            datetime = json.getString("updateDate");
        } catch (JSONException e) {
            datetime = "";
        }
        try {
            paymentStatus = json.getString("paymentStatus");
        } catch (JSONException e) {
            paymentStatus = "";
        }
        try {
            fulfillmentStatus = json.getString("fulfillmentStatus");
        } catch (JSONException e) {
            fulfillmentStatus = "";
        }
        try {
            ipAddress = json.getString("ipAddress");
        } catch (JSONException e) {
            ipAddress = "";
        }
        try {
            customerID = json.getString("customerId");
        } catch (JSONException e) {
            customerID = "";
        }

        try {
            adminNote = json.getString("privateAdminNotes");
            if (adminNote.length() > 13) {
                deliveryDate = adminNote.substring(13, 23);
            }
        } catch (JSONException e) {
            JSONObject extraFields;
            try {
                extraFields = json.getJSONObject("extraFields");
            } catch (JSONException e1) {
                extraFields = new JSONObject();
            }

            try {
                deliveryDate = extraFields.getString("cstmz_delivery_date");
            } catch (JSONException e1) {
                deliveryDate = "";
            }
        }

        try {
            shippingPerson = new PersonModel(json.getJSONObject("shippingPerson"));
        } catch (JSONException e) {
            try {
                shippingPerson = new PersonModel(json.getJSONObject("billingPerson"));
            } catch (JSONException ex) {
                shippingPerson = new PersonModel();
            }
        }
        products.clear();
        JSONArray productArray;
        try {
            productArray = json.getJSONArray("items");
        } catch (JSONException e) {
            productArray = new JSONArray();
        }
        for (int i = 0; i < productArray.length(); i++) {
            JSONObject productObj;
            try {
                productObj = productArray.getJSONObject(i);
            } catch (JSONException e) {
                productObj = new JSONObject();
            }
            ProductModel model = new ProductModel(productObj);
            products.add(model);
        }

        try {
            orderComments = json.getString("orderComments");
        } catch (JSONException e) {
            orderNumber = "";
        }
    }

}
