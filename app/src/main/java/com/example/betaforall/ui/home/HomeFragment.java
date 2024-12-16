package com.example.betaforall.ui.home;

import static com.mapbox.mapboxsdk.WellKnownTileServer.Mapbox;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.betaforall.R;
import com.example.betaforall.api.NominatimApi;
import com.example.betaforall.api.NominatimResponse;
import com.example.betaforall.api.RetrofitClient;
import com.example.betaforall.api.MapLibreGeocoderResponse;
import com.example.betaforall.databinding.FragmentHomeBinding;
import com.example.betaforall.model.DeliveryLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.geojson.Point;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AutoCompleteTextView etAddressIn;
    private AutoCompleteTextView etAddressFor;
    private DatabaseReference databaseReference;
    private static final String TAG = "HomeFragment";

    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private static final int DELAY = 1000; // Задержка для запроса (в миллисекундах)
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private final String MAPLIBRE_ACCESS_TOKEN = "MsxXjyIRFqrkKjR8QxsF";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        etAddressIn = binding.etAddressIn;
        etAddressFor = binding.etAddressFor;

        databaseReference = FirebaseDatabase.getInstance().getReference("deliveryLocations");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        Button btnCreateOrder = binding.getRoot().findViewById(R.id.btn_create_order);
        btnCreateOrder.setOnClickListener(v -> createOrder());

        setupAutoComplete(etAddressIn);
        setupAutoComplete(etAddressFor);

        return root;
    }

    private void setupAutoComplete(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    runnable = () -> fetchSuggestions(s.toString(), autoCompleteTextView);
                    handler.postDelayed(runnable, DELAY);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void fetchSuggestions(String query, AutoCompleteTextView autoCompleteTextView) {
        Log.d("fetchSuggestions", "Начат запрос предложений для: " + query);

        NominatimApi api = RetrofitClient.getRetrofitInstance().create(NominatimApi.class);
        Log.d("fetchSuggestions", "API клиент успешно создан");

        api.search(query, "json", 1, "ru").enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<NominatimResponse> results = response.body();
                    Log.d("fetchSuggestions", "Ответ успешный, количество результатов: " + results.size());

                    // Создаём список подсказок
                    List<String> suggestions = new ArrayList<>();
                    for (NominatimResponse result : results) {
                        String displayName = result.getDisplayName();
                        suggestions.add(displayName);  // Добавляем адрес в список подсказок
                        Log.d("fetchSuggestions", "Добавлена подсказка: " + displayName);
                    }

                    // Создаём и устанавливаем адаптер для AutoCompleteTextView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(autoCompleteTextView.getContext(),
                            android.R.layout.simple_dropdown_item_1line, suggestions);
                    autoCompleteTextView.setAdapter(adapter);

                    // Применяем фильтрацию
                    autoCompleteTextView.showDropDown();
                } else {
                    Log.e("fetchSuggestions", "Результаты отсутствуют или ответ неуспешен");
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                Log.e("fetchSuggestions", "Ошибка выполнения запроса: " + t.getMessage());
            }
        });

        Log.d("fetchSuggestions", "Запрос завершён (ответ может быть обработан позже)");
    }

    private void createOrder() {
        String addressIn = etAddressIn.getText().toString();
        String addressFor = etAddressFor.getText().toString();

        if (TextUtils.isEmpty(addressIn) || TextUtils.isEmpty(addressFor)) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание уникального идентификатора
        String id = databaseReference.push().getKey();
        if (id != null) {
            // Создание объекта DeliveryLocation с ID
            DeliveryLocation order = new DeliveryLocation(id, addressIn, addressFor, "В ожидании курьера", "0");

            // Сохранение в Firebase
            databaseReference.child(id).setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Заказ создан", Toast.LENGTH_SHORT).show();
                        // Очистка полей после создания заказа
                        etAddressIn.setText("");
                        etAddressFor.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Ошибка при создании заказа", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Ошибка создания ID", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
