package com.yd.yourdoctorpartnerandroid.fragments;


import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.BuildConfig;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.DoctorCertificationAdapter;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Specialist;
import com.yd.yourdoctorpartnerandroid.models.SpecialistDoctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.changePassword.ChangePasswordService;
import com.yd.yourdoctorpartnerandroid.networks.changePassword.PasswordRequest;
import com.yd.yourdoctorpartnerandroid.networks.changePassword.PasswordResponse;
import com.yd.yourdoctorpartnerandroid.networks.changeProfileDoctor.DoctorRequest;
import com.yd.yourdoctorpartnerandroid.networks.changeProfileDoctor.DoctorRespone;
import com.yd.yourdoctorpartnerandroid.networks.changeProfileDoctor.changeProfileDoctorService;
import com.yd.yourdoctorpartnerandroid.networks.getLinkImageService.GetLinkImageService;
import com.yd.yourdoctorpartnerandroid.networks.getLinkImageService.MainGetLink;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.yd.yourdoctorpartnerandroid.fragments.RegisterFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorProfileFragment extends Fragment implements  View.OnClickListener{
    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String USER_INFO = "USER_INFO";
    public static final int TYPE_EDIT = 1;
    public static final int TYPE_CANCEL = 2;
    public static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})";
    private boolean isChangeInfo;
    //handle image
    private String mImagePathToBeAttached;
    private Bitmap mImageToBeAttached;
    private String filename;


    @BindView(R.id.tv_fname)
    TextView tv_fname;
    @BindView(R.id.tv_mname)
    TextView tv_mname;
    @BindView(R.id.tv_lname)
    TextView tv_lname;

    @BindView(R.id.rb_doctorRanking)
    RatingBar rb_doctorRanking;
    @BindView(R.id.tv_remainMoney)
    TextView tv_remainMoney;
    @BindView(R.id.tv_phone)
    EditText tv_phone;
    @BindView(R.id.tv_universityGraduate)
    EditText tv_universityGraduate;
    @BindView(R.id.tv_yearGraduate)
    EditText tv_yearGraduate;

    @BindView(R.id.radio_male)
    RadioButton rbMale;
    @BindView(R.id.radio_other)
    RadioButton rbOther;
    @BindView(R.id.radio_female)
    RadioButton rbFmale;
    @BindView(R.id.rg_gender)
    RadioGroup rg_gender;

    @BindView(R.id.ed_birthday)
    EditText ed_birthday;
    @BindView(R.id.ed_address)
    EditText ed_address;
    @BindView(R.id.ed_placeWorking)
    EditText ed_placeWorking;


    @BindView(R.id.tv_certificates)
    EditText tv_certificates;
    @BindView(R.id.tv_specialist)
    EditText tv_specialist;
    @BindView(R.id.rv_image_certificates)
    RecyclerView rv_image_certificates;

    @BindView(R.id.pbProfileDoctor)
    ProgressBar pbProfileDoctor;




    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.iv_upload_avatar)
    CircleImageView ivUploadAvatar;

    @BindView(R.id.tb_main)
    Toolbar tbMain;

    @BindView(R.id.rl_mainButton)
    RelativeLayout rlMainButton;
    @BindView(R.id.rl_YesNoButton)
    RelativeLayout rlYesNoButton;

    @BindView(R.id.btn_no)
    Button btnNo;
    @BindView(R.id.btn_yes)
    Button btnYes;

    @BindView(R.id.btn_change_password)
    Button btnChangePassword;
    @BindView(R.id.btn_edit_profile)
    Button btnEditProfile;

    @BindView(R.id.til_place_working)
    TextInputLayout til_placeWorking;

    @BindView(R.id.til_address)
    TextInputLayout til_address;

    private Doctor currentDoctor;

    private Unbinder unbinder;

    //handle password
    EditText et_old_password;
    EditText et_new_password;
    EditText et_confirm_new_password;
    TextView tv_message_change_password;
    ProgressBar progress_change_password;
    AlertDialog dialogChangePassword;


    public DoctorProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        setupUI(view);

        return view;
    }

    private void setupUI(View view) {

        unbinder = ButterKnife.bind(this, view);
        currentDoctor = SharedPrefs.getInstance().get(USER_INFO, Doctor.class);
        isChangeInfo = false;

        tbMain.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tbMain.setTitle(R.string.profile_page);
        tbMain.setTitleTextColor(getResources().getColor(R.color.primary_text));
        tbMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenManager.backFragment(getFragmentManager());
            }
        });
        // can not edit phone, and remain money
        tv_phone.setEnabled(false);
        tv_remainMoney.setEnabled(false);

        //set on click
        btnChangePassword.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        ivUploadAvatar.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        btnYes.setOnClickListener(this);

        DoctorCertificationAdapter doctorCertificationAdapter = new DoctorCertificationAdapter(currentDoctor.getCertificates(),getContext());
        rv_image_certificates.setAdapter(doctorCertificationAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false);
        rv_image_certificates.setLayoutManager(gridLayoutManager);
        tv_fname.setText(currentDoctor.getFirstName());
        tv_mname.setText(currentDoctor.getMiddleName());
        tv_lname.setText(currentDoctor.getLastName());
        tv_phone.setText(currentDoctor.getPhoneNumber());
        tv_remainMoney.setText(Utils.formatStringNumber(currentDoctor.getRemainMoney()) + " đ");
        tv_universityGraduate.setText(currentDoctor.getUniversityGraduate());
        tv_universityGraduate.setEnabled(false);
        tv_yearGraduate.setText(currentDoctor.getYearGraduate());
        tv_yearGraduate.setEnabled(false);
        rb_doctorRanking.setRating(currentDoctor.getCurrentRating());
        String specialistTotal = "";

        for(SpecialistDoctor specialist: currentDoctor.getIdSpecialist()){
            specialistTotal = specialistTotal + specialist.getName() + ", ";
        }
        tv_specialist.setText(specialistTotal);
        tv_specialist.setEnabled(false);
        tv_certificates.setEnabled(false);

        setUpCalendar();
        setScreenFunction(TYPE_CANCEL);
    }

    private void setScreenFunction(int type) {
        if (type == TYPE_EDIT) {
            enableAll();
            rlMainButton.setVisibility(View.GONE);
            rlYesNoButton.setVisibility(View.VISIBLE);
        } else if (type == TYPE_CANCEL) {
            isChangeInfo = false;
            ZoomImageViewUtils.loadCircleImage(getContext(), currentDoctor.getAvatar().toString(), ivAvatar);
            ed_address.setText(currentDoctor.getAddress());
            ed_birthday.setText(currentDoctor.getBirthday());
            ed_placeWorking.setText(currentDoctor.getPlaceWorking());

            switch (currentDoctor.getGender()) {
                case 1: {
                    rbMale.setChecked(true);
                    rbFmale.setChecked(false);
                    rbOther.setChecked(false);
                    break;
                }
                case 2: {
                    rbMale.setChecked(false);
                    rbFmale.setChecked(true);
                    rbOther.setChecked(false);
                    break;
                }
                case 3: {
                    rbMale.setChecked(false);
                    rbFmale.setChecked(false);
                    rbOther.setChecked(true);
                    break;
                }
            }
            disableAll();
            rlMainButton.setVisibility(View.VISIBLE);
            rlYesNoButton.setVisibility(View.GONE);
        }
        mImageToBeAttached = null;
        pbProfileDoctor.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_password: {
                showDialogChangePassword();
                break;
            }
            case R.id.iv_upload_avatar: {
                //imageUtils.displayAttachImageDialog();
                displayAttachImageDialog();
                break;
            }
            case R.id.btn_edit_profile: {
                setScreenFunction(TYPE_EDIT);
                break;
            }
            case R.id.btn_yes: {
                onSubmit();
                break;
            }
            case R.id.btn_no: {
                setScreenFunction(TYPE_CANCEL);
                break;
            }
        }
    }

    private void showDialogChangePassword() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        et_old_password = view.findViewById(R.id.et_old_password);
        et_new_password = view.findViewById(R.id.et_new_password);
        et_confirm_new_password = view.findViewById(R.id.et_confirm_new_password);
        et_old_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        et_new_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        et_confirm_new_password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        tv_message_change_password = view.findViewById(R.id.tv_message_change_password);
        progress_change_password = view.findViewById(R.id.progress_change_password);
        builder.setView(view);
        builder.setTitle("Thay đổi mật khẩu");

        builder.setPositiveButton("Thay đổi", new DialogInterface.OnClickListener() {
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
        dialogChangePassword = builder.create();
        dialogChangePassword.show();
        dialogChangePassword.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onValidatePassword(et_old_password.getText().toString(),et_new_password.getText().toString(), et_confirm_new_password.getText().toString(), tv_message_change_password)) {
                    pbProfileDoctor.setVisibility(View.VISIBLE);
                    PasswordRequest passwordRequest = new PasswordRequest();
                    passwordRequest.setId(currentDoctor.getDoctorId());
                    passwordRequest.setOldPassword(et_old_password.getText().toString());
                    passwordRequest.setNewPassword(et_new_password.getText().toString());

                    ChangePasswordService changePasswordService = RetrofitFactory.getInstance().createService(ChangePasswordService.class);
                    changePasswordService.changePasswordService(SharedPrefs.getInstance().get(JWT_TOKEN, String.class), passwordRequest).enqueue(new Callback<PasswordResponse>() {
                        @Override
                        public void onResponse(Call<PasswordResponse> call, Response<PasswordResponse> response) {
                            tv_message_change_password.setVisibility(View.VISIBLE);
                            PasswordResponse passwordResponse = response.body();
                            if (response.code() == 200 && passwordResponse.isChangePasswordSuccess()) {
                                tv_message_change_password.setText("Thay đổi mật khẩu thành công");
                                tv_message_change_password.setTextColor(getResources().getColor(R.color.colorPrimary));
                                et_old_password.setText("");
                                et_new_password.setText("");
                                et_confirm_new_password.setText("");
                                Toast.makeText(getContext(),"Thay đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                                dialogChangePassword.dismiss();
                            } else if(response.code() == 401) {
                                Utils.backToLogin(getActivity().getApplicationContext());

                            }else {
                                tv_message_change_password.setTextColor(getResources().getColor(R.color.red));
                                tv_message_change_password.setText("Mật khẩu cũ không đúng!");
                            }
                            pbProfileDoctor.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<PasswordResponse> call, Throwable t) {
                            tv_message_change_password.setVisibility(View.VISIBLE);
                            tv_message_change_password.setText("Không kết nối được với máy chủ!");
                            pbProfileDoctor.setVisibility(View.GONE);

                        }
                    });
                }

            }
        });
    }


    private void setUpCalendar() {
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthDay(myCalendar);
            }

        };

        ed_birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateBirthDay(Calendar myCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR , -100 );

        if (myCalendar.getTimeInMillis() >= (Calendar.getInstance().getTimeInMillis())) {
            Toast.makeText(getContext(), "Bạn không thể chọn ngày sinh của bạn sau thời gian hiện tại", Toast.LENGTH_LONG).show();
        }else if(myCalendar.getTimeInMillis() <= calendar.getTimeInMillis()){
            Toast.makeText(getContext(), "Năm sinh của bạn không hợp lệ", Toast.LENGTH_LONG).show();
        }else {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            ed_birthday.setText(sdf.format(myCalendar.getTime()));
        }
    }

    private Doctor newDoctor;
    private void onSubmit() {

        String birthday = ed_birthday.getText().toString();
        String address = ed_address.getText().toString();
        String placeWorking =  ed_placeWorking.getText().toString();
        int gender = getGender();

        if (currentDoctor.getGender() != gender) isChangeInfo = true;
        if (!currentDoctor.getBirthday().equals(birthday)) isChangeInfo = true;
        if (!currentDoctor.getAddress().equals(address)) isChangeInfo = true;
        if (!currentDoctor.getPlaceWorking().equals(placeWorking)) isChangeInfo = true;

        if (!isChangeInfo) {
            Toast.makeText(getContext(), "Bạn không thay đổi bất kỳ thông tin nào!", Toast.LENGTH_LONG).show();
            return;
        } else if (onValidate()) {

            newDoctor = currentDoctor;
            newDoctor.setAddress(address);
            newDoctor.setBirthday(birthday);
            newDoctor.setPlaceWorking(placeWorking);
            newDoctor.setGender(gender);
            onUpdateUser();
        }

    }

    private void onUpdateUser() {
        if(pbProfileDoctor != null) pbProfileDoctor.setVisibility(View.VISIBLE);
        if (mImageToBeAttached != null) {
            GetLinkImageService getLinkeImageService = RetrofitFactory.getInstance().createService(GetLinkImageService.class);
            getLinkeImageService.uploadImageToGetLink(getImageUpload()).enqueue(new Callback<MainGetLink>() {
                @Override
                public void onResponse(Call<MainGetLink> call, Response<MainGetLink> response) {

                    if (response.code() == 200) {
                        updateDoctorNetWork( response.body().getFilePath());
                    } else {
                        Toast.makeText(getContext(), "Không thể kết nối máy chủ", Toast.LENGTH_LONG).show();
                        if(pbProfileDoctor != null) pbProfileDoctor.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<MainGetLink> call, Throwable t) {
                    Toast.makeText(getContext(), "Không thể kết nối máy chủ", Toast.LENGTH_LONG).show();
                    if(pbProfileDoctor != null) pbProfileDoctor.setVisibility(View.GONE);
                }
            });
        } else {
            updateDoctorNetWork(null);
        }

    }

    private void updateDoctorNetWork(String linkImage) {
        DoctorRequest doctorRequest;
        if (linkImage == null) {
            doctorRequest = new DoctorRequest(newDoctor.getDoctorId(),newDoctor.getBirthday(),
                    newDoctor.getAddress(),newDoctor.getPlaceWorking(),
                    newDoctor.getAvatar(),newDoctor.getGender());
        } else {
            doctorRequest = new DoctorRequest(newDoctor.getDoctorId(),newDoctor.getBirthday(),
                    newDoctor.getAddress(),newDoctor.getPlaceWorking(),linkImage,newDoctor.getGender());
        }
        changeProfileDoctorService changeProfileDoctorService = RetrofitFactory.getInstance().createService(changeProfileDoctorService.class);
        changeProfileDoctorService.changeProfileDoctorService(SharedPrefs.getInstance().get(JWT_TOKEN, String.class), doctorRequest).enqueue(new Callback<DoctorRespone>() {
            @Override
            public void onResponse(Call<DoctorRespone> call, Response<DoctorRespone> response) {
                if (response.code() == 200) {
                    DoctorRespone doctorRespone = response.body();
                    if (doctorRespone != null) {
                        currentDoctor.setGender(doctorRespone.getInformationDoctor().getGender());
                        currentDoctor.setAddress(doctorRespone.getInformationDoctor().getAddress());
                        currentDoctor.setAvatar(doctorRespone.getInformationDoctor().getAvatar());
                        currentDoctor.setBirthday(doctorRespone.getInformationDoctor().getBirthday());
                        currentDoctor.setPlaceWorking(doctorRespone.getInformationDoctor().getPlaceWorking());
                        SharedPrefs.getInstance().put(USER_INFO, currentDoctor);
                        Toast.makeText(getContext(), "Chỉnh sửa thành công", Toast.LENGTH_LONG).show();
                        setScreenFunction(TYPE_CANCEL);
                    }
                } else {
                    Toast.makeText(getContext(), "Kết nối máy chủ không thành công", Toast.LENGTH_LONG).show();
                }
                if(pbProfileDoctor != null) pbProfileDoctor.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DoctorRespone> call, Throwable t) {
                Toast.makeText(getContext(), "Kết nối máy chủ không thành công", Toast.LENGTH_LONG).show();
                if(pbProfileDoctor != null) pbProfileDoctor.setVisibility(View.GONE);
            }
        });
    }

    private void enableAll() {
        ed_address.setEnabled(true);
        ed_birthday.setEnabled(true);
        ed_placeWorking.setEnabled(true);
        ivUploadAvatar.setVisibility(View.VISIBLE);
        rbFmale.setEnabled(true);
        rbMale.setEnabled(true);
        rbOther.setEnabled(true);
    }

    private void disableAll() {
        ed_address.setEnabled(false);
        ed_birthday.setEnabled(false);
        ed_placeWorking.setEnabled(false);
        ivUploadAvatar.setVisibility(View.GONE);

        switch (currentDoctor.getGender()) {
            case 1: {
                rbFmale.setEnabled(false);
                rbOther.setEnabled(false);
                break;
            }
            case 2: {
                rbMale.setEnabled(false);
                rbOther.setEnabled(false);
                break;
            }
            case 3: {
                rbMale.setEnabled(false);
                rbFmale.setEnabled(false);
                break;
            }
        }

    }

    private int getGender() {
        int checked = 1;
        if (rbMale.isChecked()) {
            checked = 1;
        }
        if (rbFmale.isChecked()) {
            checked = 2;
        }
        if (rbOther.isChecked()) {
            checked = 3;
        }
        return checked;
    }

    private boolean onValidate() {
        boolean isValid = true;
        String placeWorking = ed_placeWorking.getText().toString();
        String address = ed_address.getText().toString();

        if (placeWorking == null || placeWorking.trim().length() == 0) {
            til_placeWorking.setError("Bạn phải nhập nơi làm việc hiện tại");
            isValid =  false;
        }else {
            til_placeWorking.setError(null);
        }

        if (address == null || address.trim().length() == 0) {
            til_address.setError("Bạn phải nhập địa chỉ hiện tại");
            isValid =  false;
        }else {
            til_address.setError(null);
        }
        return isValid;
    }

    private boolean onValidatePassword(String oldPassword,String newPassword, String confirmPassword, TextView tv_message_change_password) {

        boolean isValidate = true;
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        if(oldPassword.equals("") || newPassword.equals("") || confirmPassword.equals("")){
            tv_message_change_password.setText("Bạn phải nhập đầy đủ các ô mật khẩu!");
            tv_message_change_password.setVisibility(View.VISIBLE);
            isValidate = false;
        } else if(oldPassword.equals(newPassword)){
            tv_message_change_password.setText("Mật khẩu cũ và mật khẩu mới không thể giống nhau");
            tv_message_change_password.setVisibility(View.VISIBLE);
            isValidate = false;
        } else if (!pattern.matcher(newPassword).matches()) {
            tv_message_change_password.setText(getResources().getString(R.string.password_rule));
            tv_message_change_password.setVisibility(View.VISIBLE);
            isValidate = false;
        } else if (!newPassword.equals(confirmPassword)) {
            tv_message_change_password.setText(getResources().getString(R.string.confirm_password_error));
            tv_message_change_password.setVisibility(View.VISIBLE);
            isValidate = false;
        } else {
            tv_message_change_password.setVisibility(View.GONE);
        }
        return isValidate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void displayAttachImageDialog() {
        CharSequence[] items;
        if (mImageToBeAttached != null)
            items = new CharSequence[]{"Chụp ảnh", "Chọn ảnh", "Xóa ảnh"};
        else
            items = new CharSequence[]{"Chụp ảnh", "Chọn ảnh"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Ảnh đại diện");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    dispatchTakePhotoIntent();
                } else if (item == 1) {
                    dispatchChoosePhotoIntent();
                } else {
                    deleteCurrentPhoto();
                }
            }
        });
        builder.show();
    }

    public int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "Cannot create a temp image file", e);
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile));
                if (checkPermission()) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } else {
                    requestPermission();
                }
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, REQUEST_PERMISSION_CODE);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "TODO_LITE-" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;
            ZoomImageViewUtils.loadCircleImage(getContext(), currentDoctor.getAvatar(), ivAvatar);
        }
    }

    public MultipartBody.Part getImageUpload() {

        filename = UUID.randomUUID().toString();
        if (mImageToBeAttached != null) {
            File file = Utils.persistImage(mImageToBeAttached, filename, getContext());
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            return MultipartBody.Part.createFormData("imageChat", file.getName(), requestBody);
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                try {
                    ExifInterface exif = new ExifInterface(mImagePathToBeAttached);
                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                    int rotationAngle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                    mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, options.outWidth, options.outHeight, matrix, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                file.delete();
            }
            mImagePathToBeAttached = null;
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {

                Uri uri = data.getData();
                ContentResolver resolver = getActivity().getContentResolver();
                int rotationAngle = getOrientation(getActivity(), uri);
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(resolver, uri);
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, mImageToBeAttached.getWidth(), mImageToBeAttached.getHeight(), matrix, true);
            } catch (IOException e) {
                Log.e(TAG, "Cannot get a selected photo from the gallery.", e);
            }
        }
        updateUI();
    }


    public void updateUI() {
        if (mImageToBeAttached != null) {
            Log.e("UserProfileImage", "not null");
            ivAvatar.setImageBitmap(mImageToBeAttached);
            isChangeInfo = true;
        } else {
            Log.e("UserProfileImage", "is null");
            ivAvatar.setImageResource(R.drawable.patient_avatar);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }



}
