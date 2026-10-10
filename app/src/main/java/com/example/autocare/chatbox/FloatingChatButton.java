
package com.example.autocare.chatbox;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FloatingChatButton {

    private FloatingChatButton() {
        // Class dùng chung, không tạo đối tượng trực tiếp
    }

    public static void attach(Activity activity, Runnable onChatClick) {
        FrameLayout root = activity.findViewById(android.R.id.content);

        if (root == null) {
            return;
        }

        // Tránh thêm nút nhiều lần vào cùng một màn hình
        if (root.findViewWithTag("floating_chat_button") != null) {
            return;
        }

        float density = activity.getResources()
                .getDisplayMetrics().density;

        int size = (int) (58 * density);
        int margin = (int) (16 * density);
        int touchSlop = ViewConfiguration.get(activity)
                .getScaledTouchSlop();

        TextView chatButton = new TextView(activity);
        chatButton.setTag("floating_chat_button");
        chatButton.setText("💬");
        chatButton.setTextSize(27);
        chatButton.setGravity(Gravity.CENTER);
        chatButton.setTextColor(Color.WHITE);
        chatButton.setTypeface(null, Typeface.BOLD);
        chatButton.setContentDescription("Mở chatbot");

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(Color.rgb(30, 136, 229));
        background.setStroke(
                (int) (2 * density),
                Color.WHITE
        );

        chatButton.setBackground(background);
        chatButton.setElevation(10 * density);
        chatButton.setClickable(true);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(size, size);

        params.gravity = Gravity.TOP | Gravity.START;

        // Ban đầu đặt nút ở góc dưới bên phải
        root.addView(chatButton, params);

        root.post(() -> {
            params.leftMargin = Math.max(
                    margin,
                    root.getWidth() - size - margin
            );

            params.topMargin = Math.max(
                    margin,
                    root.getHeight() - size - margin
            );

            chatButton.setLayoutParams(params);
        });

        chatButton.setOnClickListener(v -> {
            if (onChatClick != null) {
                onChatClick.run();
            }
        });

        chatButton.setOnTouchListener(new View.OnTouchListener() {
            private float downRawX;
            private float downRawY;
            private int startLeft;
            private int startTop;
            private boolean moved;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();

                        startLeft = params.leftMargin;
                        startTop = params.topMargin;

                        moved = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - downRawX;
                        float dy = event.getRawY() - downRawY;

                        if (Math.abs(dx) > touchSlop
                                || Math.abs(dy) > touchSlop) {
                            moved = true;
                        }

                        if (moved) {
                            int maxLeft = Math.max(
                                    0,
                                    root.getWidth() - view.getWidth()
                            );

                            int maxTop = Math.max(
                                    0,
                                    root.getHeight() - view.getHeight()
                            );

                            params.leftMargin = Math.max(
                                    0,
                                    Math.min(maxLeft, startLeft + (int) dx)
                            );

                            params.topMargin = Math.max(
                                    0,
                                    Math.min(maxTop, startTop + (int) dy)
                            );

                            view.setLayoutParams(params);
                        }

                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!moved) {
                            view.performClick();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        return true;
                }

                return false;
            }
        });
    }
}
