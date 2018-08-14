package com.yd.yourdoctorpartnerandroid.fragments;


import android.app.AlertDialog;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.BuildConfig;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.DoctorCertificationAdapter;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

    @BindView(R.id.iv_avatar)
    ImageView iv_avatar;

    @BindView(R.id.iv_upload_avatar)
    ImageView iv_upload_avatar;

    Unbinder butterKnife;

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

    @BindView(R.id.tv_universityGraduate)
    EditText tv_universityGraduate;

    @BindView(R.id.tv_yearGraduate)
    EditText tv_yearGraduate;

    @BindView(R.id.ed_placeWorking)
    EditText ed_placeWorking;

    @BindView(R.id.tv_certificates)
    EditText tv_certificates;

    @BindView(R.id.tv_specialist)
    EditText tv_specialist;

    @BindView(R.id.rv_image_certificates)
    RecyclerView rv_image_certificates;

    @BindView(R.id.btn_change_password)
    Button btn_change_password;

    @BindView(R.id.btn_edit_profile)
    Button btn_edit_profile;

    @BindView(R.id.pbProfileDoctor)
    ProgressBar pbProfileDoctor;

    private Doctor infoDoctor;

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

    private void setupUI(View view){

        butterKnife = ButterKnife.bind(DoctorProfileFragment.this, view);
        infoDoctor = SharedPrefs.getInstance().get("USER_INFO",Doctor.class);
        btn_change_password.setOnClickListener(this);
        btn_edit_profile.setOnClickListener(this);
        if(infoDoctor!=null){
            Picasso.with(getContext()).load(infoDoctor.getAvatar().toString()).transform(new CropCircleTransformation()).into(iv_avatar);
            tv_fname.setText(infoDoctor.getFirstName());
            tv_mname.setText(infoDoctor.getMiddleName());
            tv_lname.setText(infoDoctor.getLastName());
            rb_doctorRanking.setRating(infoDoctor.getCurrentRating());
            tv_remainMoney.setText(infoDoctor.getRemainMoney()+" VND");
            tv_phone.setText(infoDoctor.getPhoneNumber());
            switch (infoDoctor.getGender()){
                case 1:{
                    rbMale.setChecked(true);
                    rbFmale.setChecked(false);
                    rbOther.setChecked(false);
                    rbFmale.setEnabled(false);
                    rbOther.setEnabled(false);
                    break;
                }
                case 2:{
                    rbFmale.setChecked(true);
                    rbMale.setChecked(false);
                    rbOther.setChecked(false);
                    rbMale.setEnabled(false);
                    rbOther.setEnabled(false);
                    break;
                }
                case 3:{
                    rbMale.setChecked(false);
                    rbFmale.setChecked(false);
                    rbOther.setChecked(true);
                    rbMale.setEnabled(false);
                    rbFmale.setEnabled(false);
                    break;
                }
            }
        }
        ed_birthday.setText(infoDoctor.getBirthday());
        ed_address.setText(infoDoctor.getAddress());
        tv_universityGraduate.setText(infoDoctor.getUniversityGraduate());
        tv_yearGraduate.setText(infoDoctor.getYearGraduate());
        ed_placeWorking.setText(infoDoctor.getPlaceWorking());
        String spec = "";
        for (int i = 0; i <infoDoctor.getIdSpecialist().size();i++){
            spec += infoDoctor.getIdSpecialist().get(i).getName() + ", ";
        }
        tv_specialist.setText(spec);
//        tv_name_doctor.setText("Lê Thế Anh");
//        rb_doctorranking.setRating((float) 3);
//        rb_doctorranking.setMax(5);
//
//        ((AppCompatActivity)getActivity()).setSupportActionBar(tb_back_from_profile_doctor);
//        final ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
//        actionbar.setTitle("Trang cá nhân bác sĩ");
//
//        tb_back_from_profile_doctor.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ScreenManager.backFragment(getFragmentManager());
//            }
//        });

        DoctorCertificationAdapter doctorCertificationAdapter = new DoctorCertificationAdapter(infoDoctor.getCertificates(),getContext());
        rv_image_certificates.setAdapter(doctorCertificationAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false);
        rv_image_certificates.setLayoutManager(gridLayoutManager);
        disableControl();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        butterKnife.unbind();
    }

    private void disableControl(){
        tv_phone.setEnabled(false);
        tv_universityGraduate.setEnabled(false);
        tv_yearGraduate.setEnabled(false);
        tv_specialist.setEnabled(false);
        tv_certificates.setEnabled(false);
        ed_birthday.setEnabled(false);
        ed_address.setEnabled(false);
        ed_placeWorking.setEnabled(false);
        iv_upload_avatar.setVisibility(View.INVISIBLE);
    }

    private void enableControl(){
        ed_birthday.setEnabled(true);
        ed_address.setEnabled(true);
        ed_placeWorking.setEnabled(true);
        iv_upload_avatar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_profile:{
                enableControl();
                setUpCalendar();
                 break;
            }
//            case R.id.btn_change_password:{
//                showDialogChangePassword();
//                break;
//            }
//            case R.id.btn_yes: {
//                onSubmit();
//                break;
//            }
//            case R.id.btn_no: {
//                setScreenFunction(TYPE_CANCEL);
//                break;
//            }
        }
    }
    ///
    private void onSubmit() {
        String birthday = ed_birthday.getText().toString();
        String address = ed_address.getText().toString();
        String placeWorking = ed_placeWorking.getText().toString();
        int gender = getGender();
        if (infoDoctor.getGender() != gender) isChangeInfo = true;
        if (!infoDoctor.getBirthday().equals(birthday)) isChangeInfo = true;
        if (!infoDoctor.getAddress().equals(address)) isChangeInfo = true;
        if (!infoDoctor.getPlaceWorking().equals(placeWorking)) isChangeInfo = true;
        if (!isChangeInfo) {
            Toast.makeText(getContext(), "Bạn không thay đổi bất kỳ thông tin nào!", Toast.LENGTH_LONG).show();
            return;
        } else{
            Doctor newDoctor = infoDoctor;
            newDoctor.setAddress(address);
            newDoctor.setBirthday(birthday);
            newDoctor.setPlaceWorking(placeWorking);
            newDoctor.setGender(gender);
            onUpdateUser(newDoctor);
        }

    }
    private void onUpdateUser(final Doctor newDoctor) {
        pbProfileDoctor.setVisibility(View.VISIBLE);
        if (mImageToBeAttached != null) {
            GetLinkImageService getLinkeImageService = RetrofitFactory.getInstance().createService(GetLinkImageService.class);
            getLinkeImageService.uploadImageToGetLink(getImageUpload()).enqueue(new Callback<MainGetLink>() {
                @Override
                public void onResponse(Call<MainGetLink> call, Response<MainGetLink> response) {

                    if (response.code() == 200) {
                        updateDoctorNetWork(newDoctor, response.body().getFilePath());
                    } else {
                        Toast.makeText(getContext(), "Không thể kết nối máy chủ", Toast.LENGTH_LONG).show();
                        pbProfileDoctor.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<MainGetLink> call, Throwable t) {
                    Toast.makeText(getContext(), "Không thể kết nối máy chủ", Toast.LENGTH_LONG).show();
                    pbProfileDoctor.setVisibility(View.GONE);
                }
            });
        } else {
            updateDoctorNetWork(newDoctor, null);
        }

    }

    private void updateDoctorNetWork(Doctor newDoctor, String linkImage) {
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
                        infoDoctor.setGender(doctorRespone.getUpdateSuccess().getGender());
                        infoDoctor.setAddress(doctorRespone.getUpdateSuccess().getAddress());
                        infoDoctor.setAvatar(doctorRespone.getUpdateSuccess().getAvatar());
                        infoDoctor.setBirthday(doctorRespone.getUpdateSuccess().getBirthday());
                        infoDoctor.setPlaceWorking(doctorRespone.getUpdateSuccess().getPlaceWorking());
                        SharedPrefs.getInstance().put(USER_INFO, infoDoctor);
                        Toast.makeText(getContext(), "Chỉnh sửa thành công", Toast.LENGTH_LONG).show();
                        setScreenFunction(TYPE_CANCEL);
                    }
                } else {
                    Toast.makeText(getContext(), "Kết nối máy chủ không thành công", Toast.LENGTH_LONG).show();
                }
                pbProfileDoctor.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DoctorRespone> call, Throwable t) {
                Toast.makeText(getContext(), "Kết nối máy chủ không thành công", Toast.LENGTH_LONG).show();
                pbProfileDoctor.setVisibility(View.GONE);
            }
        });
    }
    private void setScreenFunction(int type) {
        if (type == TYPE_EDIT) {
            enableControl();
//            rlMainButton.setVisibility(View.GONE);
//            rlYesNoButton.setVisibility(View.VISIBLE);
        } else if (type == TYPE_CANCEL) {
            isChangeInfo = false;
            tv_fname.setText(infoDoctor.getFirstName());
            tv_mname.setText(infoDoctor.getMiddleName());
            tv_lname.setText(infoDoctor.getLastName());
            ZoomImageViewUtils.loadCircleImage(getContext(), infoDoctor.getAvatar().toString(), iv_avatar);
            tv_phone.setText(infoDoctor.getPhoneNumber());
            ed_address.setText(infoDoctor.getAddress());
            ed_birthday.setText(infoDoctor.getBirthday());
            tv_remainMoney.setText(infoDoctor.getRemainMoney() + " đ");
            switch (infoDoctor.getGender()) {
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
            disableControl();
//            rlMainButton.setVisibility(View.VISIBLE);
//            rlYesNoButton.setVisibility(View.GONE);
        }
        mImageToBeAttached = null;
        pbProfileDoctor.setVisibility(View.GONE);
    }
    ///
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
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ed_birthday.setText(sdf.format(myCalendar.getTime()));
    }
    ///////////////////////////////////
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
            ZoomImageViewUtils.loadCircleImage(getContext(), infoDoctor.getAvatar(), iv_avatar);
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
            iv_avatar.setImageBitmap(mImageToBeAttached);
            isChangeInfo = true;
        } else {
            Log.e("UserProfileImage", "is null");
            iv_avatar.setImageResource(R.drawable.patient_avatar);
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
    ///////////////////////////

}
