package com.yd.yourdoctorpartnerandroid.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.activities.ChatActivity;
import com.yd.yourdoctorpartnerandroid.events.ItemClickListener;
import com.yd.yourdoctorpartnerandroid.models.HistoryChat;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.Utils;
import com.yd.yourdoctorpartnerandroid.utils.ZoomImageViewUtils;

import java.util.ArrayList;
import java.util.List;


public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ChatHistoryViewHolder> {
    private List<HistoryChat> historyChats;
    Patient currentPatient;
    private Context context;
    private View.OnClickListener onClickListener;
    private boolean isLoadingAdded = false;

    private static final int ITEM = 0;
    private static final int LOADING = 1;


    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onClickListener = onItemClickListener;
    }

    public ChatHistoryAdapter(Context context) {

        this.context = context;
        currentPatient = SharedPrefs.getInstance().get("USER_INFO", Patient.class);

        this.historyChats = new ArrayList<>();
    }


    @NonNull
    @Override
    public ChatHistoryAdapter.ChatHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChatHistoryAdapter.ChatHistoryViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_process, parent, false);
                // v2.setOnClickListener(onClickListener);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;

    }

    @NonNull
    private ChatHistoryAdapter.ChatHistoryViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ChatHistoryAdapter.ChatHistoryViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_chat_history, parent, false);
        viewHolder = new ChatHistoryAdapter.ChatHistoryViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatHistoryAdapter.ChatHistoryViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case ITEM:
                //DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder movieVH = (DoctorRankingSpecialistAdapter.DoctorRankingSpecialistViewHolder) holder;

                holder.setData(historyChats.get(position));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.e("chatHistoryIdClick",holder.getHistoryChat().getId() );
                        Log.e("patientChoiceIdClick", holder.getHistoryChat().getPatientID().getId());
                        intent.putExtra("chatHistoryId", holder.getHistoryChat().getId());
                        intent.putExtra("patientChoiceId", holder.getHistoryChat().getPatientID().getId());
                        context.startActivity(intent);

                    }
                });
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == historyChats.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(HistoryChat mc) {
        historyChats.add(mc);
        notifyItemInserted(historyChats.size() - 1);
    }

    public void addAll(List<HistoryChat> chatHistoryResponses) {
        for (HistoryChat chatHistoryResponse : chatHistoryResponses) {
            add(chatHistoryResponse);
        }
    }

    public void remove(HistoryChat chatHistoryResponse) {
        int position = historyChats.indexOf(chatHistoryResponse);
        if (position > -1) {
            historyChats.remove(position);
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
        add(new HistoryChat());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = historyChats.size() - 1;
        HistoryChat item = getItem(position);

        if (item != null) {
            historyChats.remove(position);
            notifyItemRemoved(position);
        }
    }

    public HistoryChat getItem(int position) {
        return historyChats.get(position);
    }

    @Override
    public int getItemCount() {
        return historyChats == null ? 0 : historyChats.size();
    }

    public class ChatHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView ivChatHistory;
        TextView tvTitleNotification;
        TextView tvTimeChatHistory;
        TextView tvStatusChatHistory;
        TextView tvContentHistory;
        private ItemClickListener itemClickListener;
        private HistoryChat historyChat;

        public ChatHistoryViewHolder(View itemView) {
            super(itemView);
            ivChatHistory = itemView.findViewById(R.id.iv_chat_history);
            tvTitleNotification = itemView.findViewById(R.id.tv_title_notification);
            tvTimeChatHistory = itemView.findViewById(R.id.tv_time_chat_history);
            tvStatusChatHistory = itemView.findViewById(R.id.tv_status_chat_history);
            tvContentHistory = itemView.findViewById(R.id.tv_content_history);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setData(HistoryChat historyChat) {

            this.historyChat = historyChat;

            if (historyChat != null) {
                if (context == null) Log.d("Anhle", "context bi null");
                ZoomImageViewUtils.loadCircleImage(context, historyChat.getPatientID().getAvatar() ,ivChatHistory);
                tvTitleNotification.setText("Cuộc chat với BN." + historyChat.getPatientID().getFullName());
                tvTimeChatHistory.setText("Thời gian tạo: " + Utils.convertTime(historyChat.getCreatedAt())
                        +", Tin nhắn cuối cùng: " + Utils.convertTime(historyChat.getUpdatedAt())
                );
                //getcontent topic
                tvContentHistory.setText("Nội dung: " + historyChat.getContentTopic());
                if(historyChat.getStatus() == 1){
                    tvStatusChatHistory.setText("Trạng thái: Chưa Hoàn thành");

                }else {
                    tvStatusChatHistory.setText("Trạng thái: Hoàn thành");
                }

            }
        }


        public HistoryChat getHistoryChat() {
            return historyChat;
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }

    protected class LoadingVH extends ChatHistoryViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

}

