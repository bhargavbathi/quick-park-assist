package com.quick_park_assist.serviceImpl;

import com.quick_park_assist.repository.BookingSpotRepository;
import com.quick_park_assist.repository.ParkingSpotRepository;
import com.quick_park_assist.repository.ReservationRepository;
import com.quick_park_assist.repository.VehicleRepository;
import com.quick_park_assist.service.IStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class StatsServiceImpl implements IStatsService {

    private final BookingSpotRepository bookingSpotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public StatsServiceImpl(BookingSpotRepository bookingSpotRepository, VehicleRepository vehicleRepository, ParkingSpotRepository parkingSpotRepository, ReservationRepository reservationRepository) {
        this.bookingSpotRepository = bookingSpotRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSpotRepository = parkingSpotRepository;}

    @Override

    public Map<String, Object> getStatsForUser(Long userId) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("availableSpots", bookingSpotRepository.countByUserId(userId));
            stats.put("totalHours", bookingSpotRepository.sumDurationByUserId(userId));
            stats.put("amountSpent", bookingSpotRepository.sumEstimatedPriceByUserId(userId));
            stats.put("activeBookings", vehicleRepository.countByUserId(userId)); // Added Vehicle Count
            return stats;
    }
    @Override
    public Map<String, Object> getSpotOwnerStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSpots", parkingSpotRepository.countTotalSpotsByOwner(userId));
        stats.put("evSpots", parkingSpotRepository.countEvSpotsByOwner(userId));
        stats.put("currentBookings", bookingSpotRepository.countActiveBookingsByOwner(userId));
        stats.put("totalRevenue", bookingSpotRepository.calculateTotalRevenueByOwner(userId));
        return stats;
    }


    @Override
    public List<Map<String, Object>> getRecentActivityForUser(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookingSpotRepository.findRecentActivityByUserId(userId, pageable).getContent();

    }
    @Override
    public List<Map<String, Object>> getRecentActivityForOwner(Long userId, int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return parkingSpotRepository.getRecentActivityByUserId(userId,pageable).getContent();
    }

}
