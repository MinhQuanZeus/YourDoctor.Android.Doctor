package com.yd.yourdoctorpartnerandroid.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nhancv.npermission.NPermission;
import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionClient;
import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionParameters;
import com.nhancv.webrtcpeer.rtc_peer.SignalingEvents;
import com.nhancv.webrtcpeer.rtc_peer.SignalingParameters;
import com.nhancv.webrtcpeer.rtc_peer.config.DefaultConfig;
import com.nhancv.webrtcpeer.rtc_plugins.ProxyRenderer;
import com.nhancv.webrtcpeer.rtc_plugins.RTCAudioManager;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.DoctorApplication;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.activities.MainActivity;
import com.yd.yourdoctorpartnerandroid.kurento.KurentoRTCCClient;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.TypeCall;
import com.yd.yourdoctorpartnerandroid.models.VideoCallSession;
import com.yd.yourdoctorpartnerandroid.utils.RxScheduler;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallingFragment extends Fragment implements IKurentoFragment, SignalingEvents, PeerConnectionClient.PeerConnectionEvents {
    public static final String STREAM_HOST = "https://kurento-socket.herokuapp.com/";
    public static final String TAG = "KurentoActivity";
    private SurfaceViewRenderer vGLSurfaceViewCallFull;
    private SurfaceViewRenderer vGLSurfaceViewCallPip;
    private EglBase rootEglBase;
    private ProxyRenderer localProxyRenderer;
    private ProxyRenderer remoteProxyRenderer;
    private Toast logToast;
    private boolean isGranted;
    private boolean isSwappedFeeds;

    private Socket client;

    //Present
    private Gson gson;

    private PeerConnectionClient peerConnectionClient;
    private KurentoRTCCClient rtcClient;
    private PeerConnectionParameters peerConnectionParameters;
    private DefaultConfig defaultConfig;
    private RTCAudioManager audioManager;
    private SignalingParameters signalingParameters;
    private boolean iceConnected;

    private VideoCallSession videoCallSession;
    private MediaPlayer mMediaPlayer;
    private Timer timer;
    private TimerTask timerTask;
    private int timeCounter = 0;
    private Handler handler = new Handler();

    @BindView(R.id.rl_timer)
    RelativeLayout rlTimer;
    @BindView(R.id.tv_calling_doctor)
    TextView tvCallingDoctor;
    @BindView(R.id.tv_timer)
    TextView tvTimer;
    @BindView(R.id.rl_calling)
    RelativeLayout rlCalling;
    @BindView(R.id.iv_avatar)
    CircleImageView ivCalleeAvatar;
    @BindView(R.id.tv_username)
    TextView tvCalleeName;
    @BindView(R.id.iv_decline)
    ImageView ivDecline;
    Unbinder unbinder;

    public CallingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calling, container, false);
        init(view);
        return view;
    }

    private void init(View viewFragment) {
        unbinder = ButterKnife.bind(this, viewFragment);
        vGLSurfaceViewCallPip = (SurfaceViewRenderer) viewFragment.findViewById(R.id.vGLSurfaceViewCallPip);
        vGLSurfaceViewCallFull = (SurfaceViewRenderer) viewFragment.findViewById(R.id.vGLSurfaceViewCallFull);

        rlTimer.setVisibility(View.GONE);
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        //config peer
        localProxyRenderer = new ProxyRenderer();
        remoteProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCallFull.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCallFull.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCallFull.setEnableHardwareScaler(true);
        vGLSurfaceViewCallFull.setMirror(true);

        vGLSurfaceViewCallPip.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCallPip.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCallPip.setEnableHardwareScaler(true);
        vGLSurfaceViewCallPip.setMirror(true);
        vGLSurfaceViewCallPip.setZOrderMediaOverlay(true);

        // Swap feeds on pip view click.
        vGLSurfaceViewCallPip.setOnClickListener(view -> setSwappedFeeds(!isSwappedFeeds));

        setSwappedFeeds(true);

        client = DoctorApplication.self().getSocket();
        client.connect();

        connectServer();

        if (videoCallSession.getType() == TypeCall.CALL) {
            ZoomImageViewUtils.loadCircleImage(getContext(),videoCallSession.getCalleeAvatar(),ivCalleeAvatar);
            tvCalleeName.setText(videoCallSession.getCalleeName());
            transactionToCalling(videoCallSession.getCallerId(), videoCallSession.getCalleeId(), true);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer = MediaPlayer.create(getContext(), R.raw.phone_call);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } else {
            rlCalling.setVisibility(View.GONE);
            Log.d("MainActivityCall", videoCallSession.toString());
            transactionToCalling(videoCallSession.getCalleeId(), videoCallSession.getCallerId(), false);
        }

        ivDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCall();
            }
        });
        startHandler();

    }

    public CallingFragment setVideoCallSession(VideoCallSession videoCallSession) {
        this.videoCallSession = videoCallSession;
        return this;
    }

    @Override
    public void onLocalDescription(SessionDescription sessionDescription) {
        RxScheduler.runOnUi(o -> {
            if (rtcClient != null) {
                if (signalingParameters.initiator) {
                    rtcClient.sendOfferSdp(sessionDescription);
                } else {
                    rtcClient.sendAnswerSdp(sessionDescription);
                }
            }
            if (peerConnectionParameters.videoMaxBitrate > 0) {
                Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
            }
        });
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        RxScheduler.runOnUi(o -> {
            Log.d(TAG, "onIceCandidate");
            if (rtcClient != null) {
                rtcClient.sendLocalIceCandidate(iceCandidate);
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        RxScheduler.runOnUi(o -> {
            if (rtcClient != null) {
                rtcClient.sendLocalIceCandidateRemovals(iceCandidates);
            }
        });
    }

    @Override
    public void onIceConnected() {
        Log.d(TAG, "onIceConnected");
        RxScheduler.runOnUi(o -> {
            iceConnected = true;
            callConnected();
        });
    }

    @Override
    public void onIceDisconnected() {
        RxScheduler.runOnUi(o -> {
            iceConnected = false;
            disconnect();
        });
    }

    @Override
    public void onPeerConnectionClosed() {

    }

    @Override
    public void onPeerConnectionStatsReady(StatsReport[] statsReports) {
        RxScheduler.runOnUi(o -> {
            if (iceConnected) {
                Log.e(TAG, "run: " + statsReports);
            }
        });
    }

    @Override
    public void onPeerConnectionError(String s) {
        Log.e(TAG, "onPeerConnectionError: " + s);
    }

    @Override
    public void onSignalConnected(SignalingParameters signalingParameters) {
        RxScheduler.runOnUi(o -> {
            this.signalingParameters = signalingParameters;
            VideoCapturer videoCapturer = null;
            if (peerConnectionParameters.videoCallEnabled) {
                videoCapturer = createVideoCapturer();
            }
            peerConnectionClient
                    .createPeerConnection(getEglBaseContext(), getLocalProxyRenderer(),
                            getRemoteProxyRenderer(), videoCapturer,
                            signalingParameters);

            peerConnectionClient.createOffer();
        });
    }

    @Override
    public void onRemoteDescription(SessionDescription sessionDescription) {
        RxScheduler.runOnUi(o -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                return;
            }
            peerConnectionClient.setRemoteDescription(sessionDescription);
            if (!signalingParameters.initiator) {
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(IceCandidate iceCandidate) {
        Log.d("onRemoteIceCandidate", iceCandidate.serverUrl);
        RxScheduler.runOnUi(o -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.addRemoteIceCandidate(iceCandidate);
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        RxScheduler.runOnUi(o -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.removeRemoteIceCandidates(iceCandidates);
        });
    }

    @Override
    public void onChannelClose() {
        RxScheduler.runOnUi(o -> {
            disconnect();
        });
    }

    @Override
    public void onChannelError(String s) {
        Log.e(TAG, "onChannelError: " + s);
    }

    @Override
    public void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    @Override
    public void disconnect() {
        localProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCallFull != null) {
            vGLSurfaceViewCallFull.release();
            vGLSurfaceViewCallFull = null;
        }
    }

    @Override
    public VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            if (!captureToTexture()) {
                return null;
            }
            videoCapturer = createCameraCapturer(new Camera2Enumerator(getContext()));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            return null;
        }
        return videoCapturer;
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getLocalProxyRenderer() {
        return localProxyRenderer;
    }

    @Override
    public VideoRenderer.Callbacks getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }

    @Override
    public void setSwappedFeeds(boolean swappedFeed) {
        Logging.d(TAG, "setSwappedFeeds: " + swappedFeed);
        this.isSwappedFeeds = swappedFeed;
        localProxyRenderer.setTarget(swappedFeed ? vGLSurfaceViewCallFull : vGLSurfaceViewCallPip);
        remoteProxyRenderer.setTarget(swappedFeed ? vGLSurfaceViewCallPip : vGLSurfaceViewCallFull);
        vGLSurfaceViewCallFull.setMirror(swappedFeed);
        vGLSurfaceViewCallPip.setMirror(!swappedFeed);
    }

    @Override
    public void socketConnect(boolean success) {

    }

    @Override
    public void registerStatus(boolean success) {

    }

    @Override
    public void transactionToCalling(String fromPeer, String toPeer, boolean isHost) {
        initPeerConfig(fromPeer, toPeer, isHost);
        startCall();
    }

    @Override
    public void incomingCalling(String fromPeer) {

    }

    @Override
    public void stopCalling() {
        disconnect();
        if (iceConnected) {
            disconnectKurento();
        }
        ScreenManager.openFragment(getActivity().getSupportFragmentManager(), new DoneVideoCallFragment().setVideoCallSession(videoCallSession, timeCounter), R.id.fl_video_call, false, true);
    }

    @Override
    public void startCallIng() {

    }

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(getContext()) && getDefaultConfig().isUseCamera2();
    }

    private boolean captureToTexture() {
        return getDefaultConfig().isCaptureToTexture();
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    public void startCall() {
//        if (!(Build.VERSION.SDK_INT < 23 || !checkPermission())) {
//            nPermission.requestPermission(getActivity(), Manifest.permission.CAMERA);
//            return;
//        }

        if (rtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }

        SignalingParameters parameters = new SignalingParameters(
                new LinkedList<PeerConnection.IceServer>() {
                    {
                        add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
                        add(new PeerConnection.IceServer("stun:s3.xirsys.com"));
                        add(new PeerConnection.IceServer("turn:s3.xirsys.com:3478?transport=tcp", "e2494e42-8673-11e8-aca8-281d506a467b", "e2494f00-8673-11e8-ad08-61ea1143cff9"));
                        add(new PeerConnection.IceServer("turns:s3.xirsys.com:443?transport=tcp", "e2494e42-8673-11e8-aca8-281d506a467b", "e2494f00-8673-11e8-ad08-61ea1143cff9"));
                        add(new PeerConnection.IceServer("turns:s3.xirsys.com:5349?transport=tcp", "e2494e42-8673-11e8-aca8-281d506a467b", "e2494f00-8673-11e8-ad08-61ea1143cff9"));
                        add(new PeerConnection.IceServer("turn:s3.xirsys.com:80?transport=udp", "e2494e42-8673-11e8-aca8-281d506a467b", "e2494f00-8673-11e8-ad08-61ea1143cff9"));
                        add(new PeerConnection.IceServer("turn:s3.xirsys.com:3478?transport=udp", "e2494e42-8673-11e8-aca8-281d506a467b", "e2494f00-8673-11e8-ad08-61ea1143cff9"));
                        add(new PeerConnection.IceServer("turn:s3.xirsys.com:80?transport=tcp", "e2494e42-8673-11e8-aca8-281d506a467b", "e2494f00-8673-11e8-ad08-61ea1143cff9"));
                    }
                }, true, null, null, null, null, null);
        onSignalConnected(parameters);
        audioManager = RTCAudioManager.create(getActivity().getApplicationContext());
        audioManager.start((audioDevice, availableAudioDevices) ->
                Log.d(TAG, "onAudioManagerDevicesChanged: " + availableAudioDevices + ", "
                        + "selected: " + audioDevice));
    }

    private void callConnected() {
        if (videoCallSession.getType() == TypeCall.CALL) {
            mMediaPlayer.stop();
        }
        rlCalling.setVisibility(View.GONE);
        rlTimer.setVisibility(View.VISIBLE);
        if(videoCallSession != null) tvCalleeName.setText(videoCallSession.getCalleeName());
        countTime();
        timerTask.run();
        client.emit("connectedCall", "stop");
        if (peerConnectionClient == null) {
            return;
        }
        peerConnectionClient.enableStatsEvents(true, 1000);
        setSwappedFeeds(false);
    }

    public void initPeerConfig(String fromPeer, String toPeer, boolean isHost) {
        rtcClient = new KurentoRTCCClient(client, fromPeer, toPeer, isHost);
        defaultConfig = new DefaultConfig();
        peerConnectionParameters = defaultConfig.createPeerConnectionParams();
        peerConnectionClient = PeerConnectionClient.getInstance();
        peerConnectionClient.createPeerConnectionFactory(
                getActivity().getApplicationContext(), peerConnectionParameters, this);
    }

    public void disconnectKurento() {
        if (rtcClient != null) {
            rtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }

        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
    }

    public void connectServer() {
        client.on("incomingCall", incomingCall);
        client.on("callResponse", callResponse);
        client.on("iceCandidate", iceCandidate);
        client.on("startCommunication", startCommunication);
        client.on("stopCommunication", stopCommunication);
    }

    private Emitter.Listener incomingCall = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            RxScheduler.runOnUi(o -> {
                try {
                    JSONObject data = new JSONObject(args[0].toString());
                    incomingCalling(data.getString("from"));
                } catch (Exception e) {

                }
            });
        }
    };
    private Emitter.Listener callResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = new JSONObject(args[0].toString());
                Log.d(TAG, data.toString());
                if (data.getString("response").equalsIgnoreCase("rejected")) {
                    RxScheduler.runOnUi(o -> {
                        logAndToast("Bệnh nhân đã hủy cuộc gọi.");

                    });
                } else {
                    SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                            data.getString("sdpAnswer"));
                    onRemoteDescription(sdp);
                    RxScheduler.runOnUi(o -> {
                        startCallIng();
                    });
                }
            } catch (Exception e) {

            }

        }
    };
    private Emitter.Listener iceCandidate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = new JSONObject(args[0].toString());
                onRemoteIceCandidate(
                        new IceCandidate(data.getJSONObject("candidate").getString("sdpMid"), data.getJSONObject("candidate").getInt("sdpMLineIndex"),
                                data.getJSONObject("candidate").getString("candidate")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener startCommunication = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //JSONObject data = (JSONObject) args[0];
            SessionDescription sdp = null;
            try {
                JSONObject data = new JSONObject(args[0].toString());
                Log.d(TAG, "startCommunication");
                Log.d(TAG, data.toString());
                sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                        data.getString("sdpAnswer"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            onRemoteDescription(sdp);
            RxScheduler.runOnUi(o -> {
                startCallIng();

            });
        }
    };
    private Emitter.Listener stopCommunication = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            RxScheduler.runOnUi(o -> {
                stopCalling();
            });
        }
    };

    private void stopCall() {
        disconnect();
        if (iceConnected) {
            disconnectKurento();
        }
        if (this.timerTask != null) {
            timerTask.cancel();
        }
        client.emit("stop", "stop");
        ScreenManager.openFragment(getActivity().getSupportFragmentManager(), new DoneVideoCallFragment().setVideoCallSession(videoCallSession, timeCounter), R.id.fl_video_call, false, true);

    }

    private void countTime() {
        timer = new Timer();
        Handler handler = new Handler();
        timeCounter = 0;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timeCounter++;
                        int min = timeCounter / 60;
                        int sec = timeCounter % 60;
                        String timeMin = "";
                        String timeSec = "";
                        if (min < 10) {
                            timeMin = "0" + min;
                        } else {
                            timeMin = min + "";
                        }
                        if (sec < 10) {
                            timeSec = "0" + sec;
                        } else {
                            timeSec = sec + "";
                        }
                        String time = timeMin + ":" + timeSec;
                        tvTimer.setText(time);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    public void startHandler() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isNetworkConnected()){
                    logAndToast("Mất kết nối mạng");
                    stopCalling();
                };
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                RECORD_AUDIO);
        int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}
