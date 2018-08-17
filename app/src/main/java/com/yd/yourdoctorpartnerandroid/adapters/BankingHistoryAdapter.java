package com.yd.yourdoctorpartnerandroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.networks.getBankingHistory.ObjectBanking;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;
import java.util.ArrayList;
import java.util.List;



public class BankingHistoryAdapter extends RecyclerView.Adapter<BankingHistoryAdapter.BankingHistoryViewHolder> {
    private List<ObjectBanking> objectBankings;
    Doctor currentDoctor;
    private Context context;
    private View.OnClickListener onClickListener;
    private boolean isLoadingAdded = false;

    private static final int ITEM = 0;
    private static final int LOADING = 1;



    public BankingHistoryAdapter(Context context) {

        this.context = context;
        currentDoctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
        this.objectBankings = new ArrayList<>();
    }


    @NonNull
    @Override
    public BankingHistoryAdapter.BankingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BankingHistoryAdapter.BankingHistoryViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_process, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;

    }

    @NonNull
    private BankingHistoryAdapter.BankingHistoryViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        BankingHistoryAdapter.BankingHistoryViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_banking_history, parent, false);
        viewHolder = new BankingHistoryAdapter.BankingHistoryViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BankingHistoryAdapter.BankingHistoryViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case ITEM:
                //DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder movieVH = (DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder) holder;
                holder.setData(objectBankings.get(position));

                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == objectBankings.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(ObjectBanking mc) {
        objectBankings.add(mc);
        notifyItemInserted(objectBankings.size() - 1);
    }

    public void addAll(List<ObjectBanking> objectBankings) {
        for (ObjectBanking objectBanking : objectBankings) {
            add(objectBanking);
        }
    }

    public void remove(ObjectBanking objectBanking) {
        int position = objectBankings.indexOf(objectBanking);
        if (position > -1) {
            objectBankings.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ObjectBanking());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = objectBankings.size() - 1;
        ObjectBanking item = getItem(position);

        if (item != null) {
            objectBankings.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ObjectBanking getItem(int position) {
        return objectBankings.get(position);
    }

    @Override
    public int getItemCount() {
        return objectBankings == null ? 0 : objectBankings.size();
    }

    public class BankingHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBankingHistory;
        TextView tvTitleBanking;
        TextView tvContentBanking;
        TextView tvTimeBanking;
        TextView tvStatus;
        private ObjectBanking objectBanking;

        public BankingHistoryViewHolder(View itemView) {
            super(itemView);
            ivBankingHistory = itemView.findViewById(R.id.iv_banking_history);
            tvTitleBanking = itemView.findViewById(R.id.tv_title_banking);
            tvContentBanking = itemView.findViewById(R.id.tv_content_banking);
            tvTimeBanking = itemView.findViewById(R.id.tv_time_banking);
            tvStatus = itemView.findViewById(R.id.tv_status_banking);


        }

        public void setData(ObjectBanking objectBanking) {

            this.objectBanking = objectBanking;

            if (objectBanking != null) {
                ivBankingHistory.setImageResource(R.drawable.your_doctor_logo);
                tvTitleBanking.setText("Yêu cầu rút tiền với hệ thống");
                tvContentBanking.setText("Số tiền giao dịch " + objectBanking.getAmount() +"đ, Số tiền hiện tại " + objectBanking.getRemainMoney() +"đ");
                tvTimeBanking.setText("Thời gian tạo : " + Utils.convertTimeFromMonggo(objectBanking.getCreatedAt()));

                switch (objectBanking.getStatus()){
                    case 1:{
                        tvStatus.setText("Trạng thái : Mới tạo");
                        break;
                    }
                    case 2:{
                        tvStatus.setText("Trạng thái : Đã xác nhận, đang chờ xử lý từ hệ thống");
                        break;
                    }
                    case 3:{
                        tvStatus.setText("Trạng thái : Đã gửi tiền");
                        break;
                    }
                }

            }
        }


        public ObjectBanking getObjectBanking() {
            return objectBanking;
        }


    }

    protected class LoadingVH extends BankingHistoryViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

}


