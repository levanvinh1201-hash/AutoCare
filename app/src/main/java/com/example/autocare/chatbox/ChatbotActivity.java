package com.example.autocare.chatbox;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatbotActivity extends AppCompatActivity {

    private EditText edtTinNhan;
    private ImageButton btnGui;
    private TextView btnXoaLichSu;
    private RecyclerView recyclerChat;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private ChatMessageAdapter adapter;
    private final ArrayList<ChatMessage> danhSachTinNhan =
            new ArrayList<>();

    private ListenerRegistration chatListener;

    private String uid;
    private boolean isAdmin = false;
    private boolean dangGui = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Vui lòng đăng nhập để sử dụng chatbot",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uid = auth.getCurrentUser().getUid();

        anhXaView();
        khoiTaoDanhSach();
        ganSuKien();
        xacDinhVaiTroVaTaiChat();
    }

    private void anhXaView() {
        edtTinNhan = findViewById(R.id.edtTinNhanChat);
        btnGui = findViewById(R.id.btnGui);
        btnXoaLichSu = findViewById(R.id.btnXoaLichSuChat);
        recyclerChat = findViewById(R.id.recyclerChat);
    }

    private void khoiTaoDanhSach() {
        adapter = new ChatMessageAdapter(danhSachTinNhan);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);

        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(adapter);
    }

    private void ganSuKien() {
        btnGui.setOnClickListener(v -> guiTinNhan());

        btnXoaLichSu.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Xóa lịch sử trò chuyện")
                        .setMessage(
                                "Bạn có chắc muốn xóa toàn bộ "
                                        + "lịch sử chat của tài khoản này?"
                        )
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Xóa",
                                (dialog, which) -> xoaLichSuChat())
                        .show()
        );
    }

    private void xacDinhVaiTroVaTaiChat() {
        // Xác định vai trò từ hồ sơ Firestore, không tin vào
        // giá trị role được truyền từ Intent.
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String role = doc.getString("role");

                    if (role == null) {
                        role = doc.getString("vaiTro");
                    }

                    if (role == null) {
                        role = doc.getString("loaiTaiKhoan");
                    }

                    isAdmin = laAdmin(role);
                    taiLichSuChat();
                })
                .addOnFailureListener(e -> {
                    // Nếu không đọc được hồ sơ, mặc định quyền thấp nhất.
                    isAdmin = false;
                    taiLichSuChat();
                });
    }

    private boolean laAdmin(String role) {
        if (role == null) {
            return false;
        }

        String value = role.trim().toLowerCase();

        return value.equals("admin")
                || value.equals("quantri")
                || value.equals("quản trị viên")
                || value.equals("administrator");
    }

    private com.google.firebase.firestore.CollectionReference
    getMessagesRef() {
        return db.collection("chats")
                .document(uid)
                .collection("messages");
    }

    private void taiLichSuChat() {
        chatListener = getMessagesRef()
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(200)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this,
                                "Không thể tải lịch sử chat",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    danhSachTinNhan.clear();

                    for (DocumentSnapshot doc
                            : snapshot.getDocuments()) {

                        String text = doc.getString("text");
                        Boolean fromUser = doc.getBoolean("fromUser");
                        Long timestamp = doc.getLong("timestamp");

                        if (text == null) {
                            continue;
                        }

                        danhSachTinNhan.add(new ChatMessage(
                                text,
                                Boolean.TRUE.equals(fromUser),
                                timestamp == null
                                        ? 0L : timestamp
                        ));
                    }

                    adapter.notifyDataSetChanged();

                    if (!danhSachTinNhan.isEmpty()) {
                        recyclerChat.scrollToPosition(
                                danhSachTinNhan.size() - 1
                        );
                    }
                });
    }

    private void guiTinNhan() {
        if (dangGui) {
            return;
        }

        String text = edtTinNhan.getText().toString().trim();

        if (text.isEmpty()) {
            edtTinNhan.setError("Hãy nhập nội dung");
            return;
        }

        if (text.length() > 1000) {
            edtTinNhan.setError(
                    "Tin nhắn tối đa 1000 ký tự"
            );
            return;
        }

        dangGui = true;
        btnGui.setEnabled(false);

        edtTinNhan.setText("");
        anBanPhim();

        luuTinNhan(text, true, () -> {
            ChatbotEngine engine =
                    new ChatbotEngine(uid, isAdmin);

            engine.answer(text, answer ->
                    luuTinNhan(answer, false, () -> {
                        dangGui = false;
                        btnGui.setEnabled(true);
                    })
            );
        });
    }

    private void luuTinNhan(
            String text,
            boolean fromUser,
            Runnable onComplete) {

        Map<String, Object> message = new HashMap<>();
        message.put("text", text);
        message.put("fromUser", fromUser);
        message.put("timestamp", System.currentTimeMillis());
        message.put("senderRole", fromUser
                ? (isAdmin ? "admin" : "customer")
                : "bot");

        getMessagesRef()
                .add(message)
                .addOnSuccessListener(ref -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Không lưu được tin nhắn. "
                                    + "Kiểm tra quyền Firestore.",
                            Toast.LENGTH_LONG).show();

                    if (fromUser) {
                        dangGui = false;
                        btnGui.setEnabled(true);
                    } else if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void xoaLichSuChat() {
        getMessagesRef()
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        Toast.makeText(this,
                                "Lịch sử chat đang trống",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Firestore giới hạn 500 thao tác mỗi batch.
                    WriteBatch batch = db.batch();
                    int count = 0;

                    for (DocumentSnapshot doc
                            : snapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                        count++;

                        if (count == 450) {
                            // Với lịch sử lớn hơn 450 tin nhắn,
                            // cần chia thành nhiều batch.
                            Toast.makeText(this,
                                    "Lịch sử quá dài; chưa xóa. "
                                            + "Hãy thử lại sau khi rút gọn.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    batch.commit()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this,
                                            "Đã xóa lịch sử chat",
                                            Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Xóa lịch sử thất bại",
                                            Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Không tải được lịch sử để xóa",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void anBanPhim() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    edtTinNhan.getWindowToken(), 0
            );
        }
    }

    @Override
    protected void onDestroy() {
        if (chatListener != null) {
            chatListener.remove();
        }

        super.onDestroy();
    }
}