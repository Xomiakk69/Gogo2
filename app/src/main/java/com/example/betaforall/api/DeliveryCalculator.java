package com.example.betaforall.api;

import android.location.Location;

public class DeliveryCalculator {

    // Средняя скорость в км/ч
    private static final double AVERAGE_SPEED = 40.0; // 50 км/ч

    /**
     * Рассчитывает примерное время доставки.
     *
     * @param addressInLat  Широта начального адреса
     * @param addressInLon  Долгота начального адреса
     * @param addressForLat Широта конечного адреса
     * @param addressForLon Долгота конечного адреса
     * @param coefficient   Коэффициент (например, 1.25 для грузового транспорта)
     * @return Примерное время доставки в минутах
     */
    public static String calculateDeliveryDuration(double addressInLat, double addressInLon,
                                                   double addressForLat, double addressForLon,
                                                   double coefficient) {
        // Вычисление расстояния в километрах
        float[] results = new float[1];
        Location.distanceBetween(addressInLat, addressInLon, addressForLat, addressForLon, results);
        double distanceKm = results[0] / 1000.0; // Из метров в километры

        // Вычисление времени в часах
        double timeHours = (distanceKm / AVERAGE_SPEED) * coefficient;

        // Конвертация в минуты
        int timeMinutes = (int) (timeHours * 60);

        if (timeMinutes == 0){
            timeMinutes = 3;
        }

        return timeMinutes + " минут";
    }
}
