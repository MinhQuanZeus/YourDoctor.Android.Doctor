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

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.ChatHistoryAdapter;
import com.yd.yourdoctorpartnerandroid.adapters.NotificationAdapter;
import com.yd.yourdoctorpartnerandroid.managers.PaginationScrollListener;
import com.yd.yourdoctorpartnerandroid.models.HistoryChat;
import com.yd.yourdoctorpartnerandroid.models.Notification;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getListHistoryChat.GetListHistoryChat;
import com.yd.yourdoctorpartnerandroid.networks.getListHistoryChat.MainObjectHistoryChat;
import com.yd.yourdoctorpartnerandroid.networks.getListNotification.GetListNotificationService;
import com.yd.yourdoctorpartnerandroid.networks.getListNotification.MainObjectNotification;
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
public class ListChatHistoryFragment extends Fragment {

    private Context context;
    Unbinder butterKnife;

    private ChatHistoryAdapter chatHistoryAdapter;

    LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 0;


    @BindView(R.id.rvListChatHistory)
    RecyclerView rvListChatHistory;
    @BindView(R.id.pbListChatHistory)
    ProgressBar pbListChatHistory;

    Patient currentPatient;


    public ListChatHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_chat_history, container, false);
        ButterKnife.bind(this, view);
        chatHistoryAdapter = new ChatHistoryAdapter(getContext());
        currentPatient = SharedPrefs.getInstance().get("USER_INFO", Patient.class);
        setUpListChatHistory();
        return view;
    }


    private void setUpListChatHistory() {

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvListChatHistory.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListChatHistory.addItemDecoration(dividerItemDecoration);
        rvListChatHistory.setItemAnimator(new DefaultItemAnimator());
        rvListChatHistory.setAdapter(chatHistoryAdapter);

        rvListChatHistory.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
        GetListHistoryChat getListHistoryChat = RetrofitFactory.getInstance().createService(GetListHistoryChat.class);
        getListHistoryChat.getListHistoryChat(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), currentPatient.getId(), 10 + "", currentPage + "").enqueue(new Callback<MainObjectHistoryChat>() {
            @Override
            public void onResponse(Call<MainObjectHistoryChat> call, Response<MainObjectHistoryChat> response) {
                if (response.code() == 200) {
                    MainObjectHistoryChat mainObject = response.body();
                    Log.e("haha", response.body().toString());
                    List<HistoryChat> historyChats = mainObject.getListChatsHistory();
                    if (historyChats != null && historyChats.size() > 0) {
                        chatHistoryAdapter.addAll(historyChats);

                        if (historyChats.size() == 10)
                            chatHistoryAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }
                    if(pbListChatHistory != null){
                        pbListChatHistory.setVisibility(View.GONE);
                    }
                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<MainObjectHistoryChat> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                if(pbListChatHistory != null){
                    pbListChatHistory.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadNextPage() {
        GetListHistoryChat getListHistoryChat = RetrofitFactory.getInstance().createService(GetListHistoryChat.class);
        getListHistoryChat.getListHistoryChat(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), currentPatient.getId(), 10 + "", currentPage + "").enqueue(new Callback<MainObjectHistoryChat>() {
            @Override
            public void onResponse(Call<MainObjectHistoryChat> call, Response<MainObjectHistoryChat> response) {
                if (response.code() == 200) {
                    MainObjectHistoryChat mainObject = response.body();
                    // Log.e("haha" , response.body().toString());
                    List<HistoryChat> historyChats = mainObject.getListChatsHistory();

                    if (historyChats != null && historyChats.size() > 0) {


                        chatHistoryAdapter.removeLoadingFooter();  // 2
                        isLoading = false;   // 3

                        chatHistoryAdapter.addAll(historyChats);   // 4

                        if (historyChats.size() == 5)
                            chatHistoryAdapter.addLoadingFooter();  // 5
                        else isLastPage = true;
                    }
                    if(pbListChatHistory != null){
                        pbListChatHistory.setVisibility(View.GONE);
                    }
                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<MainObjectHistoryChat> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                if(pbListChatHistory != null){
                    pbListChatHistory.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
