package com.example.tanimart.printer;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class EscPosPrinterUtil {
    private static final String TAG = "EscPosPrinterUtil";

    // Basic ESC/POS commands
    private static final byte[] INIT = new byte[]{0x1B, 0x40}; // Initialize printer
    private static final byte[] NEW_LINE = new byte[]{0x0A};
    private static final byte[] ALIGN_LEFT = new byte[]{0x1B, 0x61, 0x00};
    private static final byte[] ALIGN_CENTER = new byte[]{0x1B, 0x61, 0x01};
    private static final byte[] ALIGN_RIGHT = new byte[]{0x1B, 0x61, 0x02};
    private static final byte[] BOLD_ON = new byte[]{0x1B, 0x45, 0x01};
    private static final byte[] BOLD_OFF = new byte[]{0x1B, 0x45, 0x00};
    private static final byte[] TXT_DOUBLE_ON = new byte[]{0x1B, 0x21, 0x30}; // double height+width
    private static final byte[] TXT_DOUBLE_OFF = new byte[]{0x1B, 0x21, 0x00};

    // Cut (varies: try GS V 1 or GS V 66)
    private static final byte[] CUT_PAPER_1 = new byte[]{0x1D, 0x56, 0x01}; // partial cut
    private static final byte[] CUT_PAPER_2 = new byte[]{0x1D, 0x56, 0x42, 0x00}; // some printers

    // Helper menggabungkan byte arrays
    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private static byte[] concatMany(byte[]... arrays) {
        int total = 0;
        for (byte[] p : arrays) total += p.length;
        byte[] r = new byte[total];
        int pos = 0;
        for (byte[] p : arrays) {
            System.arraycopy(p, 0, r, pos, p.length);
            pos += p.length;
        }
        return r;
    }

    // Convert string ke bytes (encoding bisa diubah jika perlu)
    private static byte[] textBytes(String s) {
        try {
            // coba "UTF-8", kalau karakter rusak ganti "GBK" atau "CP437"
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encoding error: " + e.getMessage());
            return s.getBytes();
        }
    }

    // Format baris: nama (left) dan harga (right) pada lebar tetap
    private static String formatLine(String left, String right) {
        // asumsi printer width ~ 32-48 chars; kita pakai 32 chars per line (sesuaikan)
        int lineChars = 32;
        if (left == null) left = "";
        if (right == null) right = "";
        int leftLen = left.length();
        int rightLen = right.length();
        int spaceCount = lineChars - leftLen - rightLen;
        if (spaceCount < 0) {
            // potong left kalau perlu
            left = left.substring(0, Math.max(0, lineChars - rightLen - 1)) + " ";
            spaceCount = 1;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(left);
        for (int i = 0; i < spaceCount; i++) sb.append(' ');
        sb.append(right);
        return sb.toString();
    }

    /**
     * printReceipt
     * @param printer BluetoothPrintService sudah connect
     * @param storeName nama toko
     * @param cashier nama kasir
     * @param date waktu string
     * @param invoice no invoice
     * @param payment jenis pembayaran
     * @param items list of Item (name, qtyPrice, price)
     * @param subtotal subtotal
     * @param total total
     * @param pay bayar
     * @param change kembali
     */
    public static void printReceipt(BluetoothPrintService printer,
                                    String storeName,
                                    String cashier,
                                    String date,
                                    String invoice,
                                    String payment,
                                    List<String[]> items,
                                    String subtotal,
                                    String total,
                                    String pay,
                                    String change) {
        try {
            // init
            printer.write(INIT);

            // header
            printer.write(ALIGN_CENTER);
            printer.write(BOLD_ON);
            printer.write(textBytes(storeName + "\n"));
            printer.write(BOLD_OFF);
            printer.write(textBytes("Pusat\n"));
            printer.write(NEW_LINE);

            printer.write(ALIGN_LEFT);
            printer.write(textBytes("Kasir: " + cashier + "\n"));
            printer.write(textBytes("Waktu: " + date + "\n"));
            printer.write(textBytes("No. Struk: " + invoice + "\n"));
            printer.write(textBytes("Jenis Pembayaran: " + payment + "\n"));
            printer.write(NEW_LINE);

            printer.write(ALIGN_CENTER);
            printer.write(BOLD_ON);
            printer.write(textBytes("### LUNAS ###\n"));
            printer.write(BOLD_OFF);
            printer.write(NEW_LINE);

            // items
            printer.write(ALIGN_LEFT);
            for (String[] it : items) {
                // it[0] = name, it[1] = qtyPrice (e.g. "120.000 x 1"), it[2] = price
                String left = it[0] + " " + it[1];
                String right = it[2];
                // buat dua baris: nama+qty di atas, harga di kanan baris bawah
                // disederhanakan: satu baris format left-right
                printer.write(textBytes(formatLine(left, right) + "\n"));
            }
            printer.write(NEW_LINE);

            // totals
            printer.write(textBytes(formatLine("Subtotal", subtotal) + "\n"));
            printer.write(BOLD_ON);
            printer.write(textBytes(formatLine("Total (" + items.size() + " Produk)", total) + "\n"));
            printer.write(BOLD_OFF);
            printer.write(textBytes(formatLine("Bayar", pay) + "\n"));
            printer.write(textBytes(formatLine("Kembali", change) + "\n"));

            printer.write(NEW_LINE);
            printer.write(ALIGN_CENTER);
            printer.write(textBytes("Powered by Qasir\n"));
            printer.write(textBytes("www.qasir.id\n"));
            printer.write(NEW_LINE);
            printer.write(NEW_LINE);

            // final new lines then cut
            printer.write(NEW_LINE);
            printer.write(NEW_LINE);
            // coba perintah cut
            try {
                printer.write(CUT_PAPER_1);
            } catch (Exception e) {
                // fallback
                try { printer.write(CUT_PAPER_2); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            Log.e(TAG, "printReceipt error: " + e.getMessage());
        }
    }
}
