package com.example.books;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class StatementFragment extends Fragment {

    private static final int REQUEST_PERMISSION = 1;
    private File pdfFile;
    private ImageView pdfImageView;
    private Button downloadButton;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statement, container, false);
    }

     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdfImageView = view.findViewById(R.id.pdfImageView);
        downloadButton = view.findViewById(R.id.downloadPdfButton);

        // Request necessary permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

        // Fetch data from server and generate PDF
        fetchData();

        // Download button functionality
        downloadButton.setOnClickListener(v -> {
            if (pdfFile != null && pdfFile.exists()) {
                sharePDF(pdfFile);
            } else {
                Toast.makeText(getContext(), "PDF not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://your-server-address/data"; // Replace with your server URL

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("OkHttp", "Request Failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> generatePDF(responseData));
                    }
                }
            }
        });
    }

    private void generatePDF(String jsonData) {
        // Parse JSON data using Gson
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
        List<Map<String, String>> dataList = gson.fromJson(jsonData, listType);

        // Calculate total fee
        double totalFee = 0.0;

        // Create PDF Document
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);

        // Table Headers
        canvas.drawText("Name", 10, 30, paint);
        canvas.drawText("Date", 110, 30, paint);
        canvas.drawText("Fee", 210, 30, paint);

        int yPos = 60; // Starting Y position for rows

        for (Map<String, String> data : dataList) {
            String name = data.get("name");
            String date = data.get("date");
            String feeStr = data.get("fee");

            // Parse fee as double
            double fee = Double.parseDouble(feeStr);
            totalFee += fee; // Add to total fee

            // Add rows
            canvas.drawText(name, 10, yPos, paint);
            canvas.drawText(date, 110, yPos, paint);
            canvas.drawText(String.format("%.2f", fee), 210, yPos, paint);

            yPos += 30;
        }

        // Add total fee row
        yPos += 20; // Add some spacing before total row
        canvas.drawText("Total Fee", 10, yPos, paint);
        canvas.drawText(String.format("%.2f", totalFee), 210, yPos, paint);

        pdfDocument.finishPage(page);

        // Save the PDF to external storage
        pdfFile = new File(Environment.getExternalStorageDirectory(), "EmployeeDetails.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(getContext(), "PDF saved to: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Display PDF
            displayPDF(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    private void displayPDF(File file) {
        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

            PdfRenderer.Page page = pdfRenderer.openPage(0);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Display bitmap in ImageView
            pdfImageView.setImageBitmap(bitmap);

            page.close();
            pdfRenderer.close();
            fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sharePDF(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share PDF"));
    }
}
