package com.example.vhomestay.mapper;

import com.example.vhomestay.model.dto.response.UserResponseDto;
import com.example.vhomestay.model.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper {
    private final ModelMapper modelMapper;

    public UserResponseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(Customer.class, UserResponseDto.class)
                .addMapping(Customer::getId, UserResponseDto::setId)
                .addMapping(Customer::getAvatar, UserResponseDto::setAvatar)
                .addMapping(src -> src.getAccount().getEmail(), UserResponseDto::setEmail)
                .addMapping(Customer::getFirstName, UserResponseDto::setFirstName)
                .addMapping(Customer::getLastName, UserResponseDto::setLastName)
                .addMapping(Customer::getPhoneNumber, UserResponseDto::setPhoneNumber)
                .addMapping(Customer::getGender, UserResponseDto::setGender)
                .addMapping(Customer::getDateOfBirth, UserResponseDto::setDateOfBirth)
                .addMapping(Customer::getAddress, UserResponseDto::setAddress)
                .addMapping(src -> src.getAccount().getId(), UserResponseDto::setAccountId)
                .addMapping(src -> src.getAccount().getRole(), UserResponseDto::setRole)
                .addMapping(src -> src.getAccount().getProvider(), UserResponseDto::setProvider);
    }

    public UserResponseDto mapToCustomerDTO(Customer customer) {
        return modelMapper.map(customer, UserResponseDto.class);
    }
    public Customer mapToCustomerEntity(UserResponseDto userResponseDto) {
        return modelMapper.map(userResponseDto, Customer.class);
    }
}
