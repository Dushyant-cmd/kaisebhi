package com.kaisebhi.kaisebhi.Utility;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
