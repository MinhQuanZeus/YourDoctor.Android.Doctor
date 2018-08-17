package com.yd.yourdoctorpartnerandroid.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.VideoCallHistoryAdapter;
import com.yd.yourdoctorpartnerandroid.managers.PaginationScrollListener;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getHistoryVideoCall.GetHistoryVideoCall;
import com.yd.yourdoctorpartnerandroid.networks.getHistoryVideoCall.MainObjectHistoryVideo;
import com.yd.yourdoctorpartnerandroid.networks.getHistoryVideoCall.ObjectHistoryVideo;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoCallHistoryFragment extends Fragment {

    private Context context;
    Unbinder butterKnife;

    private VideoCallHistoryAdapter videoCallHistoryAdapter;

    LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 0;

    @BindView(R.id.rvListVideoCallHistory)
    RecyclerView rvListVideoCallHistory;
    @BindView(R.id.pbVideoCallHistory)
    ProgressBar pbVideoCallHistory;

    Doctor currentDoctor;


    public VideoCallHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_call_history, container, false);
        ButterKnife.bind(this, view);
        videoCallHistoryAdapter = new VideoCallHistoryAdapter(getContext());
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        setUpListVideoHistory();
        return view;
    }


    private void setUpListVideoHistory() {

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvListVideoCallHistory.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListVideoCallHistory.addItemDecoration(dividerItemDecoration);
        rvListVideoCallHistory.setItemAnimator(new DefaultItemAnimator());
        rvListVideoCallHistory.setAdapter(videoCallHistoryAdapter);

        rvListVideoCallHistory.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1; //Increment page index to load the next one
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //pbNotificaton.setVisibility(View.VISIBLE);
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstPage();
            }
        }, 1000);

        //doctorRankingAdapter.notifyDataSetChanged();
    }

    private void loadFirstPage() {
        GetHistoryVideoCall getPaymentHistoryListService = RetrofitFactory.getInstance().createService(GetHistoryVideoCall.class);
        getPaymentHistoryListService.getHistoryVideoCall(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), currentDoctor.getDoctorId(), 10 + "", currentPage + "").enqueue(new Callback<MainObjectHistoryVideo>() {
            @Override
            public void onResponse(Call<MainObjectHistoryVideo> call, Response<MainObjectHistoryVideo> response) {
                if (response.code() == 200) {
                    MainObjectHistoryVideo mainObject = response.body();
                    List<ObjectHistoryVideo> objectHistoryVideos = mainObject.getListVideoCallHistory();
                    if (objectHistoryVideos != null && objectHistoryVideos.size() > 0) {
                        videoCallHistoryAdapter.addAll(objectHistoryVideos);

                        if (objectHistoryVideos.size() == 10)
                            videoCallHistoryAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }

                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }

                if(pbVideoCallHistory != null){
                    pbVideoCallHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MainObjectHistoryVideo> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể tải được lịch sử thanh toán", Toast.LENGTH_LONG).show();
                if(pbVideoCallHistory != null){
                    pbVideoCallHistory.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadNextPage() {
        GetHistoryVideoCall getHistoryVideoCall = RetrofitFactory.getInstance().createService(GetHistoryVideoCall.class);
        getHistoryVideoCall.getHistoryVideoCall(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), currentDoctor.getDoctorId(), 10 + "", currentPage + "").enqueue(new Callback<MainObjectHistoryVideo>() {
            @Override
            public void onResponse(Call<MainObjectHistoryVideo> call, Response<MainObjectHistoryVideo> response) {
                if (response.code() == 200) {
                    MainObjectHistoryVideo mainObject = response.body();
                    List<ObjectHistoryVideo> objectHistoryVideos = mainObject.getListVideoCallHistory();

                    if (objectHistoryVideos != null && objectHistoryVideos.size() > 0) {


                        videoCallHistoryAdapter.removeLoadingFooter();  // 2
                        isLoading = false;   // 3

                        videoCallHistoryAdapter.addAll(objectHistoryVideos);   // 4

                        if (objectHistoryVideos.size() == 5)
                            videoCallHistoryAdapter.addLoadingFooter();  // 5
                        else isLastPage = true;
                    }

                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<MainObjectHistoryVideo> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể tải thêm được lịch sử thanh toán", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}

