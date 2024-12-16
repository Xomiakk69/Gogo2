package com.example.betaforall.ui.dashboard;

import static com.example.betaforall.api.DeliveryCalculator.calculateDeliveryDuration;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.betaforall.api.DeliveryCalculator;
import com.example.betaforall.databinding.FragmentDashboardBinding;
import com.example.betaforall.model.DeliveryLocation;
import com.example.betaforall.adapter.OrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private OrderAdapter adapter;
    private List<DeliveryLocation> orders;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Инициализация списка заказов
        orders = new ArrayList<>();
        adapter = new OrderAdapter(orders, position -> {
            DeliveryLocation selectedOrder = adapter.getSelectedOrder();
            if (selectedOrder != null) {
                Toast.makeText(requireContext(), "Выбран заказ: " + selectedOrder.getAddressin(), Toast.LENGTH_SHORT).show();
            }
        });

        // Настройка RecyclerView
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrders.setAdapter(adapter);

        // Загрузка данных из Firebase
        loadOrdersFromFirebase();

        // Обработка кнопки
        binding.btnTakeOrder.setOnClickListener(v -> takeOrder());

        return root;
    }

    private void loadOrdersFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("deliveryLocations");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    DeliveryLocation order = data.getValue(DeliveryLocation.class);
                    if (order != null) {
                        orders.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void takeOrder() {
        DeliveryLocation selectedOrder = adapter.getSelectedOrder();
        if (selectedOrder != null) {
            String addressIn = selectedOrder.getAddressin();
            String addressFor = selectedOrder.getAddressfor();

            if (addressIn != null && addressFor != null) {
                // Коэффициент для расчёта (грузовая доставка или обычная)
                double coefficient = addressFor.contains("грузовая") ? 1.25 : 1.0;

                // Расчёт времени доставки
                getCoordinatesAndCalculateTime(addressIn, addressFor, coefficient, selectedOrder);
            } else {
                Toast.makeText(requireContext(), "Адреса некорректны", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Выберите заказ", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод конвертации времени
    // Метод конвертации времени
    private String convertTime(long minutes) {
        if (minutes < 60) {
            return minutes + " минут";
        } else if (minutes < 1440) {  // меньше 24 часов
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " час" + (hours > 1 ? "а" : "") + (remainingMinutes > 0 ? " и " + remainingMinutes + " минут" : "");
        } else {
            long days = minutes / 1440;  // количество дней
            long remainingHours = (minutes % 1440) / 60;  // оставшиеся часы
            return days + " день" + (days > 1 ? "я" : "") + (remainingHours > 0 ? " и " + remainingHours + " час" + (remainingHours > 1 ? "а" : "") : "");
        }
    }

    private void getCoordinatesAndCalculateTime(String addressIn, String addressFor, double coefficient, DeliveryLocation selectedOrder) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            // Геокодирование начального адреса
            List<Address> addressInResults = geocoder.getFromLocationName(addressIn, 1);
            List<Address> addressForResults = geocoder.getFromLocationName(addressFor, 1);

            if (!addressInResults.isEmpty() && !addressForResults.isEmpty()) {
                Address start = addressInResults.get(0);
                Address end = addressForResults.get(0);

                double startLat = start.getLatitude();
                double startLon = start.getLongitude();
                double endLat = end.getLatitude();
                double endLon = end.getLongitude();

                // Расчёт времени доставки с коэффициентом
                String deliveryTime = calculateDeliveryDuration(startLat, startLon, endLat, endLon, coefficient);

                try {
                    // Очищаем строку от текста и оставляем только числа
                    String timeInMinutesStr = deliveryTime.replaceAll("[^0-9]", "");  // Удаляем всё, что не число
                    long deliveryTimeInMinutes = Long.parseLong(timeInMinutesStr);  // Преобразуем строку в число минут

                    // Преобразуем время
                    String formattedDeliveryTime = convertTime(deliveryTimeInMinutes);

                    // Обновляем заказ
                    selectedOrder.setStatusdost("выполняется");
                    selectedOrder.setDeliveryDuration(formattedDeliveryTime);

                    // Сохранение изменений в Firebase
                    updateOrderInDatabase(selectedOrder);

                    Toast.makeText(requireContext(), "Примерное время доставки: " + formattedDeliveryTime, Toast.LENGTH_LONG).show();
                } catch (NumberFormatException e) {
                    // Если преобразование не удалось, выводим сообщение об ошибке
                    Toast.makeText(requireContext(), "Ошибка при расчёте времени доставки", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(requireContext(), "Не удалось найти координаты адресов", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка получения координат", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateOrderInDatabase(DeliveryLocation order) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("deliveryLocations");
        if (order.getId() != null) {
            databaseRef.child(order.getId()).setValue(order)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Статус заказа обновлен", Toast.LENGTH_SHORT).show();
                            Log.d("FirebaseUpdate", "Заказ обновлен: " + order.getId());
                        } else {
                            Log.e("FirebaseUpdate", "Ошибка обновления: " + task.getException());
                            Toast.makeText(requireContext(), "Ошибка обновления статуса", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("FirebaseUpdate", "Ключ заказа отсутствует");
            Toast.makeText(requireContext(), "Не удалось найти ключ заказа", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
