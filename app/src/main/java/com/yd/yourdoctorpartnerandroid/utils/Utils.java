package com.yd.yourdoctorpartnerandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yd.yourdoctorpartnerandroid.activities.AuthActivity;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.services.CheckNetWordChangeService;
import com.yd.yourdoctorpartnerandroid.services.YDFirebaseMessagingService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getStringResourceByString(Context context, String name) {
        try {
            Resources res = context.getResources();
            return res.getString(res.getIdentifier(name, "string", context.getPackageName()));
        } catch (Exception ex) {
            return "";
        }
    }

    public static File persistImage(Bitmap bitmap, String name, Context context) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        //yyyy MM dd HH:mm:ss
        Format format = new SimpleDateFormat("HH:mm, dd/MM");
        return format.format(date);
    }

//    public static String convertTimeFromMonggo(String timeString) {
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//        Date date = null;
//        Format format2;
//        try {
//            date = format.parse(timeString);
//        } catch (Exception e) {
//            date = new Date();
//        }
//        format2 = new SimpleDateFormat("HH:mm, dd/MM ");
//        return format2.format(date);
//
//    }

    public static String formatStringNumber(int number){
        return NumberFormat.getNumberInstance(Locale.GERMAN).format(number);
    }

    public static void backToLogin(Context context) {
        String idUser = SharedPrefs.getInstance().get("USER_INFO", Doctor.class).getDoctorId();
        if(idUser != null){
            try{
                SocketUtils.getInstance().closeConnect();
                SharedPrefs.getInstance().clear();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(idUser);
                LoadDefaultModel.getInstance().unregisterServiceCheckNetwork(context);
                System.exit(0);
            }catch (Exception e){
                Log.e("LogoutFailed " , e.toString());
            }
        }
    }

    public static boolean verifyVietnameesName(String name){
        return name.matches("^[a-zA-Z_ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶ"+
                "ẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợ" +
                "ụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$");

    }

    public static String handleStringDescription(String theStrDes) {
        if(theStrDes == null) return "";
        int startString;
        if (theStrDes.contains("</br>")) {
            startString = theStrDes.lastIndexOf("</br>");

            return theStrDes.substring(startString + 5);
        }
        return theStrDes;
    }

    public static String hanleStringImage(String theStrImage) {
        if(theStrImage == null) return "";
        try {
            int startString;
            int endString;
            if (theStrImage.contains("<img")) {
                if (theStrImage.contains("data-original=")) {
                    startString = theStrImage.lastIndexOf("data-original=");

                    if (theStrImage.contains("png")) {
                        endString = theStrImage.lastIndexOf(".png");
                    } else {
                        endString = theStrImage.lastIndexOf(".jpg");
                    }

                    return theStrImage.substring(startString + 15, endString + 4);
                } else if (theStrImage.contains("src=")) {
                    startString = theStrImage.lastIndexOf("src=");

                    if (theStrImage.contains("png")) {
                        endString = theStrImage.lastIndexOf(".png");
                    } else {
                        endString = theStrImage.lastIndexOf(".jpg");
                    }

                    return theStrImage.substring(startString + 5, endString + 4);
                }
            }
            return theStrImage;
        } catch (Exception e) {
            return "";
        }
    }

}
