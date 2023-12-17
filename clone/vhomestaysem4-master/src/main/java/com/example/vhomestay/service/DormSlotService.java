package com.example.vhomestay.service;

import com.example.vhomestay.model.entity.DormSlot;

import java.util.Optional;

public interface DormSlotService extends BaseService<DormSlot, Long>{
    Optional<DormSlot> findDormSlotById(Long id);
}
