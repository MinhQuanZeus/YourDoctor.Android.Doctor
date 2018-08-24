package com.yd.yourdoctorpartnerandroid.fragments;


import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.BuildConfig;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.CertificateRegisterAdapter;
import com.yd.yourdoctorpartnerandroid.models.Specialist;
import com.yd.yourdoctorpartnerandroid.networks.registerDoctor.CertificateRequest;
import com.yd.yourdoctorpartnerandroid.networks.registerDoctor.DoctorRegister;
import com.yd.yourdoctorpartnerandroid.models.SpecialistDoctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getLinkImageService.GetLinkImageService;
import com.yd.yourdoctorpartnerandroid.networks.getLinkImageService.MainGetLink;
import com.yd.yourdoctorpartnerandroid.networks.getSpecialistService.GetSpecialistService;
import com.yd.yourdoctorpartnerandroid.networks.getSpecialistService.MainObjectSpecialist;
import com.yd.yourdoctorpartnerandroid.networks.registerDoctor.RegisterDoctorService;
import com.yd.yourdoctorpartnerandroid.networks.registerDoctor.RegisterResponse;
import com.yd.yourdoctorpartnerandroid.utils.LoadDefaultModel;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {
    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String USER_INFO = "USER_INFO";

    public static final String TAG = "RegisterFragment";
    public static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})";

    private String phoneNumber;

    private String mImagePathToBeAttached;
    MultipartBody.Part multipartBodyPartAvatar;
    private String filename;
    private Bitmap mImageToBeAttached;

    ArrayList<CertificateImage> certificateImageArrayList;

    //Display 2 option layout
    @BindView(R.id.relative_group_certificate)
    RelativeLayout relativeGroupCertificate;
    @BindView(R.id.rl_main_register)
    RelativeLayout rlMainRegister;

    //Group layout about adding certificate
    @BindView(R.id.til_name_certificate)
    TextInputLayout til_name_certificate;
    @BindView(R.id.btn_cancel_add)
    Button btnCancelCertificate;
    @BindView(R.id.btn_ok_certificate)
    Button btnOkCertificate;
    @BindView(R.id.ed_name_certificate)
    EditText ed_name_certificate;
    @BindView(R.id.iv_Certificate)
    CircleImageView ivCertificate;
    @BindView(R.id.iv_upload_certificate)
    CircleImageView ivUploadCertificate;

    //To check is this system in main process
    private boolean isInProcessAddingCertificate;

    private ArrayList<Specialist> specialists;
    private ArrayList<Specialist> specialistsChoice;

    public RegisterFragment setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.iv_upload_avatar)
    CircleImageView ivUploadAvatar;

    @BindView(R.id.ed_fname)
    EditText edFname;
    @BindView(R.id.ed_mname)
    EditText edMname;
    @BindView(R.id.ed_lname)
    EditText edLname;
    @BindView(R.id.ed_phone)
    EditText edPhone;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.ed_confirm_password)
    EditText edCormfirmPassword;
    @BindView(R.id.ed_birthday)
    EditText edBirthday;
    @BindView(R.id.ed_address)
    EditText edAddress;
    @BindView(R.id.ed_place_graduate)
    EditText ed_place_graduate;
    @BindView(R.id.ed_year_graduate)
    EditText ed_year_graduate;
    @BindView(R.id.ed_place_working)
    EditText ed_place_working;


    @BindView(R.id.rg_gender)
    RadioGroup groupGender;

    @BindView(R.id.radio_male)
    RadioButton rbMale;
    @BindView(R.id.radio_other)
    RadioButton rbOther;
    @BindView(R.id.radio_female)
    RadioButton rbFmale;
    @BindView(R.id.pb_register)
    ProgressBar pb_register;

    @BindView(R.id.btn_sign_up)
    CircularProgressButton btnSignUp;

    @BindView(R.id.til_fname)
    TextInputLayout tilFname;
    @BindView(R.id.til_mname)
    TextInputLayout tilMname;
    @BindView(R.id.til_lname)
    TextInputLayout tillname;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.til_confirm_password)
    TextInputLayout tilConfirmPassword;
    @BindView(R.id.til_place_graduate)
    TextInputLayout til_place_graduate;
    @BindView(R.id.til_year_graduate)
    TextInputLayout til_year_graduate;
    @BindView(R.id.til_place_working)
    TextInputLayout til_place_working;

    @BindView(R.id.cb_list_specialist)
    ListView cb_list_specialist;

    @BindView(R.id.btn_certificate)
    Button btn_certificate;

    @BindView(R.id.rl_certification)
    RecyclerView rl_certification;

    @BindView(R.id.tb_main)
    Toolbar toolbar;



    LinearLayoutManager linearLayoutManager;
    CertificateRegisterAdapter certificateRegisterAdapter;

    List<CertificateRequest> listCertification;
    int countCertificate;
    int totalCertificate;

    private DoctorRegister doctorRegister;

    private Unbinder unbinder;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        setUp(view);
        return view;
    }

    private void setUp(View view) {
        unbinder = ButterKnife.bind(this, view);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle(R.string.sign_up);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primary_text));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        rl_certification.setFocusable(false);
        cb_list_specialist.setFocusable(false);
        isInProcessAddingCertificate = false;
        certificateImageArrayList = new ArrayList<>();
        specialistsChoice = new ArrayList<>();
        edPhone.setText(phoneNumber);
        edPhone.setEnabled(false);
        edPhone.setEnabled(false);

        btnCancelCertificate.setOnClickListener(this);
        btnOkCertificate.setOnClickListener(this);
        ivUploadCertificate.setOnClickListener(this);
        btn_certificate.setOnClickListener(this);
        ivUploadAvatar.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        listCertification = new ArrayList<>();

        certificateRegisterAdapter = new CertificateRegisterAdapter(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rl_certification.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rl_certification.addItemDecoration(dividerItemDecoration);
        rl_certification.setItemAnimator(new DefaultItemAnimator());
        rl_certification.setAdapter(certificateRegisterAdapter);

        setUpCalendar();
        setUpCheckBoxListSpecialist();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_certificate: {
                isInProcessAddingCertificate = true;
                relativeGroupCertificate.setVisibility(View.VISIBLE);
                rlMainRegister.setVisibility(View.GONE);
                mImageToBeAttached = null;
                break;
            }
            case R.id.iv_upload_avatar: {
                displayAttachImageDialog();
                break;
            }
            case R.id.btn_sign_up: {
                onSubmit();
                break;
            }
            case R.id.iv_upload_certificate: {
                displayAttachImageDialog();
                break;
            }
            case R.id.btn_cancel_add: {
                isInProcessAddingCertificate = false;
                relativeGroupCertificate.setVisibility(View.GONE);
                rlMainRegister.setVisibility(View.VISIBLE);
                ed_name_certificate.setText("");
                ivCertificate.setImageResource(R.drawable.patient_avatar);
                mImageToBeAttached = null;
                break;
            }
            case R.id.btn_ok_certificate: {
                if (ed_name_certificate.getText() == null || ed_name_certificate.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Bạn phải đặt tên chứng chỉ!", Toast.LENGTH_LONG).show();
                } else if (mImageToBeAttached == null) {
                    Toast.makeText(getContext(), "Bạn chưa chọn ảnh!", Toast.LENGTH_LONG).show();
                } else {
                    isInProcessAddingCertificate = false;
                    relativeGroupCertificate.setVisibility(View.GONE);
                    rlMainRegister.setVisibility(View.VISIBLE);

                    certificateRegisterAdapter.add(new CertificateImage(ed_name_certificate.getText().toString(),
                            getImageUpload(), mImageToBeAttached));
                    ed_name_certificate.setText("");
                    ivCertificate.setImageResource(R.drawable.patient_avatar);
                    //certificateImageArrayList.add(new CertificateImage(ed_name_certificate.getText().toString(), getImageUpload(), mImageToBeAttached));
                    mImageToBeAttached = null;
                }

                break;
            }
        }
    }


    private void setUpCheckBoxListSpecialist() {
        cb_list_specialist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cb_list_specialist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                if (currentCheck) {
                    specialistsChoice.add(specialists.get(position));
                } else {
                    specialistsChoice.remove(specialists.get(position));
                }
            }

        });
        loadSpecialist();

    }

    private void loadSpecialist() {
        GetSpecialistService getSpecialistService = RetrofitFactory.getInstance().createService(GetSpecialistService.class);
        getSpecialistService.getMainObjectSpecialist().enqueue(new Callback<MainObjectSpecialist>() {
            @Override
            public void onResponse(Call<MainObjectSpecialist> call, Response<MainObjectSpecialist> response) {
                MainObjectSpecialist mainObjectSpecialist = response.body();
                specialists = (ArrayList<Specialist>) mainObjectSpecialist.getListSpecialist();
                if (specialists != null) {
                    LoadDefaultModel.getInstance().setSpecialists(specialists);
                    ArrayAdapter<Specialist> arrayAdapter
                            = new ArrayAdapter<Specialist>(getContext(), android.R.layout.simple_list_item_checked, specialists);
                    cb_list_specialist.setAdapter(arrayAdapter);
                }
                pb_register.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MainObjectSpecialist> call, Throwable t) {
                pb_register.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Không tải được danh sách chuyên khoa, Bạn nên tải lại trang", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
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

        edBirthday.setOnClickListener(new View.OnClickListener() {

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
        calendar.add(Calendar.YEAR, -100);

        if (myCalendar.getTimeInMillis() >= (Calendar.getInstance().getTimeInMillis())) {
            Toast.makeText(getContext(), "Bạn không thể chọn ngày sinh của bạn sau thời gian hiện tại", Toast.LENGTH_LONG).show();
        } else if (myCalendar.getTimeInMillis() <= calendar.getTimeInMillis()) {
            Toast.makeText(getContext(), "Năm sinh của bạn không hợp lệ", Toast.LENGTH_LONG).show();
        } else {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edBirthday.setText(sdf.format(myCalendar.getTime()));
        }

    }

    private void onSubmit() {
        if (!onValidate()) {
            return;
        }
        btnSignUp.startAnimation();
        onCreateUser();
    }



    private void onCreateUser() {
        countCertificate = 0;
        totalCertificate = certificateRegisterAdapter.getChosenCertificationList().size();

        doctorRegister = new DoctorRegister();

        ArrayList<SpecialistDoctor> specialistDoctors = new ArrayList<>();
        for (Specialist specialist : specialistsChoice) {
            specialistDoctors.add(new SpecialistDoctor(specialist.getId(), specialist.getName()));
        }

        doctorRegister.setIdSpecialist(specialistDoctors);

        GetLinkImageService getLinkeImageService = RetrofitFactory.getInstance().createService(GetLinkImageService.class);
        getLinkeImageService.uploadImageToGetLink(multipartBodyPartAvatar).enqueue(new Callback<MainGetLink>() {
            @Override
            public void onResponse(Call<MainGetLink> call, Response<MainGetLink> response) {

                if (response.code() == 200) {

                    doctorRegister.setAvatar(response.body().getFilePath());
                    if (totalCertificate > 0) {
                        uploadCertificate(certificateRegisterAdapter.getChosenCertificationList());
                    } else {
                        uploadDoctor();
                    }

                } else {
                    Toast.makeText(getContext(), "Lỗi đã xảy ra, không thể tải lên trai chủ", Toast.LENGTH_LONG).show();
                    pb_register.setVisibility(View.GONE);
                    refreshDataForFailed();
                }
            }

            @Override
            public void onFailure(Call<MainGetLink> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi đã xảy ra, không thể tải lên trai chủ", Toast.LENGTH_LONG).show();
                refreshDataForFailed();
                pb_register.setVisibility(View.GONE);
            }
        });

    }

    public void refreshDataForFailed() {
        totalCertificate = 0;
        countCertificate = 0;
        listCertification.clear();
    }

    public void uploadCertificate(List<CertificateImage> certificateImages) {
        CertificateImage certificateImage = certificateImages.get(countCertificate);
        GetLinkImageService getLinkeImageService = RetrofitFactory.getInstance().createService(GetLinkImageService.class);
        getLinkeImageService.uploadImageToGetLink(certificateImage.getCertificateUpload()).enqueue(new Callback<MainGetLink>() {
            @Override
            public void onResponse(Call<MainGetLink> call, Response<MainGetLink> response) {

                if (response.code() == 200) {
                    listCertification.add(new CertificateRequest(certificateImage.getName(), response.body().getFilePath()));
                    countCertificate++;
                    if (countCertificate == totalCertificate) {
                        doctorRegister.setCertificates((ArrayList<CertificateRequest>) listCertification);
                        uploadDoctor();
                    } else {
                        uploadCertificate(certificateRegisterAdapter.getChosenCertificationList());
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể kết nối máy chủ", Toast.LENGTH_LONG).show();
                    refreshDataForFailed();
                    pb_register.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MainGetLink> call, Throwable t) {
                Toast.makeText(getContext(), "Không thể kết nối máy chủ", Toast.LENGTH_LONG).show();
                refreshDataForFailed();
                pb_register.setVisibility(View.GONE);
            }
        });


    }

    public void uploadDoctor() {
        doctorRegister.setPhoneNumber(phoneNumber);
        doctorRegister.setFirstName(edFname.getText().toString());
        doctorRegister.setMiddleName(edMname.getText().toString());
        doctorRegister.setLastName(edLname.getText().toString());
        doctorRegister.setPassword(edPassword.getText().toString());
        doctorRegister.setAddress(edAddress.getText().toString());
        doctorRegister.setBirthday(edBirthday.getText().toString());
        doctorRegister.setPlaceWorking(ed_place_working.getText().toString());
        doctorRegister.setYearGraduate(ed_year_graduate.getText().toString());
        doctorRegister.setUniversityGraduate(ed_place_graduate.getText().toString());
        doctorRegister.setRemainMoney(0);
        doctorRegister.setGender(getGender());
        RegisterDoctorService registerDoctorService = RetrofitFactory.getInstance().createService(RegisterDoctorService.class);
        registerDoctorService.register(doctorRegister)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                        if (response.code() == 200 || response.code() == 201) {
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            btnSignUp.revertAnimation();
                            //getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getContext(), "Không thể tải dữ liệu, Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                            btnSignUp.revertAnimation();

                        }
                        refreshDataForFailed();
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        btnSignUp.revertAnimation();
                        refreshDataForFailed();
                        Toast.makeText(getContext(), "Không thể tải dữ liệu, Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                    }
                });
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
        boolean isValidate = true;
        String fname = edFname.getText().toString();
        String lname = edLname.getText().toString();
        String mName = edMname.getText().toString();
        String password = edPassword.getText().toString();
        String yearGraduate = ed_year_graduate.getText().toString();
        String placeGraduate = ed_place_graduate.getText().toString();
        String placeWorking = ed_place_working.getText().toString();
        String confirmPassword = edCormfirmPassword.getText().toString();
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

        if (fname == null || fname.trim().length() == 0) {
            isValidate = false;
            tilFname.setError(getResources().getString(R.string.fname_required));
        } else if(!Utils.verifyVietnameesName(fname)){
            isValidate = false;
            tilFname.setError("Họ không hợp lệ");
        }
        else {
            tilFname.setError(null);
        }

        if (lname == null || lname.trim().length() == 0) {
            isValidate = false;
            tillname.setError(getResources().getString(R.string.lname_required));
        }else if(!Utils.verifyVietnameesName(lname)){
            isValidate = false;
            tillname.setError("Tên không hợp lệ");
        }
        else {
            tillname.setError(null);
        }

        if(mName != null && mName.trim().length() > 0){
            if(!Utils.verifyVietnameesName(mName)){
                isValidate = false;
                tilMname.setError("Tên đệm không hợp lệ");
            }
        }

        if (!pattern.matcher(password).matches()) {
            isValidate = false;
            tilPassword.setError(getResources().getString(R.string.password_rule));
        } else {
            tilPassword.setError(null);
        }
        if (!password.equals(confirmPassword)) {
            isValidate = false;
            tilConfirmPassword.setError(getResources().getString(R.string.confirm_password_error));
        } else {
            tilConfirmPassword.setError(null);
        }

        if (placeGraduate == null || placeGraduate.trim().length() == 0) {
            isValidate = false;
            til_place_graduate.setError("Bạn nên nhập nơi tốt nghiệp");
        } else {
            til_place_graduate.setError(null);
        }

        if (yearGraduate == null || yearGraduate.trim().length() == 0) {
            isValidate = false;
            til_year_graduate.setError("Bạn nên nhập năm tốt nghiệp");
        } else {
            til_year_graduate.setError(null);
        }

        if (placeWorking == null || placeWorking.trim().length() == 0) {
            isValidate = false;
            til_place_working.setError("Bạn nên nhập nơi làm việc hiện tại");
        } else {
            til_place_working.setError(null);
        }


        if (specialistsChoice.size() == 0) {
            Toast.makeText(getContext(), "Bạn chưa lựa chọn chuyên khoa", Toast.LENGTH_LONG).show();
            isValidate = false;
        }


        return isValidate;

    }

    private void displayAttachImageDialog() {
        CharSequence[] items;
        items = new CharSequence[]{"Chụp ảnh", "Chọn ảnh"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn ảnh");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    dispatchTakePhotoIntent();
                } else if (item == 1) {
                    dispatchChoosePhotoIntent();
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
        if (!isInProcessAddingCertificate) {
            if (mImageToBeAttached != null) {
                ivAvatar.setImageBitmap(mImageToBeAttached);
                multipartBodyPartAvatar = getImageUpload();
            } else {
                ivAvatar.setImageResource(R.drawable.patient_avatar);
            }
            mImageToBeAttached = null;
        } else {
            if (mImageToBeAttached != null) {
                ivCertificate.setImageBitmap(mImageToBeAttached);
            } else {
                ivCertificate.setImageResource(R.drawable.patient_avatar);
            }
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public class CertificateImage {
        String name;
        MultipartBody.Part certificateUpload;
        Bitmap mImageToBeAttached;
        String link;

        public CertificateImage(String name, MultipartBody.Part certificateUpload, Bitmap mImageToBeAttached) {
            this.name = name;
            this.certificateUpload = certificateUpload;
            this.mImageToBeAttached = mImageToBeAttached;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public MultipartBody.Part getCertificateUpload() {
            return certificateUpload;
        }

        public void setCertificateUpload(MultipartBody.Part certificateUpload) {
            this.certificateUpload = certificateUpload;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Bitmap getmImageToBeAttached() {
            return mImageToBeAttached;
        }

        public void setmImageToBeAttached(Bitmap mImageToBeAttached) {
            this.mImageToBeAttached = mImageToBeAttached;
        }
    }

}

