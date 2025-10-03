package com.example.tanimart.ui.adapter;

import android.content.Context;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfDocumentAdapter extends PrintDocumentAdapter {
    private Context context;
    private String path;

    public PdfDocumentAdapter(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    public void onLayout(android.print.PrintAttributes oldAttributes,
                         android.print.PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         android.os.Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("receipt.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .build();
        callback.onLayoutFinished(pdi, true);
    }

    @Override
    public void onWrite(android.print.PageRange[] pages,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        File file = new File(path);
        try {
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int size;
            while ((size = in.read(buf)) >= 0 && !cancellationSignal.isCanceled()) {
                out.write(buf, 0, size);
            }

            in.close();
            out.close();
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
            } else {
                callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});
            }
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
        }
    }
}
