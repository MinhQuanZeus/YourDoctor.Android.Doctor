package com.yd.yourdoctorpartnerandroid.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.activities.MainActivity;
import com.yd.yourdoctorpartnerandroid.models.VideoCallSession;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoneVideoCallFragment extends Fragment {

    @BindView(R.id.tv_done)
    TextView tvDone;
    @BindView(R.id.btn_ok)
    CircularProgressButton btnOk;
    Unbinder unbinder;
    private int timeCall;
    private VideoCallSession videoCallSession;

    public DoneVideoCallFragment setVideoCallSession(VideoCallSession videoCallSession, int timeCall) {
        this.timeCall = timeCall;
        this.videoCallSession = videoCallSession;
        return this;
    }

    public DoneVideoCallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_done_video_call, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        unbinder = ButterKnife.bind(this, view);
        int min = timeCall / 60;
        int sec = timeCall % 60;
        String message = "Bạn vừa kết thúc cuộc tư vấn với bệnh nhân "
                + videoCallSession.getCallerName() + " trong " + min + "min" + sec + "s. \n"
                + "Chi tiết số tiền bạn nhận được sẽ được chúng tôi gửi lại sau.\n"
                + "Cảm ơn bạn đã tham gia cùng chúng tôi!";
        tvDone.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });
    }

}
