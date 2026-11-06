package com.example.autocare.chatbox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatbotActivity extends AppCompatActivity {
    private static final String TAG = "AutoCareAI";
    private static final String WORKER_URL =
            "https://autocare-ai.lvvinh-dhmt17a1hn.workers.dev/";

    private EditText edtTinNhan;
    private ImageButton btnGui;
    private TextView btnXoaLichSu;
    private RecyclerView recyclerChat;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ChatMessageAdapter adapter;
    private final ArrayList<ChatMessage> danhSachTinNhan = new ArrayList<>();
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private ListenerRegistration chatListener;
    private String uid;
    private boolean isAdmin = false;
    private boolean dangGui = false;
    private boolean dangXoa = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng chatbot", Toast.LENGTH_SHORT).show();
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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(manager);
        recyclerChat.setAdapter(adapter);
    }

    private void ganSuKien() {
        btnGui.setOnClickListener(v -> guiTinNhan());
        btnXoaLichSu.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Xóa lịch sử trò chuyện")
                .setMessage("Bạn có chắc muốn xóa toàn bộ lịch sử chat của tài khoản này?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> xoaLichSuChat())
                .show());
    }

    private void xacDinhVaiTroVaTaiChat() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String role = doc.getString("role");
                    if (role == null) role = doc.getString("vaiTro");
                    if (role == null) role = doc.getString("loaiTaiKhoan");
                    isAdmin = laAdmin(role);
                    taiLichSuChat();
                })
                .addOnFailureListener(e -> {
                    isAdmin = false;
                    taiLichSuChat();
                });
    }

    private boolean laAdmin(String role) {
        if (role == null) return false;
        String value = role.trim().toLowerCase(Locale.ROOT);
        return value.equals("admin") || value.equals("quantri")
                || value.equals("quản trị viên") || value.equals("administrator");
    }

    private CollectionReference getMessagesRef() {
        return db.collection("chats").document(uid).collection("messages");
    }

    private void taiLichSuChat() {
        if (chatListener != null) chatListener.remove();
        chatListener = getMessagesRef()
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limitToLast(200)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Lỗi tải lịch sử", error);
                        return;
                    }
                    if (snapshot == null) return;
                    danhSachTinNhan.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String text = doc.getString("text");
                        Boolean fromUser = doc.getBoolean("fromUser");
                        Long timestamp = doc.getLong("timestamp");
                        if (text != null) {
                            danhSachTinNhan.add(new ChatMessage(
                                    text, Boolean.TRUE.equals(fromUser),
                                    timestamp == null ? 0L : timestamp));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (!danhSachTinNhan.isEmpty()) {
                        recyclerChat.scrollToPosition(danhSachTinNhan.size() - 1);
                    }
                });
    }

    private void guiTinNhan() {
        if (dangGui || dangXoa) return;
        String text = edtTinNhan.getText().toString().trim();
        if (text.isEmpty()) {
            edtTinNhan.setError("Hãy nhập nội dung");
            return;
        }
        if (text.length() > 1000) {
            edtTinNhan.setError("Tin nhắn tối đa 1000 ký tự");
            return;
        }
        dangGui = true;
        btnGui.setEnabled(false);
        edtTinNhan.setText("");
        anBanPhim();
        luuTinNhan(text, true,
                () -> hoiGroqAI(text),
                () -> {
                    edtTinNhan.setText(text);
                    ketThucGui();
                });
    }

    private void hoiGroqAI(String cauHoi) {
        if (auth.getCurrentUser() == null) {
            thongBao("Phiên đăng nhập đã hết. Vui lòng đăng nhập lại.");
            ketThucGui();
            return;
        }
        auth.getCurrentUser().getIdToken(false)
                .addOnSuccessListener(tokenResult -> {
                    String idToken = tokenResult.getToken();
                    if (idToken == null || idToken.isEmpty()) {
                        thongBao("Không lấy được mã xác thực Firebase.");
                        ketThucGui();
                        return;
                    }
                    networkExecutor.execute(() -> {
                        try {
                            String answer = goiWorker(cauHoi, idToken);
                            runOnUiThread(() -> {
                                if (isFinishing() || isDestroyed()) return;
                                luuTinNhan(answer, false,
                                        this::ketThucGui,
                                        this::ketThucGui);
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi gọi Cloudflare/Groq", e);
                            runOnUiThread(() -> {
                                if (isFinishing() || isDestroyed()) return;
                                thongBao("AI chưa trả lời: " + thongDiepLoi(e));
                                ketThucGui();
                            });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi lấy Firebase ID token", e);
                    thongBao("Không xác thực được tài khoản.");
                    ketThucGui();
                });
    }

    private String goiWorker(String cauHoi, String idToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(WORKER_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(45000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + idToken);
            JSONObject body = new JSONObject();
            body.put("message", cauHoi);
            byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
            try (java.io.OutputStream output = connection.getOutputStream()) {
                output.write(payload);
            }
            int status = connection.getResponseCode();
            InputStream stream = status >= 200 && status < 300
                    ? connection.getInputStream() : connection.getErrorStream();
            String responseBody = docNoiDung(stream);
            if (status < 200 || status >= 300) {
                Log.e(TAG, "Worker HTTP " + status + ": " + responseBody);
                if (status == 401 || status == 403) {
                    throw new Exception("Không có quyền truy cập AI (HTTP " + status + ")");
                }
                if (status == 429) throw new Exception("Đã vượt giới hạn lượt hỏi, thử lại sau");
                throw new Exception("Máy chủ AI báo lỗi HTTP " + status);
            }
            String answer = new JSONObject(responseBody).optString("answer", "").trim();
            if (answer.isEmpty()) throw new Exception("AI trả về câu trả lời trống");
            return answer;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private String docNoiDung(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
        }
        return result.toString();
    }

    private String thongDiepLoi(Exception e) {
        String message = e.getMessage();
        return message == null || message.trim().isEmpty()
                ? "Kiểm tra mạng và Logcat" : message;
    }

    private void luuTinNhan(String text, boolean fromUser,
                            Runnable onSuccess, Runnable onFailure) {
        Map<String, Object> message = new HashMap<>();
        message.put("text", text);
        message.put("fromUser", fromUser);
        message.put("timestamp", System.currentTimeMillis());
        message.put("senderRole", fromUser ? (isAdmin ? "admin" : "customer") : "bot");
        getMessagesRef().add(message)
                .addOnSuccessListener(ref -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi lưu tin nhắn", e);
                    thongBao("Không lưu được tin nhắn. Kiểm tra quyền Firestore.");
                    if (onFailure != null) onFailure.run();
                });
    }

    private void ketThucGui() {
        dangGui = false;
        btnGui.setEnabled(!dangXoa);
    }

    private void xoaLichSuChat() {
        if (dangGui || dangXoa) {
            thongBao("Vui lòng đợi AI trả lời xong.");
            return;
        }
        dangXoa = true;
        btnGui.setEnabled(false);
        btnXoaLichSu.setEnabled(false);
        getMessagesRef().get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        thongBao("Lịch sử chat đang trống");
                        ketThucXoa();
                        return;
                    }
                    xoaTheoNhom(new ArrayList<>(snapshot.getDocuments()), 0);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải lịch sử để xóa", e);
                    thongBao("Không tải được lịch sử chat");
                    ketThucXoa();
                });
    }

    private void xoaTheoNhom(ArrayList<DocumentSnapshot> docs, int start) {
        if (start >= docs.size()) {
            thongBao("Đã xóa lịch sử chat");
            ketThucXoa();
            return;
        }
        WriteBatch batch = db.batch();
        int end = Math.min(start + 400, docs.size());
        for (int i = start; i < end; i++) batch.delete(docs.get(i).getReference());
        batch.commit()
                .addOnSuccessListener(unused -> xoaTheoNhom(docs, end))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi xóa lịch sử", e);
                    thongBao("Xóa chưa hoàn tất. Vui lòng thử lại.");
                    ketThucXoa();
                });
    }

    private void ketThucXoa() {
        dangXoa = false;
        btnGui.setEnabled(!dangGui);
        btnXoaLichSu.setEnabled(true);
    }

    private void thongBao(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void anBanPhim() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(edtTinNhan.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        if (chatListener != null) {
            chatListener.remove();
            chatListener = null;
        }
        networkExecutor.shutdownNow();
        super.onDestroy();
    }
}
