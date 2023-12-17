package com.example.vhomestay.service;

import com.example.vhomestay.model.entity.CancellationHistory;

public interface CancellationHistoryService extends BaseService<CancellationHistory, Long>{
    boolean saveCancellationHistory(CancellationHistory cancellationHistory);
    boolean refundBookingByManager(String bookingCode);
}
