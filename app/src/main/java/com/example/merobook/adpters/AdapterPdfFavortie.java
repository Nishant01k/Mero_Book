package com.example.merobook.adpters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merobook.MyApplication;
import com.example.merobook.activities.PdfDetailActivity;
import com.example.merobook.databinding.RowPdfDownloadBinding;
import com.example.merobook.databinding.RowPdfFavoriteBinding;
import com.example.merobook.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterPdfFavortie extends RecyclerView.Adapter<AdapterPdfFavortie.HolderPdfFavortie>{

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;

    private RowPdfFavoriteBinding binding;

    private static String TAG = "FAV_BOOK_TAG";

    public AdapterPdfFavortie(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfFavortie onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfFavoriteBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderPdfFavortie(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfFavortie holder, int position) {

        ModelPdf model = pdfArrayList.get(position);

        loadBookDetails(model, holder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId", model.getId());
                context.startActivity(intent);

            }
        });

        holder.removeFavBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.removeFromFavorite(context, model.getId());

            }
        });
    }

    private void loadBookDetails(ModelPdf model, HolderPdfFavortie holder) {
        String bookId = model.getId();
        Log.d(TAG, "loadBookDetails: Book Details of Book ID "+bookId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String bookTitle = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String bookUrl = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();

                        model.setFavortie(true);
                        model.setTitle(bookTitle);
                        model.setDescription(description);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);

                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(categoryId, holder.categoryTv);
                        MyApplication.loadPdfFromUrlSinglePage(""+bookUrl, ""+bookTitle, holder.pdfView, holder.progressBar, null);
                        MyApplication.loadPdfSize(""+bookUrl, ""+bookTitle, holder.sizeTv);

                        holder.titleTv.setText(bookTitle);
                        holder.descriptionTv.setText(description);
                        holder.dateTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class HolderPdfFavortie extends RecyclerView.ViewHolder{

        PDFView pdfView;
        SpinKitView progressBar;
        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        ImageButton removeFavBTn;

        public HolderPdfFavortie(@NonNull View itemView) {
            super(itemView);

            pdfView =binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            removeFavBTn = binding.removeFavBTn;

        }
    }
}
