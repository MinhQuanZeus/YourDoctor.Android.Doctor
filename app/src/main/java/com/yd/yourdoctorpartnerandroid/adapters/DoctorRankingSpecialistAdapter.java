package com.yd.yourdoctorpartnerandroid.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.events.ItemClickListener;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class DoctorRankingSpecialistAdapter extends RecyclerView.Adapter<DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder> {
    private List<Doctor> rankingDoctorList;
    private Context context;
    private View.OnClickListener onClickListener;
    private boolean isLoadingAdded = false;

    private static final int ITEM = 0;
    private static final int LOADING = 1;


    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onClickListener = onItemClickListener;
    }

    public DoctorRankingSpecialistAdapter(Context context) {

        this.context = context;
        this.rankingDoctorList = new ArrayList<>();
    }

    public List<Doctor> getDoctors() {
        return rankingDoctorList;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.rankingDoctorList = doctors;
    }


    @NonNull
    @Override
    public DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder viewHolder = null;
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
    private DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_doctor_ranking, parent, false);
        viewHolder = new DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case ITEM:
                holder.setData(rankingDoctorList.get(position), position);
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == rankingDoctorList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(Doctor mc) {
        rankingDoctorList.add(mc);
        notifyItemInserted(rankingDoctorList.size() - 1);
    }

    public void addAll(List<Doctor> mcList) {
        for (Doctor mc : mcList) {
            add(mc);
        }
    }

    public void remove(Doctor city) {
        int position = rankingDoctorList.indexOf(city);
        if (position > -1) {
            rankingDoctorList.remove(position);
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
        add(new Doctor());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = rankingDoctorList.size() - 1;
        Doctor item = getItem(position);

        if (item != null) {
            rankingDoctorList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Doctor getItem(int position) {
        return rankingDoctorList.get(position);
    }

    @Override
    public int getItemCount() {
        return rankingDoctorList == null ? 0 : rankingDoctorList.size();
    }

    public class DoctorRankingSpecialistViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumberRank;
        TextView tvNameDoctorRanking;
        ImageView ivItemDoctorRanking;
        RatingBar rbDoctorRanking;

        private Doctor doctorModel;

        public DoctorRankingSpecialistViewHolder(View itemView) {
            super(itemView);
            tvNumberRank = itemView.findViewById(R.id.tvNumberRank);
            tvNameDoctorRanking = itemView.findViewById(R.id.tvNameDoctorRanking);
            ivItemDoctorRanking = itemView.findViewById(R.id.ivItemDoctorRanking);
            rbDoctorRanking = itemView.findViewById(R.id.rbDoctorRanking);

        }

        public void setData(Doctor doctorModel, int positon) {

            this.doctorModel = doctorModel;

            if (doctorModel != null) {
                if (context == null) Log.d("Anhle", "context bi null");
                ZoomImageViewUtils.loadCircleImage(context,doctorModel.getAvatar(),ivItemDoctorRanking);
                tvNameDoctorRanking.setText(doctorModel.getFullName());
                rbDoctorRanking.setRating(doctorModel.getCurrentRating());
                tvNumberRank.setText((positon + 1) + "");

            }
        }

        public Doctor getdoctorModel() {
            return doctorModel;
        }

    }

    protected class LoadingVH extends DoctorRankingSpecialistViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

}


