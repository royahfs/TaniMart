package com.example.tanimart.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static String saveImageToInternalStorage(Context ctx, Uri sourceUri, String userId) {
        try {
            File dir = new File(ctx.getFilesDir(), "profile_photos");
            if (!dir.exists()) dir.mkdirs();
            File outFile = new File(dir, userId + ".jpg");

            try (InputStream in = ctx.getContentResolver().openInputStream(sourceUri);
                 OutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }

            return outFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
