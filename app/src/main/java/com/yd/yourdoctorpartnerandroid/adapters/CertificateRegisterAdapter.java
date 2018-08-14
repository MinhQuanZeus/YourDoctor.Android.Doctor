package com.yd.yourdoctorpartnerandroid.adapters;

import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.events.ItemClickListener;
import com.yd.yourdoctorpartnerandroid.fragments.RegisterFragment.CertificateImage;
import com.yd.yourdoctorpartnerandroid.models.Certification;

import java.util.ArrayList;
import java.util.List;

public class CertificateRegisterAdapter extends RecyclerView.Adapter<CertificateRegisterAdapter.CertificateRegisterViewHolder> {
    private List<CertificateImage> chosenCertificationList;
    private Context context;
    private View.OnClickListener onClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onClickListener = onItemClickListener;
    }

    public CertificateRegisterAdapter(Context context) {
        this.context = context;
        chosenCertificationList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CertificateRegisterAdapter.CertificateRegisterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_certification, parent, false);
        view.setOnClickListener(onClickListener);
        return new CertificateRegisterAdapter.CertificateRegisterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CertificateRegisterAdapter.CertificateRegisterViewHolder holder, int position) {
        holder.setData(chosenCertificationList.get(position), position);

//        holder.ivCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return chosenCertificationList.size();
    }

    public class CertificateRegisterViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView tvCertificationProfile;
        ImageView ivCancel;
        ImageView ivCertificationProfile;
        private ItemClickListener itemClickListener;
        private CertificateImage certificationModel;
        private  int position;
        public CertificateRegisterViewHolder(View itemView) {
            super(itemView);
            tvCertificationProfile = itemView.findViewById(R.id.tv_certification_profile);
            ivCancel = itemView.findViewById(R.id.iv_cancel);
            ivCertificationProfile = itemView.findViewById(R.id.iv_certification_profile);

            ivCancel.setOnClickListener(this);
        }

        public void setData(CertificateImage certificationModel , int position) {
            this.position = position;
            this.certificationModel = certificationModel;
            if (certificationModel != null) {
                ivCertificationProfile.setImageBitmap(certificationModel.getmImageToBeAttached());
                tvCertificationProfile.setText(certificationModel.getName());
            }
        }


        public CertificateImage getcertificationModel() {
            return certificationModel;
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.iv_cancel){
                removeAt(position);
            }
        }

    }

    public List<CertificateImage> getChosenCertificationList() {
        return chosenCertificationList;
    }

    public void removeAt(int position) {
        chosenCertificationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, chosenCertificationList.size());
    }

    public void add(CertificateImage certificateImage){
        chosenCertificationList.add(certificateImage);
        notifyItemInserted(chosenCertificationList.size() - 1);
    }
}

