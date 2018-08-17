
package com.yd.yourdoctorpartnerandroid.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.DoctorApplication;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.ChatAdapter;
import com.yd.yourdoctorpartnerandroid.events.EventSend;
import com.yd.yourdoctorpartnerandroid.fragments.ConfirmEndChatFragment;
import com.yd.yourdoctorpartnerandroid.fragments.DoctorProfileFragment;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.cancelChatService.CancelChatService;
import com.yd.yourdoctorpartnerandroid.networks.cancelChatService.MainResponseCancelChat;
import com.yd.yourdoctorpartnerandroid.networks.changePassword.ChangePasswordService;
import com.yd.yourdoctorpartnerandroid.networks.changePassword.PasswordRequest;
import com.yd.yourdoctorpartnerandroid.networks.changePassword.PasswordResponse;
import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.GetChatHistoryService;
import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.MainObjectChatHistory;
import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.MainRecord;
import com.yd.yourdoctorpartnerandroid.networks.getLinkImageService.GetLinkImageService;
import com.yd.yourdoctorpartnerandroid.networks.getLinkImageService.MainGetLink;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.models.Record;
import com.yd.yourdoctorpartnerandroid.networks.getPatientDetailService.GetPatientDetailService;
import com.yd.yourdoctorpartnerandroid.networks.getPatientDetailService.MainObjectDetailPatient;
import com.yd.yourdoctorpartnerandroid.networks.reportService.MainResponReport;
import com.yd.yourdoctorpartnerandroid.networks.reportService.ReportRequest;
import com.yd.yourdoctorpartnerandroid.networks.reportService.ReportService;
import com.yd.yourdoctorpartnerandroid.utils.ImageUtils;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;
import com.yd.yourdoctorpartnerandroid.utils.Utils;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rcChat)
    RecyclerView recyclerView;

    @BindView(R.id.btnImage)
    ImageView btnImage;

    @BindView(R.id.btnSend)
    Button btnChat;

    @BindView(R.id.editMessage)
    EditText mEditText;

    @BindView(R.id.tbMainChat)
    Toolbar tbMainChat;

    @BindView(R.id.ivInfo)
    ImageView ivInfo;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.rlMessageImage)
    RelativeLayout rlMessageImage;

    @BindView(R.id.ivMessage)
    ImageView ivMessage;

    @BindView(R.id.ivCancel)
    ImageView ivCancel;

    @BindView(R.id.ivCancelChat)
    ImageView ivCancelChat;

    private boolean isDone;

    private EditText etReasonReport;
    private AlertDialog dialogReport;

    private List<Record> recordsChat;
    public static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private AlertDialog alertDialog;

    private ChatAdapter chatApapter;
    private MainObjectChatHistory mainObject;

    ImageUtils imageUtils;

    String chatHistoryID;
    Doctor currentDoctor;
    Patient patientChoice;
    String patientChoiceId;

    private int typeChatCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);
        Socket socket = DoctorApplication.self().getSocket();
        socket.connect();
        ivInfo.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        ivCancelChat.setOnClickListener(this);
        isDone = false;
        imageUtils = new ImageUtils(this);

        typeChatCurrent = 1;
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        Intent intent = getIntent();

        chatHistoryID = intent.getStringExtra("chatHistoryId");
        patientChoiceId = intent.getStringExtra("patientChoiceId");
        alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
        Log.e("chat activity ", chatHistoryID);
        Log.e("patient Choice ", patientChoiceId);

        recordsChat = new ArrayList<>();
        chatApapter = new ChatAdapter(this, recordsChat, currentDoctor.getDoctorId());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatApapter);

        tbMainChat.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tbMainChat.setTitleTextColor(getResources().getColor(R.color.primary_text));
        tbMainChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        });
        if(!SocketUtils.getInstance().checkIsConnected()){
            Toast.makeText(getApplicationContext(),"Không kết nối được máy chủ",Toast.LENGTH_LONG);
            return;
        }
        SocketUtils.getInstance().getSocket().emit("joinRoom", chatHistoryID);
        SocketUtils.getInstance().setRoomId(chatHistoryID);

        SocketUtils.getInstance().getSocket().on("newMessage", onNewMessage);

        SocketUtils.getInstance().getSocket().on("errorUpdate", onErrorUpdate);

        SocketUtils.getInstance().getSocket().on("finishConversation", onFinishMessage);

        SocketUtils.getInstance().getSocket().on("conversationDone", onConversationDone);

        loadPatientChoice(patientChoiceId);

    }

    @Override
    public void onBackPressed() {
        backToMainActivity();
    }

    private void loadChatDisplay() {
        GetChatHistoryService getChatHistoryService = RetrofitFactory.getInstance().createService(GetChatHistoryService.class);
        getChatHistoryService.getChatHistory(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), chatHistoryID).enqueue(new Callback<MainObjectChatHistory>() {
            @Override
            public void onResponse(Call<MainObjectChatHistory> call, Response<MainObjectChatHistory> response) {
                mainObject = response.body();
                if (response.code() == 200 && mainObject != null) {
                    List<MainRecord> mainRecords = mainObject.getObjConversation().getRecords();
                    if(mainObject.getObjConversation().getStatus() == 2){
                        isDone = true;
                    }
                    for (MainRecord mainRecord : mainRecords) {
                        Record record = new Record();
                        record.setRecorderID(mainRecord.getRecorderID());
                        record.setType(mainRecord.getType());
                        record.setValue(mainRecord.getValue());
                        try {
                            record.setCreatedAt(Utils.convertTimeFromMonggo(mainRecord.getCreated()));
                        } catch (Exception e) {
                            record.setCreatedAt(new Date().toString());
                        }


                        if (record.getRecorderID().equals(patientChoice.getId())) {
                            record.setName(patientChoice.getFullName());
                            record.setAvatar(patientChoice.getAvatar());
                        }
                        recordsChat.add(record);
                    }
                    if (recordsChat.size() > 0) {
                        recyclerView.smoothScrollToPosition(recordsChat.size() - 1);
                    }
                    chatApapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    Utils.backToLogin(getApplicationContext());
                }

                if(progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MainObjectChatHistory> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Không thể tải được cuộc trò chuyện", Toast.LENGTH_LONG).show();
                if(progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadPatientChoice(String patientChoiceId) {
        GetPatientDetailService getPatientDetailService = RetrofitFactory.getInstance().createService(GetPatientDetailService.class);
        getPatientDetailService.getPatientDetailService(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), patientChoiceId).enqueue(new Callback<MainObjectDetailPatient>() {
            @Override
            public void onResponse(Call<MainObjectDetailPatient> call, Response<MainObjectDetailPatient> response) {

                if (response.code() == 200) {
                    MainObjectDetailPatient mainObject = response.body();
                    patientChoice = new Patient();
                    patientChoice.setId(mainObject.getInformationPatient().getPatientId().get_id());
                    patientChoice.setfName(mainObject.getInformationPatient().getPatientId().getFirstName());
                    patientChoice.setmName(mainObject.getInformationPatient().getPatientId().getMiddleName());
                    patientChoice.setlName(mainObject.getInformationPatient().getPatientId().getLastName());
                    patientChoice.setAddress(mainObject.getInformationPatient().getPatientId().getAddress());
                    patientChoice.setAvatar(mainObject.getInformationPatient().getPatientId().getAvatar());
                    patientChoice.setBirthday(mainObject.getInformationPatient().getPatientId().getBirthday());
                    patientChoice.setPhoneNumber(mainObject.getInformationPatient().getPatientId().getPhoneNumber());
                    tbMainChat.setTitle("Bs." + patientChoice.getFullName());
                    Log.e("patient is ", patientChoice.getlName());

                    loadChatDisplay();
                } else {
                    if(progressBar != null)progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "loi khi tai patient", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<MainObjectDetailPatient> call, Throwable t) {
                Log.e("Anhle P error ", t.toString());
                if(progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }


    private Emitter.Listener onConversationDone = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // chi hien thong bao
                    String message = (String) args[0];
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    if(progressBar != null) progressBar.setVisibility(View.GONE);

                }
            });
        }
    };

    private Emitter.Listener onErrorUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(progressBar != null) progressBar.setVisibility(View.GONE);
                    String message = (String) args[0];
                    //showMessageConfirm(message);
                    try{
                        ConfirmEndChatFragment confirmEndChatFragment = new ConfirmEndChatFragment();
                        confirmEndChatFragment.setData(currentDoctor,patientChoice,message);
                        ScreenManager.openFragment(getSupportFragmentManager(), confirmEndChatFragment, R.id.rl_chat, true, true);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }



                }
            });
        }
    };

    private Emitter.Listener onFinishMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(progressBar != null) progressBar.setVisibility(View.GONE);
                    String message = (String) args[0];
                    //showMessageConfirm(message);
                    Log.e("emitt anh le", message);

                    try{
                        ConfirmEndChatFragment confirmEndChatFragment = new ConfirmEndChatFragment();
                        confirmEndChatFragment.setData(currentDoctor,patientChoice,message);
                        ScreenManager.openFragment(getSupportFragmentManager(), confirmEndChatFragment, R.id.rl_chat, true, true);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    };


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    String message;
                    message = data.optString("data");

                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        Record record = new Record();
                        record.setRecorderID(jsonObject.getString("senderID"));
                        record.setValue(jsonObject.getString("value"));
                        record.setType(Integer.parseInt(jsonObject.getString("type")));
                        record.setCreatedAt(Utils.convertTime(Long.parseLong(jsonObject.getString("createTime"))));

                        if (record.getRecorderID().equals(patientChoiceId)) {
                            record.setAvatar(patientChoice.getAvatar());
                            record.setName(patientChoice.getFullName());
                        }

                        recordsChat.add(record);
                        if (recordsChat.size() > 0) {
                            recyclerView.smoothScrollToPosition(recordsChat.size() - 1);
                        }
                        chatApapter.notifyDataSetChanged();
                        if(progressBar != null) progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(progressBar != null) progressBar.setVisibility(View.GONE);
                    }

                }
            });
        }
    };

    private void backToMainActivity() {
        SocketUtils.getInstance().setRoomId(null);
        SocketUtils.getInstance().getSocket().emit("leaveRoom", chatHistoryID);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivInfo: {
                showDialogInfo();
                break;
            }
            case R.id.btnImage: {
                imageUtils.displayAttachImageDialog();
                break;

            }
            case R.id.btnSend: {
                handleSendMessageChat();
                break;
            }
            case R.id.ivCancel: {
                setTypeChat(1);
                break;
            }
            case R.id.ivCancelChat:{
                handleCancelChat();
                break;
            }
        }
    }

    //Dialog Info
    private ImageView ivPatientChat;

    private TextView tvNamePatientChat;

    private TextView tvBirthDayChat;

    private TextView tvContentChat;

    private TextView tvAddressChat;

    private Button btnRateChatPatient;

    private Button btnOkInfoPatient;


    private void handleCancelChat(){
        new AlertDialog.Builder(ChatActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Xác nhận việc hủy cuộc tư vấn với BN.")
                .setMessage("Bạn sẽ không nhận được tiền công nếu hủy, bạn có chắc chắn muốn hủy bỏ cuộc tư vấn không? ")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!isDone){
                            //TODO API
                            if(progressBar != null) progressBar.setVisibility(View.VISIBLE);

                            CancelChatService cancelChatService = RetrofitFactory.getInstance().createService(CancelChatService.class);
                            cancelChatService.cancelChatService(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), chatHistoryID).enqueue(new Callback<MainResponseCancelChat>() {
                                @Override
                                public void onResponse(Call<MainResponseCancelChat> call, Response<MainResponseCancelChat> response) {
                                    if (response.code() == 200 && response.body().isSuccess()) {
                                        isDone = true;
                                        Toast.makeText(getApplicationContext(), "Hủy cuộc chat thành công", Toast.LENGTH_LONG).show();
                                    } else if (response.code() == 401) {
                                        Utils.backToLogin(getApplicationContext());
                                    }
                                    if(progressBar != null) progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(Call<MainResponseCancelChat> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Lỗi kết máy chủ, không thể thực hiện yêu cầu", Toast.LENGTH_LONG).show();
                                    if(progressBar != null) progressBar.setVisibility(View.GONE);
                                }
                            });

                        }else {
                            Toast.makeText(getApplicationContext(),"Cuộc tư vấn đã kết thúc trước đó", Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void showDialogInfo() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        dialog.setContentView(R.layout.info_chat_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ivPatientChat = dialog.findViewById(R.id.ivPatientChat);
        tvNamePatientChat = dialog.findViewById(R.id.tvNamePatientChat);
        tvBirthDayChat = dialog.findViewById(R.id.tvBirthDayChat);
        tvAddressChat = dialog.findViewById(R.id.tvAddressChat);
        tvContentChat = dialog.findViewById(R.id.tvContentChat);
        btnRateChatPatient = dialog.findViewById(R.id.btn_rate_chat_patient);
        btnOkInfoPatient = dialog.findViewById(R.id.btn_ok_info_patient);
        tvContentChat.setMovementMethod(new ScrollingMovementMethod());
        //set up data
        if (patientChoice != null) {
            ZoomImageViewUtils.loadCircleImage(getApplicationContext(), patientChoice.getAvatar(), ivPatientChat);
            tvNamePatientChat.setText("BN. " + patientChoice.getFullName());
            tvBirthDayChat.setText("NS: " + patientChoice.getBirthday());
            tvAddressChat.setText("ĐC: " + patientChoice.getAddress());
        }
        if (mainObject.getObjConversation() != null) {
            tvContentChat.setText("Nội dung câu hỏi là: " + mainObject.getObjConversation().getContentTopic());
        }

        // if button is clicked, close the custom dialog
        btnOkInfoPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnRateChatPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                reportPatient();
            }
        });
        dialog.show();
    }

    private ProgressBar pbInforChat;

    public void reportPatient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.report_user_dialog, null);
        etReasonReport = view.findViewById(R.id.et_reason_report);
        pbInforChat = view.findViewById(R.id.pb_infor_chat);
        if(pbInforChat != null) pbInforChat.setVisibility(View.GONE);
        builder.setView(view);
        if (patientChoice != null) {
            builder.setTitle("Báo cáo BN." + patientChoice.getFullName());
        }
        builder.setPositiveButton("Báo cáo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogReport = builder.create();
        dialogReport.show();
        dialogReport.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pbInforChat != null) pbInforChat.setVisibility(View.VISIBLE);
                if (etReasonReport.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Bạn phải nhập lý do", Toast.LENGTH_LONG).show();
                    if(pbInforChat != null) pbInforChat.setVisibility(View.GONE);
                } else {
                    ReportRequest reportRequest = new ReportRequest();
                    reportRequest.setIdPersonBeingReported(patientChoiceId);
                    reportRequest.setIdReporter(currentDoctor.getDoctorId());
                    reportRequest.setReason(etReasonReport.getText().toString());

                    ReportService reportService = RetrofitFactory.getInstance().createService(ReportService.class);
                    reportService.reportService(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), reportRequest).enqueue(new Callback<MainResponReport>() {
                        @Override
                        public void onResponse(Call<MainResponReport> call, Response<MainResponReport> response) {
                            Log.e("Anh le doctor  ", "post submitted to API." + response.body().toString());
                            if (response.code() == 200 && response.body().isSuccess()) {
                                etReasonReport.setText("");
                                Toast.makeText(getApplicationContext(), "Báo cáo người dùng thành công", Toast.LENGTH_LONG).show();
                            } else if (response.code() == 401) {
                                Utils.backToLogin(getApplicationContext());
                            }
                            if(pbInforChat != null) pbInforChat.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<MainResponReport> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Lỗi kết máy chủ", Toast.LENGTH_LONG).show();
                            if(pbInforChat != null) pbInforChat.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });

    }

    private void handleSendMessageChat() {
        if(progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (typeChatCurrent == 1) {
            if (mEditText.getText().equals("")) {
                Toast.makeText(this, "Bạn nên nhập tin nhắn trước", Toast.LENGTH_LONG).show();
                if(progressBar != null) progressBar.setVisibility(View.GONE);
            } else {
                if(!SocketUtils.getInstance().checkIsConnected()){
                    if(progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Không có kết nối",Toast.LENGTH_LONG).show();
                }else {
                    SocketUtils.getInstance().getSocket().emit("sendMessage", currentDoctor.getDoctorId(), patientChoiceId, chatHistoryID, 1, mEditText.getText().toString().trim());
                    mEditText.setText("");

                }
                //SocketUtils.getInstance().getSocket().emit("sendMessage", currentDoctor.getDoctorId(), patientChoiceId, chatHistoryID, 1, mEditText.getText().toString());
            }

        } else {
            if (imageUtils.getImageUpload() == null) {
                if(progressBar != null) progressBar.setVisibility(View.GONE);
                return;
            }

            GetLinkImageService getLinkeImageService = RetrofitFactory.getInstance().createService(GetLinkImageService.class);
            getLinkeImageService.uploadImageToGetLink(imageUtils.getImageUpload()).enqueue(new Callback<MainGetLink>() {
                @Override
                public void onResponse(Call<MainGetLink> call, Response<MainGetLink> response) {
                    Log.e("linkImage", response.body().getFilePath());

                    if (response.code() == 200) {
                        Log.e("linkSuccess", response.body().getFilePath());
                        MainGetLink mainObject = response.body();
                        if(!SocketUtils.getInstance().checkIsConnected()){
                            if(progressBar != null) progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Không có kết nối",Toast.LENGTH_LONG).show();
                        }else {
                            SocketUtils.getInstance().getSocket().emit("sendMessage", currentDoctor.getDoctorId(), patientChoiceId, chatHistoryID, 2, mainObject.getFilePath());
                            setTypeChat(1);
                        }
                    } else if (response.code() == 401) {
                        Utils.backToLogin(getApplicationContext());
                    } else {
                        Log.e("not200", "anhle");
                        if(progressBar != null) progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<MainGetLink> call, Throwable t) {
                    Log.e("Anhlelink", t.toString());
                    if(progressBar != null) progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setTypeChat(int typeChat) {
        ivMessage.setImageResource(R.drawable.ic_image_black_24dp);
        imageUtils.clearAll();
        if (typeChat == 1) {
            typeChatCurrent = 1;
            mEditText.setVisibility(View.VISIBLE);
            rlMessageImage.setVisibility(View.GONE);
            mEditText.setText("");
        } else {
            typeChatCurrent = 2;
            mEditText.setVisibility(View.GONE);
            rlMessageImage.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(imageUtils.getmImagePathToBeAttached());
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageUtils.getmImagePathToBeAttached(), options);
                options.inJustDecodeBounds = false;
                imageUtils.setmImageToBeAttached(BitmapFactory.decodeFile(imageUtils.getmImagePathToBeAttached(), options));
                try {
                    ExifInterface exif = new ExifInterface(imageUtils.getmImagePathToBeAttached());
                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                    int rotationAngle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle, (float) imageUtils.getmImageToBeAttached().getWidth() / 2, (float) imageUtils.getmImageToBeAttached().getHeight() / 2);
                    imageUtils.setmImageToBeAttached(Bitmap.createBitmap(imageUtils.getmImageToBeAttached(), 0, 0, options.outWidth, options.outHeight, matrix, true));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("loi chup anh", ex.toString());
                }
                file.delete();
            }
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {
                Uri uri = data.getData();
                ContentResolver resolver = getContentResolver();
                int rotationAngle = imageUtils.getOrientation(this, uri);
                imageUtils.setmImageToBeAttached(MediaStore.Images.Media.getBitmap(resolver, uri));
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) imageUtils.getmImageToBeAttached().getWidth() / 2, (float) imageUtils.getmImageToBeAttached().getHeight() / 2);
                imageUtils.setmImageToBeAttached(Bitmap.createBitmap(imageUtils.getmImageToBeAttached(), 0, 0, imageUtils.getmImageToBeAttached().getWidth(), imageUtils.getmImageToBeAttached().getHeight(), matrix, true));
            } catch (IOException e) {
                Log.e("loiAnhLe", "Cannot get a selected photo from the gallery.", e);
            }
        }
        updateUI();
    }

    public void updateUI() {
        typeChatCurrent = 2;
        mEditText.setVisibility(View.GONE);
        rlMessageImage.setVisibility(View.VISIBLE);
        if (imageUtils.getmImageToBeAttached() != null) {
            ivMessage.setImageBitmap(imageUtils.getmImageToBeAttached());
        } else {
            ivMessage.setImageResource(R.drawable.ic_image_black_24dp);
        }
    }

}

