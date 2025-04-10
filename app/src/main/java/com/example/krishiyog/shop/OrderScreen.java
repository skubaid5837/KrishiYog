package com.example.krishiyog.shop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CartAdapter;
import com.example.krishiyog.adapters.OrderAdapter;
import com.example.krishiyog.databinding.ActivityOrderScreenBinding;
import com.example.krishiyog.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderScreen extends AppCompatActivity {

    ActivityOrderScreenBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    List<OrderModel> orderModelList;
    OrderAdapter adapter;
    Intent i;
    AppCompatButton lastClickedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        i = getIntent();

        //recyclerView
        itemRecyclerView();

        lastClickedButton = binding.btnOngoing;

        binding.btnDelivered.setOnClickListener(view -> { onButtonClicked(binding.btnDelivered);});
        binding.btnCancelled.setOnClickListener(view -> { onButtonClicked(binding.btnCancelled);});
        binding.btnOngoing.setOnClickListener(view -> { onButtonClicked(binding.btnOngoing);});
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void itemRecyclerView() {
        binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.ordersRecyclerView.hasFixedSize();
        orderModelList = new ArrayList<>();
        adapter = new OrderAdapter(orderModelList, this);
        binding.ordersRecyclerView.setAdapter(adapter);

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {

                        orderModelList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            OrderModel order = document.toObject(OrderModel.class);
                            if (order != null && order.getUserId().equals(userId)) {
                            // Add order to the list if it belongs to the current user
                            orderModelList.add(order);
                        }
                        }

                        // Update the RecyclerView data
                        adapter.updateData(orderModelList);
                        // Trigger the "Ongoing" button click programmatically
                        onButtonClicked(lastClickedButton);
                    }
                });
    }

    private void onButtonClicked(AppCompatButton clickedButton) {
        // Reset the background of the previously clicked button
        resetButtonBackground(lastClickedButton);

        // Update the background of the currently clicked button
        updateButtonBackground(clickedButton);

        // Update the last clicked button
        lastClickedButton = clickedButton;
    }

    private void updateButtonBackground(AppCompatButton button) {

        if (button == binding.btnOngoing) {
            button.setBackgroundResource(R.drawable.btn_ongoing_selected);
            filterOrders("Ongoing");
        } else if (button == binding.btnDelivered) {
            button.setBackgroundResource(R.drawable.btn_delivered_selected);
            filterOrders("Delivered");
        } else if (button == binding.btnCancelled) {
            button.setBackgroundResource(R.drawable.btn_canceled_selected);
            filterOrders("Cancelled");
        }

        button.setTextColor(Color.WHITE);

    }

    private void filterOrders(String status) {
        List<OrderModel> filteredList = new ArrayList<>();
        for (OrderModel order : orderModelList) {
            if (order.getStatus().equalsIgnoreCase(status)) {
                filteredList.add(order);
            }
        }

        // Update the adapter with filtered data
        adapter.updateData(filteredList);

    }

    private void resetButtonBackground(AppCompatButton button) {

        if (button == binding.btnOngoing){
            button.setBackgroundResource(R.drawable.btn_ongoing);
        } else if (button == binding.btnCancelled) {
            button.setBackgroundResource(R.drawable.btn_canceled);
        }else {
            button.setBackgroundResource(R.drawable.btn_delivered);
        }

        button.setTextColor(Color.BLACK);

    }

}