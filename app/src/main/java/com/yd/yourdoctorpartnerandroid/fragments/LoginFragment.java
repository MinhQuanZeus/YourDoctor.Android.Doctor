package com.yd.yourdoctorpartnerandroid.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.activities.MainActivity;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Certification;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getDetailDoctor.GetDetailDoctorService;
import com.yd.yourdoctorpartnerandroid.networks.getDetailDoctor.MainDetailDoctor;
import com.yd.yourdoctorpartnerandroid.networks.models.AuthResponse;
import com.yd.yourdoctorpartnerandroid.networks.models.CommonErrorResponse;
import com.yd.yourdoctorpartnerandroid.networks.models.Login;
import com.yd.yourdoctorpartnerandroid.models.SpecialistDoctor;
import com.yd.yourdoctorpartnerandroid.networks.services.LoginService;
import com.yd.yourdoctorpartnerandroid.utils.LoadDefaultModel;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String USER_INFO = "USER_INFO";
    @BindView(R.id.ed_phone)
    EditText edPhone;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.til_phone)
    TextInputLayout tilPhone;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.btn_sign_in)
    CircularProgressButton btnLogin;
    @BindView(R.id.checkBoxRemember)
    CheckBox checkBoxRemember;
    @BindView(R.id.forgotpass)
    com.yd.yourdoctorpartnerandroid.custormviews.MyTextView forgotPass;
    @BindView(R.id.tv_signup)
    TextView tvSignUp;
    private Unbinder unbinder;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        setUp(view);
        return view;
    }

    private void setUp(View view) {
        unbinder = ButterKnife.bind(this, view);
        String phone = SharedPrefs.getInstance().get("phone",String.class);
        String password = SharedPrefs.getInstance().get("password",String.class);

        if(phone != null && phone!= "" && password != null && password != null){
            checkBoxRemember.setChecked(true);
            edPhone.setText(phone);
            edPassword.setText(password);
        }

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenManager.openFragment(getActivity().getSupportFragmentManager(), new RulesRegisterFragment(), R.id.fl_auth, true, true);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputPhoneNumberFragment inputPhoneNumberFragment = new InputPhoneNumberFragment();
                inputPhoneNumberFragment.setIsForgetPassword(true);
                ScreenManager.openFragment(getActivity().getSupportFragmentManager(), inputPhoneNumberFragment, R.id.fl_auth, true, true);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
    }

    private boolean onValidate() {
        boolean isValidate = true;
        String regex = "(09|01[2|6|8|9])+([0-9]{8})\\b";
        String phone = edPhone.getText().toString();
        String password = edPassword.getText().toString();

        if (phone.isEmpty() || !phone.matches(regex)) {
            isValidate = false;
            tilPhone.setError(getResources().getString(R.string.ERROR0001));
        } else {
            isValidate = true;
            tilPhone.setError(null);
        }

        if(password.isEmpty()){
            isValidate = false;
            tilPassword.setError(getResources().getString(R.string.empty_password));
        }else {
            isValidate = true;
            tilPassword.setError(null);
        }

        return isValidate;
    }

    private void onLogin() {
        if (!onValidate()){
            return;
        }
        btnLogin.startAnimation();
        disableAll();
        String phone = edPhone.getText().toString();
        String password = edPassword.getText().toString();
        Login login = new Login(phone, password);

        LoginService loginService = RetrofitFactory.getInstance().createService(LoginService.class);
        loginService.register(login).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (response.code() == 200 || response.code() == 201) {
                    if(response.body().getDoctor().getStatus() != 1){
                        Toast.makeText(getContext() , "Tài khoản đang bị khóa!", Toast.LENGTH_LONG).show();
                        btnLogin.revertAnimation();
                    }else if(response.body().getDoctor().getRole() != 2){
                        Toast.makeText(getContext() , "Tài khoản này không phải tài khoản bác sĩ!", Toast.LENGTH_LONG).show();
                        btnLogin.revertAnimation();
                    }
                    else{
                        SharedPrefs.getInstance().put(JWT_TOKEN, response.body().getJwtToken());
                        Log.e("tokenLogin: ",SharedPrefs.getInstance().get(JWT_TOKEN,String.class));
                        if(SharedPrefs.getInstance().get(USER_INFO, Doctor.class) != null){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(SharedPrefs.getInstance().get(USER_INFO, Doctor.class).getDoctorId());
                        }
                        SharedPrefs.getInstance().put(USER_INFO, response.body().getDoctor());
                        FirebaseMessaging.getInstance().subscribeToTopic(response.body().getDoctor().getDoctorId());
                        LoadDefaultModel.getInstance().registerServiceCheckNetwork(getActivity().getApplicationContext());

                        if(checkBoxRemember.isChecked()){
                            SharedPrefs.getInstance().put("phone",edPhone.getText().toString());
                            SharedPrefs.getInstance().put("password",edPassword.getText().toString());
                        }else {
                            SharedPrefs.getInstance().put("phone","");
                            SharedPrefs.getInstance().put("password","");
                        }
                        getDetailDoctor();

                    }

                }else {
                    enableAll();
                    try{
                        CommonErrorResponse commonErrorResponse = parseToCommonError(response);
                        if (commonErrorResponse.getError() != null) {
                            String error = Utils.getStringResourceByString(getContext(), commonErrorResponse.getError());
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            Log.d("RESPONSE", error);
                        }
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Có lỗi không thể đăng nhập", Toast.LENGTH_SHORT).show();
                    }

                    btnLogin.revertAnimation();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if(btnLogin != null) btnLogin.revertAnimation();
                enableAll();
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getActivity(), getResources().getText(R.string.error_timeout), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(),"Không có kết nối mạng", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getDetailDoctor(){
        GetDetailDoctorService getDetailDoctorService = RetrofitFactory.getInstance().createService(GetDetailDoctorService.class);
        getDetailDoctorService.getDetailDoctor(SharedPrefs.getInstance().get(JWT_TOKEN,String.class),SharedPrefs.getInstance().get(USER_INFO,Doctor.class).getDoctorId()).enqueue(new Callback<MainDetailDoctor>() {
            @Override
            public void onResponse(Call<MainDetailDoctor> call, Response<MainDetailDoctor> response) {

                if (response.code() == 200) {
                    Doctor doctor;
                    doctor = SharedPrefs.getInstance().get(USER_INFO,Doctor.class);
                    doctor.setCertificates((ArrayList<Certification>) response.body().getDetailDoctor().getCertificates());
                    doctor.setIdSpecialist((ArrayList<SpecialistDoctor>)response.body().getDetailDoctor().getIdSpecialist());
                    doctor.setUniversityGraduate(response.body().getDetailDoctor().getUniversityGraduate());
                    doctor.setYearGraduate(response.body().getDetailDoctor().getYearGraduate());
                    doctor.setPlaceWorking(response.body().getDetailDoctor().getPlaceWorking());
                    doctor.setCurrentRating(response.body().getDetailDoctor().getCurrentRating());
                    SharedPrefs.getInstance().put(USER_INFO,doctor);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }else {
                    Toast.makeText(getContext(),"Đăng nhập không thành công",Toast.LENGTH_LONG).show();
                }
                btnLogin.revertAnimation();
            }

            @Override
            public void onFailure(Call<MainDetailDoctor> call, Throwable t) {
                Toast.makeText(getContext(),"Đăng nhập không thành công",Toast.LENGTH_LONG).show();
                btnLogin.revertAnimation();
            }
        });
    }

    private void enableAll() {
        this.edPassword.setEnabled(true);
        this.edPhone.setEnabled(true);
    }

    private void disableAll() {
        this.edPassword.setEnabled(false);
        this.edPhone.setEnabled(false);
    }

    public CommonErrorResponse parseToCommonError(Response<AuthResponse> response) {
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse();
        Gson gson = new GsonBuilder().create();
        try {
            commonErrorResponse = gson.fromJson(response.errorBody().string(), CommonErrorResponse.class);
        } catch (IOException e) {
            // handle failure to read error
        }
        return commonErrorResponse;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
