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
import android.widget.TextView;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.DoctorRankingSpecialistAdapter;
import com.yd.yourdoctorpartnerandroid.managers.PaginationScrollListener;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getDoctorRankingSpecialist.DoctorRanking;
import com.yd.yourdoctorpartnerandroid.networks.getDoctorRankingSpecialist.GetDoctorRankingSpecialist;
import com.yd.yourdoctorpartnerandroid.networks.getDoctorRankingSpecialist.MainObjectRanking;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

import java.util.ArrayList;
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
public class ListDoctorRankingSpecialistFragment extends Fragment {

    private String specialistId;
    private Context context;
    Unbinder butterKnife;

    @BindView(R.id.rvListDoctorRanking)
    RecyclerView rvListDoctorRanking;

    @BindView(R.id.pbRanking)
    ProgressBar progressBar;

    @BindView(R.id.tv_error_ranking_list)
    TextView tvErrorRankingList;

    private DoctorRankingSpecialistAdapter doctorRankingAdapter;

    LinearLayoutManager linearLayoutManager;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 0;


    public ListDoctorRankingSpecialistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_doctor_ranking_specialist, container, false);
        butterKnife = ButterKnife.bind(this, view);
        doctorRankingAdapter = new DoctorRankingSpecialistAdapter(getContext());
        tvErrorRankingList.setVisibility(View.GONE);
        setDoctorRankList(specialistId, rvListDoctorRanking);

        return view;
    }

    public ListDoctorRankingSpecialistFragment setSpecialistId(String specialistId, Context context) {
        this.specialistId = specialistId;
        this.context = context;
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        //butterKnife = ButterKnife.bind(this, view);
    }

    private void setDoctorRankList(String specialistId, RecyclerView rv_list_doctor_ranking) {


        // DoctorRankingSpecialistAdapter doctorRankingSpecialistAdaptert = new DoctorRankingSpecialistAdapter(null, context);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_list_doctor_ranking.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rv_list_doctor_ranking.addItemDecoration(dividerItemDecoration);
        rv_list_doctor_ranking.setItemAnimator(new DefaultItemAnimator());
        rv_list_doctor_ranking.setAdapter(doctorRankingAdapter);

        rv_list_doctor_ranking.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1; //Increment page index to load the next one
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
        GetDoctorRankingSpecialist getDoctorRankingSpecialist = RetrofitFactory.getInstance().createService(GetDoctorRankingSpecialist.class);
        getDoctorRankingSpecialist.getDoctorRankingSpecialist(SharedPrefs.getInstance().get("JWT_TOKEN", String.class),specialistId, "5", currentPage + "").enqueue(new Callback<MainObjectRanking>() {
            @Override
            public void onResponse(Call<MainObjectRanking> call, Response<MainObjectRanking> response) {
                if(response.code() == 200){
                    MainObjectRanking mainObject = response.body();
                    Log.e("haha" , response.body().toString());
                    List<DoctorRanking> doctorRankingList = mainObject.getListDoctorRanking();
                    List<Doctor> doctorList = new ArrayList<>();
                    if (doctorRankingList != null && doctorRankingList.size() > 0) {
                        for (DoctorRanking doctorRanking : doctorRankingList) {
                            Doctor doctor = new Doctor();
                            doctor.setAvatar(doctorRanking.getDoctorId().getAvatar());
                            doctor.setFirstName(doctorRanking.getDoctorId().getFirstName());
                            doctor.setLastName(doctorRanking.getDoctorId().getLastName());
                            doctor.setMiddleName(doctorRanking.getDoctorId().getMiddleName());
                            doctor.setCurrentRating((float) doctorRanking.getCurrentRating());
                            doctor.setDoctorId(doctorRanking.getDoctorId().get_id());
                            doctorList.add(doctor);
                        }


                        doctorRankingAdapter.addAll(doctorList);

                        if (doctorRankingList.size()==5) doctorRankingAdapter.addLoadingFooter();
                        else isLastPage = true;
                    }else {
                        if(tvErrorRankingList != null){
                            tvErrorRankingList.setVisibility(View.VISIBLE);
                            tvErrorRankingList.setText("Không có bác sĩ nào thuộc khoa này!");
                        }
                    }


                }else if(response.code() == 401){
                    Utils.backToLogin(getActivity().getApplicationContext());
                }else {
                    if(tvErrorRankingList != null){
                        tvErrorRankingList.setVisibility(View.VISIBLE);
                        tvErrorRankingList.setText("Không tải được dữ liệu!!");
                    }

                }

                if(progressBar != null) progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<MainObjectRanking> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadNextPage() {

        GetDoctorRankingSpecialist getDoctorRankingSpecialist = RetrofitFactory.getInstance().createService(GetDoctorRankingSpecialist.class);
        getDoctorRankingSpecialist.getDoctorRankingSpecialist(SharedPrefs.getInstance().get("JWT_TOKEN", String.class),specialistId, "5", currentPage + "").enqueue(new Callback<MainObjectRanking>() {
            @Override
            public void onResponse(Call<MainObjectRanking> call, Response<MainObjectRanking> response) {
                if(response.code() == 200){
                    MainObjectRanking mainObject = response.body();
                    List<DoctorRanking> doctorRankingList = mainObject.getListDoctorRanking();
                    List<Doctor> doctorList = new ArrayList<>();
                    for (DoctorRanking doctorRanking : doctorRankingList) {
                        Doctor doctor = new Doctor();
                        doctor.setAvatar(doctorRanking.getDoctorId().getAvatar());
                        doctor.setFirstName(doctorRanking.getDoctorId().getFirstName());
                        doctor.setLastName(doctorRanking.getDoctorId().getLastName());
                        doctor.setMiddleName(doctorRanking.getDoctorId().getMiddleName());
                        doctor.setCurrentRating((float) doctorRanking.getCurrentRating());
                        doctor.setDoctorId(doctorRanking.getDoctorId().get_id());
                        doctorList.add(doctor);
                    }
                    doctorRankingAdapter.removeLoadingFooter();  // 2
                    isLoading = false;   // 3

                    doctorRankingAdapter.addAll(doctorList);   // 4

                    if (doctorList.size()==5) doctorRankingAdapter.addLoadingFooter();  // 5
                    else isLastPage = true;
                }else if(response.code() == 401){
                    Utils.backToLogin(getActivity().getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<MainObjectRanking> call, Throwable t) {
                Log.d("Anhle", "Fail: " + t.getMessage());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterKnife.unbind();
    }
}
