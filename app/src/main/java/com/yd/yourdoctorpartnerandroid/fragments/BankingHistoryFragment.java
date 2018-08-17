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
import com.yd.yourdoctorpartnerandroid.adapters.BankingHistoryAdapter;
import com.yd.yourdoctorpartnerandroid.adapters.PaymentHistoryAdapter;
import com.yd.yourdoctorpartnerandroid.managers.PaginationScrollListener;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getBankingHistory.GetBankingHistory;
import com.yd.yourdoctorpartnerandroid.networks.getBankingHistory.MainOjectBankingHistory;
import com.yd.yourdoctorpartnerandroid.networks.getBankingHistory.ObjectBanking;
import com.yd.yourdoctorpartnerandroid.networks.getPaymentHistory.GetPaymentHistoryListService;
import com.yd.yourdoctorpartnerandroid.networks.getPaymentHistory.MainHistoryPaymentResponse;
import com.yd.yourdoctorpartnerandroid.networks.getPaymentHistory.ObjectPaymentResponse;
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

public class BankingHistoryFragment extends Fragment {

    private Context context;
    Unbinder butterKnife;

    private BankingHistoryAdapter bankingHistoryAdapter;

    LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 0;


    @BindView(R.id.rvListBanktHistory)
    RecyclerView rvListBanktHistory;
    @BindView(R.id.pbListBankHistory)
    ProgressBar pbListBankHistory;

    Doctor currentDoctor;


    public BankingHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_banking_history, container, false);
        ButterKnife.bind(this, view);
        bankingHistoryAdapter = new BankingHistoryAdapter(getContext());
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        setUpListBankingHistory();
        return view;
    }


    private void setUpListBankingHistory() {

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvListBanktHistory.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvListBanktHistory.addItemDecoration(dividerItemDecoration);
        rvListBanktHistory.setItemAnimator(new DefaultItemAnimator());
        rvListBanktHistory.setAdapter(bankingHistoryAdapter);

        rvListBanktHistory.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
        GetBankingHistory getBankingHistory = RetrofitFactory.getInstance().createService(GetBankingHistory.class);
        getBankingHistory.GetBankingHistory(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), currentDoctor.getDoctorId(), 10 + "", currentPage + "").enqueue(new Callback<MainOjectBankingHistory>() {
            @Override
            public void onResponse(Call<MainOjectBankingHistory> call, Response<MainOjectBankingHistory> response) {
                if (response.code() == 200) {
                    MainOjectBankingHistory mainObject = response.body();
                    List<ObjectBanking> objectBankings = mainObject.getListBankingHistory();
                    if (objectBankings != null && objectBankings.size() > 0) {
                        bankingHistoryAdapter.addAll(objectBankings);

                        if (objectBankings.size() == 10)
                            bankingHistoryAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }

                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }
                if(pbListBankHistory != null){
                    pbListBankHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MainOjectBankingHistory> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                Toast.makeText(getContext(),"Không thể tải được lịch sử thanh toán", Toast.LENGTH_LONG).show();
                if(pbListBankHistory != null){
                    pbListBankHistory.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadNextPage() {
        GetBankingHistory getBankingHistory = RetrofitFactory.getInstance().createService(GetBankingHistory.class);
        getBankingHistory.GetBankingHistory(SharedPrefs.getInstance().get("JWT_TOKEN", String.class), currentDoctor.getDoctorId(), 10 + "", currentPage + "").enqueue(new Callback<MainOjectBankingHistory>() {
            @Override
            public void onResponse(Call<MainOjectBankingHistory> call, Response<MainOjectBankingHistory> response) {
                if (response.code() == 200) {
                    MainOjectBankingHistory mainObject = response.body();
                    // Log.e("haha" , response.body().toString());
                    List<ObjectBanking> objectBankings = mainObject.getListBankingHistory();

                    if (objectBankings != null && objectBankings.size() > 0) {


                        bankingHistoryAdapter.removeLoadingFooter();  // 2
                        isLoading = false;   // 3

                        bankingHistoryAdapter.addAll(objectBankings);   // 4

                        if (objectBankings.size() == 5)
                            bankingHistoryAdapter.addLoadingFooter();  // 5
                        else isLastPage = true;
                    }

                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<MainOjectBankingHistory> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                Toast.makeText(getContext(),"Không thể tải thêm được lịch sử thanh toán", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
