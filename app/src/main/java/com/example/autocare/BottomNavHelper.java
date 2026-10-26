
package com.example.autocare;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class BottomNavHelper {

    public static void setup(Activity activity) {

        ganSuKien(activity, R.id.btnNavTrangChu, TrangChu.class);
        ganSuKien(activity, R.id.btnNavLich, DatLich.class);
        ganSuKien(activity, R.id.btnNavXeCuaToi, XeCuaToi.class);
        ganSuKien(activity, R.id.btnNavDaiLy, DaiLyActivity.class);
        ganSuKien(activity, R.id.btnNavTaiKhoan, TaiKhoanActivity.class);

    }

    private static void ganSuKien(
            Activity activity,
            int viewId,
            Class<? extends Activity> manHinh
    ) {

        View button = activity.findViewById(viewId);

        if (button == null) {
            return;
        }

        boolean dangMo = manHinh.isInstance(activity);

        button.setSelected(dangMo);
        button.setAlpha(dangMo ? 1.0f : 0.75f);

        button.setOnClickListener(v -> {

            if (manHinh.isInstance(activity)) {
                return;
            }

            Intent intent = new Intent(activity, manHinh);

            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            activity.startActivity(intent);

        });
    }
}
