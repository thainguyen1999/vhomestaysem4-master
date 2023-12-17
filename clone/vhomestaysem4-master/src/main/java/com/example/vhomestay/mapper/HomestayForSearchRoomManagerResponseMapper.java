package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.homestay.HomestayForSearchRoomManagerResponseDto;
import com.example.vhomestay.model.entity.Homestay;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class HomestayForSearchRoomManagerResponseMapper {
    private final ModelMapper modelMapper;

    public HomestayForSearchRoomManagerResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.addMappings(new PropertyMap<Homestay, HomestayForSearchRoomManagerResponseDto>() {
            @Override
            protected void configure() {
                map().setHomestayId(source.getId());
                map().setHomestayCode(source.getHomestayCode());
            }
        });
    }
    public HomestayForSearchRoomManagerResponseDto mapper(Homestay homestay) {
        return modelMapper.map(homestay, HomestayForSearchRoomManagerResponseDto.class);
    }
    public Homestay mapper(HomestayForSearchRoomManagerResponseDto homestayForSearchRoomManagerResponseDto) {
        return modelMapper.map(homestayForSearchRoomManagerResponseDto, Homestay.class);
    }
}
