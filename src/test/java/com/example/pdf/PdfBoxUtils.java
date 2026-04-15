package com.example.pdf;

import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfBoxUtils {

    /**
     * 把多个 PDF byte[] 合并成一个 PDF
     */
    public static byte[] merge(List<byte[]> pdfList) throws IOException {
        if (pdfList == null || pdfList.isEmpty()) {
            throw new IllegalArgumentException("PDF 列表不能为空");
        }

        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream mergedOut = new ByteArrayOutputStream();

        for (byte[] pdfBytes : pdfList) {
            if (pdfBytes != null && pdfBytes.length > 0) {
                merger.addSource(new RandomAccessReadBuffer(pdfBytes));
            }
        }

        merger.setDestinationStream(mergedOut);
        // 内存合并
        merger.mergeDocuments(null);

        return mergedOut.toByteArray();
    }
}