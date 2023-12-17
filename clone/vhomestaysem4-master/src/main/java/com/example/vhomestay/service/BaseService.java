package com.example.vhomestay.service;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
}
