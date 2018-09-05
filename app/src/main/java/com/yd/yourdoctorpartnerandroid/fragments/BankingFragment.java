package com.yd.yourdoctorpartnerandroid.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.events.EventSend;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.banking.BankResponse;
import com.yd.yourdoctorpartnerandroid.networks.banking.GetBankingService;
import com.yd.yourdoctorpartnerandroid.networks.banking.MainBanking;
import com.yd.yourdoctorpartnerandroid.networks.banking.MakeRequestExchange;
import com.yd.yourdoctorpartnerandroid.networks.banking.RequestMakeExchange;
import com.yd.yourdoctorpartnerandroid.networks.banking.ResponseMakeExchange;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankingFragment extends Fragment {

    public static final String VALIDATE_NUMBER_BANK = "^[0-9]{9,14}+$";

    @BindView(R.id.tb_banking)
    Toolbar tbBanking;
    @BindView(R.id.sp_list_bank)
    Spinner spListBank;
    @BindView(R.id.ed_number_bank)
    EditText edNumberBank;
    @BindView(R.id.btn_exchange_bank)
    Button btnExchangeBank;
    @BindView(R.id.pv_banking)
    ProgressBar pvBanking;
    @BindView(R.id.tv_message_Banking)
    TextView tvMessageBanking;
    @BindView(R.id.sp_list_money)
    Spinner spListMoney;

    private Pattern pattern;

    private boolean check;
    private Doctor currentDoctor;

    private int amountChoice;
    private BankResponse bankResponseChoice;

    private String[] moneyType;
    private ArrayAdapter<String> adapterMoney;

    private ArrayList<BankResponse> bankResponses;
    private String[] arrayBanks;
    private ArrayAdapter<String> adapterArrayBank;

    public BankingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banking, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        setupUI();
        return view;
    }


    private void setupUI() {
        tbBanking.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        tbBanking.setTitle("Rút tiền");
        tbBanking.setTitleTextColor(getResources().getColor(R.color.primary_text));
        tbBanking.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenManager.backFragment(getFragmentManager());
            }
        });

        pattern = Pattern.compile(VALIDATE_NUMBER_BANK);
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);

        setUpSpinnerMoney();

        btnExchangeBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onVailidate()) {
                    postExchangeMoneyRequest();
                }
            }
        });
        getAllBanks();
    }

    private void postExchangeMoneyRequest(){
        pvBanking.setVisibility(View.VISIBLE);

        RequestMakeExchange requestMakeExchange = new RequestMakeExchange(currentDoctor.getDoctorId(),amountChoice,
                currentDoctor.getRemainMoney()-amountChoice,
                bankResponseChoice.getNameTransaction(),
                edNumberBank.getText().toString());

        MakeRequestExchange makeRequestExchange = RetrofitFactory.getInstance().createService(MakeRequestExchange.class);
        makeRequestExchange.makeRequestExchange(SharedPrefs.getInstance().get("JWT_TOKEN", String.class),requestMakeExchange ).enqueue(new Callback<ResponseMakeExchange>() {
            @Override
            public void onResponse(Call<ResponseMakeExchange> call, Response<ResponseMakeExchange> response) {
                if (response.code() == 200) {
                    if(response.body().getBankingId() != null){
                        tvMessageBanking.setText("Tạo yêu cầu rút tiền thành công, tiếp theo bạn cần nhập mã code");
                        tvMessageBanking.setTextColor(getResources().getColor(R.color.green));
                        currentDoctor.setRemainMoney(currentDoctor.getRemainMoney()-amountChoice);
                        SharedPrefs.getInstance().put("USER_INFO", currentDoctor);
                        EventBus.getDefault().post(new EventSend(1));

                        VerifyCodePhoneNumberFragment verifyCodePhoneNumberFragment = new VerifyCodePhoneNumberFragment();
                        verifyCodePhoneNumberFragment.setData(response.body().getBankingId());
                        ScreenManager.openFragment(getActivity().getSupportFragmentManager(), verifyCodePhoneNumberFragment, R.id.rl_container, true, true);
                    }
                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }

                pvBanking.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseMakeExchange> call, Throwable t) {
                Toast.makeText(getContext(), "Kết nốt mạng có vấn đề , không thể tải dữ liệu", Toast.LENGTH_LONG).show();
                pvBanking.setVisibility(View.GONE);
            }
        });
    }

    private void setUpSpinnerMoney() {
        moneyType = new String[5];
        moneyType[0] = "100.000 đ";
        moneyType[1] = "200.000 đ";
        moneyType[2] = "500.000 đ";
        moneyType[3] = "1.000.000 đ";
        moneyType[4] = "2.000.000 đ";

        adapterMoney = new ArrayAdapter<String>
                (
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        moneyType
                );

        adapterMoney.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);

        spListMoney.setAdapter(adapterMoney);

        amountChoice = 100000;

        spListMoney.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 0: {
                        amountChoice = 100000;
                        break;
                    }
                    case 1: {
                        amountChoice = 200000;
                        break;
                    }
                    case 2: {
                        amountChoice = 500000;
                        break;
                    }
                    case 3: {
                        amountChoice = 1000000;
                        break;
                    }
                    case 4: {
                        amountChoice = 2000000;
                        break;
                    }
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });


    }


    private void getAllBanks() {
        GetBankingService getBankingService = RetrofitFactory.getInstance().createService(GetBankingService.class);
        getBankingService.getAllBanks(SharedPrefs.getInstance().get("JWT_TOKEN", String.class)).enqueue(new Callback<MainBanking>() {
            @Override
            public void onResponse(Call<MainBanking> call, Response<MainBanking> response) {
                if (response.code() == 200) {
                    bankResponses = (ArrayList<BankResponse>) response.body().getListBank();
                    if (bankResponses != null && bankResponses.size() > 0) {
                        setUpspinnerBank();
                    } else {
                        Toast.makeText(getContext(), "Không tải được ngân hàng!", Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    Utils.backToLogin(getActivity().getApplicationContext());
                }

                pvBanking.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MainBanking> call, Throwable t) {
                Toast.makeText(getContext(), "Kết nốt mạng có vấn đề , không thể tải dữ liệu", Toast.LENGTH_LONG).show();
                pvBanking.setVisibility(View.GONE);
            }
        });
    }

    private void setUpspinnerBank() {

        arrayBanks = new String[bankResponses.size()];

        for (int i = 0; i < bankResponses.size(); i++) {
            arrayBanks[i] = bankResponses.get(i).getNameTransaction();
        }

        adapterArrayBank = new ArrayAdapter<String>
                (
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        arrayBanks
                );

        adapterArrayBank.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);

        spListBank.setAdapter(adapterArrayBank);

        bankResponseChoice = bankResponses.get(0);

        spListBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                bankResponseChoice = bankResponses.get(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });
    }

    private boolean onVailidate() {
        check = true;

        if (edNumberBank.getText() == null || edNumberBank.getText().equals("")) {
            check = false;
            tvMessageBanking.setVisibility(View.VISIBLE);
            tvMessageBanking.setText("Số tài khoản bị trống!");
        } else if (!edNumberBank.getText().toString().matches("^[0-9]{9,14}+$")) {
            check = false;
            tvMessageBanking.setVisibility(View.VISIBLE);
            tvMessageBanking.setText("Số tài khoản không hợp lệ!");
        } else if (amountChoice > currentDoctor.getRemainMoney()) {
            check = false;
            tvMessageBanking.setVisibility(View.VISIBLE);
            tvMessageBanking.setText("Số tiền rút vượt quá số tiền hiện tại trong tài khoản của bạn");
        }
        return check;
    }

}
