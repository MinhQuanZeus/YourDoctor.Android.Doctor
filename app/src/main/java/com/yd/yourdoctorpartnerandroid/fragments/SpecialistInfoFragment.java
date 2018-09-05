package com.yd.yourdoctorpartnerandroid.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.DoctorCertificationAdapter;
import com.yd.yourdoctorpartnerandroid.adapters.SpecialistAdapter;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Certification;
import com.yd.yourdoctorpartnerandroid.models.Specialist;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getSpecialistService.GetSpecialistService;
import com.yd.yourdoctorpartnerandroid.networks.getSpecialistService.MainObjectSpecialist;
import com.yd.yourdoctorpartnerandroid.utils.LoadDefaultModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecialistInfoFragment extends Fragment {

    @BindView(R.id.rvListSpecialist)
    RecyclerView rvListSpecialist;

    @BindView(R.id.pbSpecialist)
    ProgressBar pbSpecialist;

    List<Specialist> specialists;

    SpecialistAdapter specialistAdapter;


    public SpecialistInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_specialist_info, container, false);
        ButterKnife.bind(this, view);
        setupUI();
        return view;
    }

    private void setupUI() {
        //TODO
        specialists = LoadDefaultModel.getInstance().getSpecialists();
        rvListSpecialist.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvListSpecialist.setFocusable(false);
        if (specialists == null) {
            GetSpecialistService getSpecialistService = RetrofitFactory.getInstance().createService(GetSpecialistService.class);
            getSpecialistService.getMainObjectSpecialist().enqueue(new Callback<MainObjectSpecialist>() {
                @Override
                public void onResponse(Call<MainObjectSpecialist> call, Response<MainObjectSpecialist> response) {
                    if (response.code() == 200) {
                        MainObjectSpecialist mainObjectSpecialist = response.body();
                        specialists = mainObjectSpecialist.getListSpecialist();
                        LoadDefaultModel.getInstance().setSpecialists(specialists);
                        specialistAdapter = new SpecialistAdapter(specialists, getContext());
                        rvListSpecialist.setAdapter(specialistAdapter);
                    }
                    if(pbSpecialist != null){
                        pbSpecialist.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<MainObjectSpecialist> call, Throwable t) {
                    Toast.makeText(getContext(), "Kết nốt mạng có vấn đề , không thể tải dữ liệu", Toast.LENGTH_LONG).show();
                    if(pbSpecialist != null){
                        pbSpecialist.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            specialistAdapter = new SpecialistAdapter(specialists, getContext());
            rvListSpecialist.setAdapter(specialistAdapter);
            if(pbSpecialist != null){
                pbSpecialist.setVisibility(View.GONE);
            }
        }


    }

}
