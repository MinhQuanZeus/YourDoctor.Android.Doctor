package com.yd.yourdoctorpartnerandroid.fragments;


import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.DoctorApplication;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.activities.MainActivity;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.VideoCallSession;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingCallFragment extends Fragment {

    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.iv_avatar_back_ground)
    ImageView ivAvatarBackground;
    @BindView(R.id.iv_accept)
    ImageView ivAccept;
    @BindView(R.id.iv_decline)
    ImageView ivDecline;
    private Socket client;
    Unbinder unbinder;

    VideoCallSession videoCallSession;
    private MediaPlayer mMediaPlayer;
    public IncomingCallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_incoming_call, container, false);
        init(view);
        return view;
    }

    public IncomingCallFragment setVideoCallSession(VideoCallSession videoCallSession) {
        this.videoCallSession = videoCallSession;
        return this;
    }

    private void init(View view) {
        unbinder = ButterKnife.bind(this, view);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(getContext(), R.raw.skype_call);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        Picasso.with(getContext()).load(videoCallSession.getCallerAvatar()).transform(new CropCircleTransformation()).into(ivAvatar);
        Picasso.with(getContext()).load(videoCallSession.getCallerAvatar()).into(ivAvatarBackground);
        tvUsername.setText(videoCallSession.getCallerName());
        client = DoctorApplication.self().getSocket();
        client.connect();
        client.on("onCallerReject", onCallerReject);
        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenManager.openFragment(getActivity().getSupportFragmentManager(), new CallingFragment().setVideoCallSession(videoCallSession), R.id.fl_video_call, false, true);
            }
        });

        ivDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declined();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
    }

    private Emitter.Listener onCallerReject = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
        }
    };

    public void declined() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", "incomingCallResponse");
            obj.put("from", videoCallSession.getCallerId());
            obj.put("callResponse", "reject");
            obj.put("message", "user declined");
            Log.d("declined", obj.toString());
            client.emit("incomingCallResponse", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
    }
}
