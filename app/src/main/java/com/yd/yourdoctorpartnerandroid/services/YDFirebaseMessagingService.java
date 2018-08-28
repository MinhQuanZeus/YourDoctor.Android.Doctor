package com.yd.yourdoctorpartnerandroid.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.activities.AuthActivity;
import com.yd.yourdoctorpartnerandroid.activities.ChatActivity;
import com.yd.yourdoctorpartnerandroid.events.EventSend;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.utils.NotificationUtils;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;

import org.greenrobot.eventbus.EventBus;


public class YDFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = YDFirebaseMessagingService.class.getSimpleName();
    private String senderId;
    private String nameSender;
    private String receiveId;
    private int type;
    private String storageId;
    private String message;
    private String createTime;
    private String remainMoney;

    private String title;
    private String description;

    static int id = 1;

    private Intent intent;
    private PendingIntent pendingIntent;
    private NotificationCompat.Builder builder;
    private NotificationManager notifManager;
    private Doctor doctor;
    private TaskStackBuilder stackBuilder;

    public YDFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            Log.e(TAG, "Notify is null");
            return;
        } else {
            senderId = remoteMessage.getData().get("senderId");
            nameSender = remoteMessage.getData().get("nameSender");
            receiveId = remoteMessage.getData().get("receiveId");
            type = Integer.parseInt(remoteMessage.getData().get("type"));
            storageId = remoteMessage.getData().get("storageId");
            message = remoteMessage.getData().get("message");
            createTime = remoteMessage.getData().get("createTime");
            remainMoney = remoteMessage.getData().get("remainMoney");
            if(SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
                EventBus.getDefault().postSticky(new EventSend(3));
                showNotification();
            }

        }
    }

    private void showNotification() {
        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id + "");
            if (mChannel == null) {
                mChannel = new NotificationChannel(id + "", "YourDoctor", importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id + "");

            switch (type) {
                case 1: {
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    Log.e("Notify is here", "Notify is here");
                    Log.e("sender id " , senderId);
                    Log.e("storageId" , storageId);
                    intent.putExtra("chatHistoryId", storageId);
                    intent.putExtra("patientChoiceId", senderId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                   //pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                    builder.setContentTitle("Thông báo chat")  // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    break;
                }
                case 3: {
                    if(remainMoney != null && !remainMoney.equals("") && SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
                        doctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
                        try{
                            doctor.setRemainMoney(Float.parseFloat(remainMoney));
                            SharedPrefs.getInstance().put("USER_INFO", doctor);
                            EventBus.getDefault().postSticky(new EventSend(1));
                        }catch (Exception e){
                            Log.e("LoiMessageFirebase :", "remainMoney");
                        }

                    }

                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    builder.setContentTitle("Thông báo Thanh Toán")  // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    break;
                }case 4:{
                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    builder.setContentTitle("Thông báo Thanh Toán rút tiền")  // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    break;
                }
                case 5: {
                    if(remainMoney != null && !remainMoney.equals("") && SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
                        doctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
                        try{
                            doctor.setRemainMoney(Float.parseFloat(remainMoney));
                            SharedPrefs.getInstance().put("USER_INFO", doctor);
                            EventBus.getDefault().postSticky(new EventSend(1));
                        }catch (Exception e){
                            Log.e("LoiMessageFirebase :", "remainMoney");
                        }

                    }

                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    builder.setContentTitle("Thông báo báo cáo người dùng")  // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    break;
                }
            }

        } else {
            builder = new NotificationCompat.Builder(this);

            switch (type) {
                case 1: {
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("chatHistoryId", storageId);
                    intent.putExtra("patientChoiceId", senderId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    builder.setContentTitle("Thông báo chat")                           // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setTicker(message)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setPriority(Notification.PRIORITY_HIGH);
                    break;
                }
                case 3: {
                    if(remainMoney != null && !remainMoney.equals("") && SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
                        doctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
                        try{
                            doctor.setRemainMoney(Float.parseFloat(remainMoney));
                            SharedPrefs.getInstance().put("USER_INFO", doctor);
                            EventBus.getDefault().postSticky(new EventSend(1));
                        }catch (Exception e){
                            Log.e("LoiMessageFirebase :", "remainMoney");
                        }

                    }
                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    builder.setContentTitle("Thông báo thanh toán")                           // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setTicker(message)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setPriority(Notification.PRIORITY_HIGH);
                    break;
                }
                case 4:{
                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    builder.setContentTitle("Thông báo Rút tiền")                           // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setTicker(message)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setPriority(Notification.PRIORITY_HIGH);
                }
                case 5: {
                    if(remainMoney != null && !remainMoney.isEmpty() && SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
                        doctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
                        try{
                            doctor.setRemainMoney(Float.parseFloat(remainMoney));
                            SharedPrefs.getInstance().put("USER_INFO", doctor);
                            EventBus.getDefault().postSticky(new EventSend(1));
                        }catch (Exception e){
                            Log.e("LoiMessageFirebase :", "remainMoney");
                        }

                    }
                    intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntent(intent);
                    pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    builder.setContentTitle("Thông báo báo cáo người dùng")                           // required
                            .setSmallIcon(R.drawable.your_doctor_logo) // required
                            .setContentText(message)  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentIntent(pendingIntent)
                            .setTicker(message)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setPriority(Notification.PRIORITY_HIGH);
                    break;
                }

            }
        }

        Notification notification = builder.build();
        notifManager.notify(id, notification);
        id++;
    }
}

