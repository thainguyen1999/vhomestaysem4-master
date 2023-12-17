package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.RefundStatus;
import com.example.vhomestay.model.entity.CancellationHistory;
import com.example.vhomestay.repository.CancellationHistoryRepository;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.CancellationHistoryService;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CancellationHistoryServiceImpl
        extends BaseServiceImpl<CancellationHistory, Long, CancellationHistoryRepository>
        implements CancellationHistoryService {
    private final CancellationHistoryRepository cancellationHistoryRepository;

    @Override
    public boolean saveCancellationHistory(CancellationHistory cancellationHistory) {
        try {
            cancellationHistoryRepository.save(cancellationHistory);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }

    @Override
    public boolean refundBookingByManager(String bookingCode) {
            String managerEmail = SecurityUtil.getCurrentUserLogin().get();

            Optional<CancellationHistory> cancellationHistoryOptional = cancellationHistoryRepository.findCancellationHistoryByBookingCodeAndManagerEmail(bookingCode, managerEmail);

            if(!cancellationHistoryOptional.isPresent()){
                throw new ResourceNotFoundException("booking.not.found");
            }

            CancellationHistory cancellationHistory = cancellationHistoryOptional.get();
            cancellationHistory.setRefundStatus(RefundStatus.REFUNDED);

        try {
            cancellationHistoryRepository.save(cancellationHistory);

            return true;
        }catch (Exception e){
            throw new ResourceInternalServerErrorException("internal.server.error");
        }
    }
}
