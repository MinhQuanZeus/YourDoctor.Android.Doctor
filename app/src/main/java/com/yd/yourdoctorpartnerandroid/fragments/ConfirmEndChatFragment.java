package com.yd.yourdoctorpartnerandroid.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.reportConversation.ReportConversation;
import com.yd.yourdoctorpartnerandroid.networks.reportConversation.RequestReportConversation;
import com.yd.yourdoctorpartnerandroid.networks.reportConversation.ResponseReportConversation;
import com.yd.yourdoctorpartnerandroid.networks.reportService.MainResponReport;
import com.yd.yourdoctorpartnerandroid.networks.reportService.ReportRequest;
import com.yd.yourdoctorpartnerandroid.networks.reportService.ReportService;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmEndChatFragment extends Fragment {


    @BindView(R.id.tbConfirmEnd)
    Toolbar tbConfirmEnd;
    @BindView(R.id.iv_ava_confirm)
    ImageView iv_ava_confirm;
    @BindView(R.id.tv_logo_confirm)
    TextView tv_logo_confirm;
    @BindView(R.id.tv_messgae_confirm)
    TextView tv_messgae_confirm;
    @BindView(R.id.btn_report)
    Button btn_report;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    private Patient currentPatient;
    private Doctor currentDoctor;
    private String message;
    private String chatHistoryID;

    public ConfirmEndChatFragment() {
        // Required empty public constructor
    }

    public void setData(Doctor currentDoctor,Patient currentPatient,String message, String chatHistoryID ){
        this.currentDoctor = currentDoctor;
        this.currentPatient = currentPatient;
        this.message = message;
        this.chatHistoryID = chatHistoryID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_end_chat, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this,view);
        setUpUI();

        return view;
    }

    private void setUpUI() {

        tbConfirmEnd.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tbConfirmEnd.setTitle("Xác nhận Kết thúc tư vấn");
        tbConfirmEnd.setTitleTextColor(getResources().getColor(R.color.primary_text));
        tbConfirmEnd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScreenManager.backFragment(getFragmentManager());
            }
        });

        ZoomImageViewUtils.loadCircleImage(getContext(),currentPatient.getAvatar(),iv_ava_confirm);
        tv_logo_confirm.setText("Cuộc tư vấn với BN." + currentPatient.getFullName() + " kết thúc");
        tv_messgae_confirm.setText(message);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScreenManager.backFragment(getActivity().getSupportFragmentManager());
            }
        });


        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportConversation();
            }
        });

    }


    private EditText etReasonReport;
    private AlertDialog dialogReport;
    private ProgressBar pbInforChat;

    private void reportConversation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.report_user_dialog, null);
        etReasonReport = view.findViewById(R.id.et_reason_report);
        pbInforChat = view.findViewById(R.id.pb_infor_chat);
        if (pbInforChat != null) pbInforChat.setVisibility(View.GONE);

        builder.setView(view);
        if (currentPatient != null) {
            builder.setTitle("Báo cáo cuộc tư vấn của BN." + currentPatient.getFullName());
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
                if (pbInforChat != null) {
                    pbInforChat.setVisibility(View.VISIBLE);
                }
                if (etReasonReport.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Bạn phải nhập lý do", Toast.LENGTH_LONG).show();
                    if (pbInforChat != null) {
                        pbInforChat.setVisibility(View.GONE);
                    }
                } else {
                    RequestReportConversation reportRequest = new RequestReportConversation(currentDoctor.getDoctorId(),
                            currentPatient.getId(), etReasonReport.getText().toString().trim(), chatHistoryID, 1);

                    ReportConversation reportConversation = RetrofitFactory.getInstance().createService(ReportConversation.class);
                    reportConversation.reportConversations(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), reportRequest).enqueue(new Callback<ResponseReportConversation>() {
                        @Override
                        public void onResponse(Call<ResponseReportConversation> call, Response<ResponseReportConversation> response) {
                            if (response.code() == 200 && response.body().isSuccess()) {
                                etReasonReport.setText("");
                                Toast.makeText(getContext(), "Báo cáo cuộc tư vấn thành công", Toast.LENGTH_LONG).show();
                            } else if (response.code() == 401) {
                                Utils.backToLogin(getContext());
                            } else {
                                Toast.makeText(getContext(), "Bạn đã báo cáo trước đó!", Toast.LENGTH_LONG).show();
                            }

                            if (pbInforChat != null) {
                                pbInforChat.setVisibility(View.GONE);
                            }
                            dialogReport.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseReportConversation> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết máy chủ", Toast.LENGTH_LONG).show();
                            if (pbInforChat != null) {
                                pbInforChat.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        });

    }

}
