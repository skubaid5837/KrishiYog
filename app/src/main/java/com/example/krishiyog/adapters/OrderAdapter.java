package com.example.krishiyog.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.krishiyog.R;
import com.example.krishiyog.databinding.OrderedItemBinding;
import com.example.krishiyog.models.OrderModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    List<OrderModel> orderModelList;
    Context context;

    public OrderAdapter(List<OrderModel> orderModelList, Context context) {
        this.orderModelList = orderModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        OrderedItemBinding binding = OrderedItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        OrderModel orderModel = orderModelList.get(position);
        holder.bind(orderModel);
    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public void updateData(List<OrderModel> filteredList) {
        this.orderModelList = filteredList; // Replace current data with new data
        notifyDataSetChanged();  // Notify RecyclerView to refresh
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private OrderedItemBinding binding;

        public ViewHolder(OrderedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderModel orderModel) {
            String productId = orderModel.getProductId();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("products")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()){
                            String productName = documentSnapshot.getString("productName");
                            int productPrice = Integer.parseInt(documentSnapshot.getString("productPrice"));
                            String productUnit = documentSnapshot.getString("productUnit");
                            String productSize = documentSnapshot.getString("productSize");
                            int productQuantity = Integer.parseInt(orderModel.getProductQuantity());

                            // Retrieve the first image URL from imageUrls list
                            List<String> imageUrls = (List<String>) documentSnapshot.get("imageUrls");
                            String imageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

                            Glide.with(itemView.getContext())
                                    .load(imageUrl)
                                    .into(binding.productImage);

                            String totalPrice = String.valueOf(productPrice * productQuantity);
                            binding.productName.setText(productName);
                            binding.productPrice.setText("₹" +totalPrice);
                            binding.productUnit.setText(productUnit+")");
                            binding.productSize.setText("("+productSize);

                        }
                    }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());


            if (orderModel.getStatus().equalsIgnoreCase("delivered")){
                binding.status.setBackgroundResource(R.drawable.btn_delivered);
                binding.status.setText("Completed");
                binding.cancelBtn.setVisibility(ViewGroup.GONE);
            } else if (orderModel.getStatus().equalsIgnoreCase("ongoing")) {
                binding.status.setBackgroundResource(R.drawable.btn_ongoing);
                binding.status.setText("Pending");
                binding.cancelBtn.setVisibility(ViewGroup.VISIBLE);
            } else if (orderModel.getStatus().equalsIgnoreCase("cancelled")) {
                binding.status.setBackgroundResource(R.drawable.btn_canceled);
                binding.status.setText("Cancelled");
                binding.cancelBtn.setVisibility(ViewGroup.GONE);
            }

            binding.cancelBtn.setOnClickListener(view -> {
                db.collection("orders")
                        .document(orderModel.getOrderId())
                        .update("status", "cancelled")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Order has been cancelled", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

            binding.quantity.setText("Quantity (" + orderModel.getProductQuantity() + ")");
            binding.orderDate.setText(orderModel.getOrderDate());
        }
    }
}
