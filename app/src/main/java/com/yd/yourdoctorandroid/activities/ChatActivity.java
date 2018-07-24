package com.yd.yourdoctorandroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorandroid.R;
import com.yd.yourdoctorandroid.adapters.ChatAdpater;
import com.yd.yourdoctorandroid.adapters.DoctorCertificationAdapter;
import com.yd.yourdoctorandroid.managers.ScreenManager;
import com.yd.yourdoctorandroid.networks.RetrofitFactory;
import com.yd.yourdoctorandroid.networks.getChatHistory.GetChatHistoryService;
import com.yd.yourdoctorandroid.networks.getChatHistory.MainObjectChatHistory;
import com.yd.yourdoctorandroid.networks.getChatHistory.MainRecord;
import com.yd.yourdoctorandroid.networks.getDoctorDetailProfile.GetDoctorDetailService;
import com.yd.yourdoctorandroid.networks.getDoctorDetailProfile.MainObjectDetailDoctor;
import com.yd.yourdoctorandroid.networks.getDoctorDetailProfile.SpecialistDetail;
import com.yd.yourdoctorandroid.networks.getPatientDetailService.GetPatientDetailService;
import com.yd.yourdoctorandroid.networks.getPatientDetailService.MainObjectDetailPatient;
import com.yd.yourdoctorandroid.networks.models.Certification;
import com.yd.yourdoctorandroid.networks.models.Doctor;
import com.yd.yourdoctorandroid.networks.models.Patient;
import com.yd.yourdoctorandroid.networks.models.Record;
import com.yd.yourdoctorandroid.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rc_chat)
    RecyclerView recyclerView;

    @BindView(R.id.btn_image)
    ImageView btn_image;

    @BindView(R.id.btn_send)
    Button btnChat;

    @BindView(R.id.edit_message)
    EditText mEditText;

    @BindView(R.id.tb_main_chat)
    Toolbar tb_main_chat;

    @BindView(R.id.iv_done)
    ImageView iv_done;

    @BindView(R.id.iv_info)
    ImageView iv_info;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private List<Record> recordsChat;
    //private final String URL_SERVER = "https://your-doctor-test2.herokuapp.com";
    private final String URL_SERVER = "http://192.168.124.103:3000";

    private Socket mSocket;
    private ChatAdpater chatApapter;
    private MainObjectChatHistory mainObject;

    String chatHistoryID;
    Doctor currentDoctor;
    Patient patientChoice;
    String patientChoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        iv_done.setOnClickListener(this);
        iv_info.setOnClickListener(this);
        btn_image.setOnClickListener(this);
        btnChat.setOnClickListener(this);

        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        Intent intent = getIntent();


        chatHistoryID = intent.getStringExtra("chatHistoryId");
        patientChoiceId = intent.getStringExtra("patientChoiceId");

        Log.e("chatActivity ", chatHistoryID);
        Log.e("PatientChoice ", patientChoiceId);
        //doctorChoice = (Doctor) intent.getSerializableExtra("doctorChoice");

        recordsChat = new ArrayList<>();
        chatApapter = new ChatAdpater(getApplicationContext(), recordsChat, currentDoctor.getId());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatApapter);
        tb_main_chat.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tb_main_chat.setTitleTextColor(getResources().getColor(R.color.primary_text));
        tb_main_chat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backToMainActivity();
            }
        });


        try {
            mSocket = IO.socket(URL_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();

        mSocket.emit("createRoom", chatHistoryID);

        mSocket.emit("addUser", currentDoctor.getId());

        mSocket.on("newMessage", onNewMessage);

        mSocket.on("errorUpdate", onErrorUpdate);

        mSocket.on("finishConversation",onFinishMessage);

        loadPatientChoice(patientChoiceId);

    }

    @Override
    public void onBackPressed() {
        backToMainActivity();
    }

    private void loadChatDisplay() {
        GetChatHistoryService getChatHistoryService = RetrofitFactory.getInstance().createService(GetChatHistoryService.class);
        getChatHistoryService.getChatHistory(chatHistoryID).enqueue(new Callback<MainObjectChatHistory>() {
            @Override
            public void onResponse(Call<MainObjectChatHistory> call, Response<MainObjectChatHistory> response) {
                mainObject = response.body();
                if(response.code() == 200 && mainObject != null){
                    Log.e("haha chat ", mainObject.toString());
                    List<MainRecord> mainRecords = mainObject.getObjConversation().getRecords();

                    for (MainRecord mainRecord:mainRecords) {
                        Record record = new Record();
                        record.setRecorderID(mainRecord.getRecorderID());
                        record.setType(mainRecord.getType());
                        record.setValue(mainRecord.getValue());
                        record.setCreatedAt(mainRecord.getCreated());

                        if(record.getRecorderID().equals(patientChoice.getId())){
                            record.setName(patientChoice.getfName() + " " + patientChoice.getmName() + " " + patientChoice.getlName());
                            record.setAvatar(patientChoice.getAvatar());
                        }
                        recordsChat.add(record);
                    }
                    chatApapter.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MainObjectChatHistory> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Không thể tải được cuộc trò chuyện", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadPatientChoice(String patientChoiceId){
        GetPatientDetailService getPatientDetailService = RetrofitFactory.getInstance().createService(GetPatientDetailService.class);
        getPatientDetailService.getPatientDetailService(patientChoiceId).enqueue(new Callback<MainObjectDetailPatient>() {
            @Override
            public void onResponse(Call<MainObjectDetailPatient> call, Response<MainObjectDetailPatient> response) {

                if(response.code() == 200){
                    MainObjectDetailPatient mainObject = response.body();

                    patientChoice = new Patient();

                    patientChoice.setId(mainObject.getInformationPatient().get(0).getPatientId().get_id());
                    patientChoice.setfName(mainObject.getInformationPatient().get(0).getPatientId().getFirstName());
                    patientChoice.setmName(mainObject.getInformationPatient().get(0).getPatientId().getMiddleName());
                    patientChoice.setlName(mainObject.getInformationPatient().get(0).getPatientId().getLastName());
                    patientChoice.setAddress(mainObject.getInformationPatient().get(0).getPatientId().getAddress());
                    patientChoice.setAvatar(mainObject.getInformationPatient().get(0).getPatientId().getAvatar());
                    patientChoice.setBirthday(mainObject.getInformationPatient().get(0).getPatientId().getBirthday());
                    patientChoice.setPhoneNumber(mainObject.getInformationPatient().get(0).getPatientId().getPhoneNumber());

                    loadChatDisplay();
                }

            }

            @Override
            public void onFailure(Call<MainObjectDetailPatient> call, Throwable t) {
                Log.e("Anhle P error ", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Emitter.Listener onErrorUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String) args[0];
                    Toast.makeText(getApplicationContext(), "Cuộc trò chuyện đã kết thúc vì vượt quá số tin nhắn lựa chọn của bệnh nhân",Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    private Emitter.Listener onFinishMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = (String) args[0];
                    Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
//                    Log.e("emitt anh le", message);
//                    progressBar.setVisibility(View.GONE);
//                    AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
//                    alertDialog.setTitle("Xác nhận kết thúc cuộc tư vấn thành công");
//                    //mainObject.getObjConversation().getContentTopic();
//                    alertDialog.setMessage(message);
//                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    backToMainActivity();
//                                }
//                            });
//                    alertDialog.show();

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
                    //Log.e("emitt anh le", message);

                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        Record record = new Record();
                        record.setRecorderID(jsonObject.getString("senderID")) ;
                        record.setValue(jsonObject.getString("value"));
                        record.setType(Integer.parseInt(jsonObject.getString("type")));
                        record.setCreatedAt(jsonObject.getString("createTime"));
                        if(record.getRecorderID().equals(patientChoice.getId())){
                            record.setAvatar(patientChoice.getAvatar());
                            record.setName(patientChoice.getfName() + " " + patientChoice.getmName() + " " + patientChoice.getlName());
                        }

                        recordsChat.add(record);
                        chatApapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private void backToMainActivity(){
        //mSocket.emit("disconnect");
        mSocket.disconnect();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_done:{
                AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
                alertDialog.setTitle("Xác nhận việc kết thúc cuộc trò chuyện");
                //mainObject.getObjConversation().getContentTopic();
                alertDialog.setMessage("Bạn có chắc muốn kết thúc cuộc trò chuyện không ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hủy",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setVisibility(View.VISIBLE);
                                mSocket.emit("doneConversation", currentDoctor.getId(), patientChoice.getId(),chatHistoryID);
                            }
                        });
                alertDialog.show();
                break;
            }
            case R.id.iv_info:{
                AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
                alertDialog.setTitle("Chat với BN  "+ patientChoice.getfName() + " " + patientChoice.getmName() + " " + patientChoice.getlName());
                //mainObject.getObjConversation().getContentTopic();
                alertDialog.setMessage("Nội dung : " + mainObject.getObjConversation().getContentTopic());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                break;
            }
            case R.id.btn_image:{
                break;

            }
            case R.id.btn_send:{
                mSocket.emit("sendMessage", currentDoctor.getId(), patientChoice.getId(), chatHistoryID, 1, mEditText.getText().toString());
                mEditText.setText("");
                break;
            }
        }
    }
}

