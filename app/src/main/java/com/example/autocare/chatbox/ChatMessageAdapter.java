
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

        holder.txtNoiDung.setText(formatChatText(message.getText()));

        // Căn chỉnh vị trí bong bóng chat
        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams)
                        holder.txtNoiDung.getLayoutParams();

        params.gravity = message.isFromUser()
                ? Gravity.END : Gravity.START;

        // Giới hạn chiều rộng bong bóng theo màn hình
        int screenWidth = holder.itemView.getResources()
                .getDisplayMetrics().widthPixels;

        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        holder.txtNoiDung.setMaxWidth((int) (screenWidth * 0.78f));

        params.leftMargin = dp(holder.itemView, 4);
        params.rightMargin = dp(holder.itemView, 4);

        holder.txtNoiDung.setLayoutParams(params);

        // Tạo giao diện bong bóng
        GradientDrawable bubble = new GradientDrawable();

        if (message.isFromUser()) {

            // Tin nhắn người dùng
            bubble.setColor(Color.rgb(25, 118, 210));

            bubble.setCornerRadii(new float[]{
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 5),
                    dp(holder.itemView, 18)
            });

            holder.txtNoiDung.setTextColor(Color.WHITE);

            holder.txtNoiDung.setElevation(dp(holder.itemView, 2));

        } else {

            // Tin nhắn AI
            bubble.setColor(Color.WHITE);

            bubble.setCornerRadii(new float[]{
                    dp(holder.itemView, 5),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18),
                    dp(holder.itemView, 18)
            });

            holder.txtNoiDung.setTextColor(
                    Color.rgb(38, 50, 56));

            holder.txtNoiDung.setElevation(dp(holder.itemView, 2));
        }

        // Viền nhẹ cho bong bóng
        bubble.setStroke(
                dp(holder.itemView, 1),
                message.isFromUser()
                        ? Color.rgb(25, 118, 210)
                        : Color.rgb(235, 239, 245)
        );

        holder.txtNoiDung.setBackground(bubble);
    }


    private String formatChatText(String text) {

        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] lines = text.split("\\r?\\n");
        StringBuilder result = new StringBuilder();

        int count = 1;

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();

            // Kiểm tra dòng tiêu đề bảng Markdown
            if (line.contains("|")
                    && i + 1 < lines.length
                    && isSeparatorLine(lines[i + 1])) {

                // Lấy tiêu đề và loại bỏ ô trống
                ArrayList<String> headers = getTableCells(line);

                // Bỏ qua dòng phân cách
                i += 2;

                count = 1;

                while (i < lines.length
                        && lines[i].contains("|")
                        && !isSeparatorLine(lines[i])) {

                    ArrayList<String> cells =
                            getTableCells(lines[i]);

                    if (!cells.isEmpty()) {

                        // Tên phụ tùng
                        result.append(count)
                                .append(". ")
                                .append(cells.get(0))
                                .append("\n");

                        // Các thông tin còn lại
                        for (int j = 1; j < cells.size(); j++) {

                            String label = j < headers.size()
                                    ? headers.get(j)
                                    : "Thông tin";

                            String value = cells.get(j);

                            label = label.replace("**", "")
                                    .trim()
                                    .toLowerCase();

                            if (label.contains("giá")) {

                                result.append("   💰 Giá: ")
                                        .append(value)
                                        .append("\n");

                            } else if (label.contains("số lượng")) {

                                result.append("   📦 Số lượng: ")
                                        .append(value)
                                        .append("\n");

                            } else if (label.contains("còn hàng")
                                    || label.contains("trạng thái")) {

                                String trangThai = value;

                                if (value.equalsIgnoreCase("Có")
                                        || value.equalsIgnoreCase("Còn")) {
                                    trangThai = "Còn hàng";
                                } else if (value.equalsIgnoreCase("Không")
                                        || value.equalsIgnoreCase("Hết")) {
                                    trangThai = "Hết hàng";
                                }

                                result.append(
                                        trangThai.equals("Hết hàng")
                                                ? "   🔴 Trạng thái: "
                                                : "   🟢 Trạng thái: "
                                ).append(trangThai).append("\n");

                            } else {

                                result.append("   • ")
                                        .append(headers.get(j))
                                        .append(": ")
                                        .append(value)
                                        .append("\n");
                            }
                        }

                        result.append("\n");
                        count++;
                    }

                    i++;
                }

                i--;

            } else {

                // Nội dung văn bản thông thường
                line = line.replace("**", "")
                        .replace("###", "")
                        .replace("##", "");

                result.append(line).append("\n");
            }
        }

        return result.toString().trim();
    }


    // Tách các cột và loại bỏ ô trống
    private ArrayList<String> getTableCells(String line) {

        ArrayList<String> cells = new ArrayList<>();

        String[] parts = line.split("\\|", -1);

        for (String part : parts) {

            String value = part.trim();

            if (!value.isEmpty()) {
                cells.add(value);
            }
        }

        return cells;
    }


    // Kiểm tra dòng phân cách của bảng Markdown
    private boolean isSeparatorLine(String line) {

        String clean = line.replace("|", "")
                .replace("-", "")
                .replace(":", "")
                .replace(" ", "");

        return line.contains("|")
                && clean.isEmpty()
                && line.contains("-");
    }


    @Override
    public int getItemCount() {
        return danhSachTinNhan.size();
    }

    private int dp(View view, int value) {
        return (int) (value *
                view.getResources().getDisplayMetrics().density);
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
