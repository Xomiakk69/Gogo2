package com.example.betaforall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betaforall.R;
import com.example.betaforall.model.DeliveryLocation;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<DeliveryLocation> orders;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnOrderClickListener {
        void onOrderClick(int position);
    }

    private final OnOrderClickListener clickListener;

    public OrderAdapter(List<DeliveryLocation> orders, OnOrderClickListener clickListener) {
        this.orders = orders;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Используем кастомный макет
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        DeliveryLocation order = orders.get(position);
        holder.itemView.setSelected(selectedPosition == position);
        holder.bind(order);
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            clickListener.onOrderClick(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public DeliveryLocation getSelectedOrder() {
        return selectedPosition != RecyclerView.NO_POSITION ? orders.get(selectedPosition) : null;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView addressIn;
        private final TextView addressFor;
        private final TextView status;
        private final TextView deliveryDuration;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            addressIn = itemView.findViewById(R.id.address_in);
            addressFor = itemView.findViewById(R.id.address_for);
            status = itemView.findViewById(R.id.status);
            deliveryDuration = itemView.findViewById(R.id.delivery_duration);
        }

        public void bind(DeliveryLocation order) {
            addressIn.setText("Адрес входа: " + (order.getAddressin() != null ? order.getAddressin() : "Не указан"));
            addressFor.setText("Адрес назначения: " + (order.getAddressfor() != null ? order.getAddressfor() : "Не указан"));
            status.setText("Статус: " + (order.getStatusdost() != null ? order.getStatusdost() : "Не указан"));
            // Если время доставки 0 или null, оставляем текст пустым
            if (order.getDeliveryDuration() != null && !order.getDeliveryDuration().equals("0")) {
                deliveryDuration.setText("Время доставки: " + order.getDeliveryDuration());
            } else {
                deliveryDuration.setText("Время доставки: ");
            }
        }
    }
}
