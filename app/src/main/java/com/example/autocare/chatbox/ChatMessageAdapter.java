package com.example.autocare.chatbox;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;

import java.util.ArrayList;

public class ChatMessageAdapter
        extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    private final ArrayList<ChatMessage> danhSachTinNhan;

    public ChatMessageAdapter(ArrayList<ChatMessage> danhSachTinNhan) {
        this.danhSachTinNhan = danhSachTinNhan;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder, int position) {

        ChatMessage message = danhSachTinNhan.get(position);
        holder.txtNoiDung.setText(message.getText());

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)
                        holder.txtNoiDung.getLayoutParams();

        params.gravity = message.isFromUser()
                ? Gravity.END : Gravity.START;

        params.leftMargin = dp(holder.itemView, 4);
        params.rightMargin = dp(holder.itemView, 4);

        holder.txtNoiDung.setLayoutParams(params);

        GradientDrawable bubble = new GradientDrawable();
        bubble.setCornerRadius(dp(holder.itemView, 18));

        if (message.isFromUser()) {
            bubble.setColor(Color.rgb(25, 118, 210));
            holder.txtNoiDung.setTextColor(Color.WHITE);
        } else {
            bubble.setColor(Color.WHITE);
            holder.txtNoiDung.setTextColor(Color.rgb(38, 50, 56));
        }

        holder.txtNoiDung.setBackground(bubble);
    }

    @Override
    public int getItemCount() {
        return danhSachTinNhan.size();
    }

    private int dp(View view, int value) {
        return (int) (value
                * view.getResources().getDisplayMetrics().density);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView txtNoiDung;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNoiDung =
                    itemView.findViewById(R.id.txtNoiDungTinNhan);
        }
    }
}