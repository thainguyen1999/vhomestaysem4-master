package com.example.vhomestay.service.impl;

import com.example.vhomestay.model.entity.DormSlot;
import com.example.vhomestay.repository.DormSlotRepository;
import com.example.vhomestay.service.DormSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DormSlotServiceImpl extends BaseServiceImpl<DormSlot, Long, DormSlotRepository>
        implements DormSlotService {
    private final DormSlotRepository repository;

    @Override
    public Optional<DormSlot> findDormSlotById(Long id) {
        return repository.findById(id);
    }

}
