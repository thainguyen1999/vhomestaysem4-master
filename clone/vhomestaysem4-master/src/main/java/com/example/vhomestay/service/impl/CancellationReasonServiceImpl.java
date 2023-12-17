package com.example.vhomestay.service.impl;

import com.example.vhomestay.model.entity.CancellationReason;
import com.example.vhomestay.repository.CancellationReasonRepository;
import com.example.vhomestay.service.CancellationReasonService;
import org.springframework.stereotype.Service;

@Service
public class CancellationReasonServiceImpl
        extends BaseServiceImpl<CancellationReason, Long, CancellationReasonRepository>
        implements CancellationReasonService {

}
