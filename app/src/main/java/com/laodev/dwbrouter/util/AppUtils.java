package com.laodev.dwbrouter.util;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.laodev.dwbrouter.R;
import com.laodev.dwbrouter.activity.CheckListActivity;
import com.laodev.dwbrouter.activity.OrderDetailActivity;
import com.laodev.dwbrouter.callback.EditTextDialogListener;
import com.laodev.dwbrouter.model.CheckListModel;
import com.laodev.dwbrouter.model.OrderModel;
import com.laodev.dwbrouter.model.ProductModel;
import com.laodev.dwbrouter.model.RegionModel;
import com.laodev.dwbrouter.model.RouterInfoModel;
import com.laodev.dwbrouter.model.UserModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppUtils {

    static public UserModel gUser = new UserModel();
    static public Date gSelDate = new Date();
    static public UserModel gSelUser = new UserModel();
    static public OrderModel gSelOrder = new OrderModel();
    static public OrderModel gSelRouterOrder = new OrderModel();

    static public List<OrderModel> gSelOrders = new ArrayList<>();
    static public List<OrderDetailActivity.RouterModel> gSelDOrders = new ArrayList<>();

    static public List<String> allProgress = new ArrayList<>();
    static public int gSelIndex = 0;
    static public CheckListModel gCheckListModel = new CheckListModel();


    public static void showOtherActivity (Context context, Class<?> cls, int direction) {
        Intent myIntent = new Intent(context, cls);
        ActivityOptions options;
        switch (direction) {
            case 0:
                options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left);
                context.startActivity(myIntent, options.toBundle());
                break;
            case 1:
                options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_left, R.anim.slide_out_right);
                context.startActivity(myIntent, options.toBundle());
                break;
            default:
                context.startActivity(myIntent);
                break;
        }
    }

    public static void initUIActivity (AppCompatActivity activity) {
        // Change Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().setStatusBarColor(activity.getColor(R.color.colorMainWhite));
        } else {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorMainWhite));
        }
    }

    public static String createPdf(Context ctx, List<OrderDetailActivity.RouterModel> orders){
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        int width = 300;
        int height = 424;

        int spaceLine = 30;
        int spacing = 11;

        for (OrderDetailActivity.RouterModel order: orders) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

//            if (order.shippingPerson.region.equals(AppConst.regions[1])) {
//                paint.setColor(ctx.getResources().getColor(R.color.colorRoosterRed));
//            } else if (order.shippingPerson.region.equals(AppConst.regions[2])) {
//                paint.setColor(ctx.getResources().getColor(R.color.colorRoosterBlue));
//            } else if (order.shippingPerson.region.equals(AppConst.regions[3])) {
//                paint.setColor(ctx.getResources().getColor(R.color.colorRoosterGreen));
//            } else if (order.shippingPerson.region.equals(AppConst.regions[4])) {
//                paint.setColor(ctx.getResources().getColor(R.color.colorRoosterYellow));
//            } else if (order.shippingPerson.region.equals(AppConst.regions[5])) {
//                paint.setColor(ctx.getResources().getColor(R.color.colorRoosterPink));
//            }

            for (RegionModel regionModel: AppConst.gAllRegions) {
                if (order.orderModel.shippingPerson.region.equals(regionModel.title)) {
                    paint.setColor(Color.parseColor(regionModel.color));
                    break;
                }
            }

            canvas.drawRect(13, 6, 65, 26, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(16);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            int index = 2;
            int weekIndex = DateHelper.getWeekFromDateString(order.orderModel.deliveryDate, "dd-MM-yyyy");
            String dayString = DateHelper.getDayFromDateString(order.orderModel.deliveryDate, "dd-MM-yyyy");
            String monthString = DateHelper.getMonthFromDateString(order.orderModel.deliveryDate, "dd-MM-yyyy");

            canvas.drawText("#" + order.orderModel.orderNumber + "     " + AppConst.weeksStrings[weekIndex - 1] + " " + dayString + " " + monthString, 20, spacing * 2, paint);
            paint.setTextSize(14);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText(order.orderModel.shippingPerson.name + "   (" + order.orderModel.shippingPerson.phone + ")", 30, (float) (spacing * 3.5), paint);
            canvas.drawText(order.orderModel.shippingPerson.street + ",  " + order.orderModel.shippingPerson.city, 30, spacing * 5, paint);

            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            int commentLength = order.orderModel.orderComments.length();
            for (int i = 0; i < (commentLength / spaceLine + 1); i++) {
                String line = "";
                if (i == order.orderModel.orderComments.length() / spaceLine) {
                    line = order.orderModel.orderComments.substring(i * spaceLine);
                } else {
                    line = order.orderModel.orderComments.substring(i * spaceLine, (i + 1) * spaceLine);
                }
                canvas.drawText(line, 30, (float) (spacing * (6.5 + i)), paint);
            }

            if (commentLength > 0) {
                index = (int)(6.5 + commentLength / spaceLine) + 2;
            } else {
                index = (int)(6.5 + commentLength / spaceLine) + 1;
            }

            int adminLength = order.orderModel.adminNote.length();
            for (int i = 0; i < (adminLength / spaceLine + 1); i++) {
                String line = "";
                if (i == order.orderModel.adminNote.length() / spaceLine) {
                    line = order.orderModel.adminNote.substring(i * spaceLine);
                } else {
                    line = order.orderModel.adminNote.substring(i * spaceLine, (i + 1) * spaceLine);
                }
                canvas.drawText(line, 30, (float) (spacing * (index + i)), paint);
            }

            index++;
            index++;

            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            paint.setTextSize(10);
            int pageIndex = 1;
            int modelIndex = index / 3;
            for (int i = 0; i < order.orderModel.products.size(); i++) {
                ProductModel model = order.orderModel.products.get(i);
                if (modelIndex == 12) {
                    modelIndex = 0;
                    document.finishPage(page);
                    pageIndex++;
                    pageInfo = new PdfDocument.PageInfo.Builder(width, height, pageIndex).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(12);
                    index = 2;
                }
                canvas.drawText((i + 1) + ": " + model.name, 30, spacing * index, paint);
                index++;

//                canvas.drawBitmap(model.bitmap, new Rect(0, 0, 100, 100), new Rect(200, spacing * index, 20, 20), null);

                String price = String.format("%.2f", model.price * model.quantity);
                canvas.drawText("prijs: " + "€ " + price + "      hoeveelheid: " + model.quantity + " x" + model.value, 40, spacing * index, paint);
//                canvas.drawText("hoeveelheid: " + model.quantity + " x " + model.value, 40, spacing * index, paint);
                index++;
                index++;

                modelIndex++;
            }

            paint.setTextSize(16);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("All prijs:   € " + String.format("%.2f", order.orderModel.total) , 20, spacing * index, paint);


            index++;
            canvas.drawLine(20, spacing * index, 320, spacing * index, paint);

            document.finishPage(page);
        }

        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/DWB Route/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        String targetPdf = directory_path + "DWB_Order_" + currentDateandTime + ".pdf";
        File filePath = new File(targetPdf);
        if (new File(targetPdf).exists()) {
            new File(targetPdf).delete();
        }
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            return "";
        }
        // close the document
        document.close();
        return targetPdf;
    }

    public static void showEditTextDialog(Context context, String message, final EditTextDialogListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final EditText edittext = new EditText(context);
        alert.setMessage(message);
        alert.setView(edittext);
        alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
            if (listener != null)
                listener.onOk(edittext.getText().toString());
        });
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();
    }

}
