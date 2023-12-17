package com.example.vhomestay.model.dto.response.household.customer;

import com.example.vhomestay.enums.HouseholdStatus;
import com.example.vhomestay.model.dto.response.homestay.HomestayDetailForCustomerResponse;
import com.example.vhomestay.model.dto.response.roomtype.customer.HouseholdRoomTypeForCustomerResponse;
import com.example.vhomestay.model.dto.response.service.manager.ServiceDetailResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HouseholdDetailForCustomer {
    private Long householdId;
    private String householdName;
    private List<HomestayDetailForCustomerResponse> homestayDetailForCustomerList;
    private String avatar;
    private String coverImage;
    private String phoneNumberFirst;
    private String phoneNumberSecond;
    private String email;
    private String linkFacebook;
    private String linkTiktok;
    private String description;
    private String linkYoutube;
    private List<HouseholdRoomTypeForCustomerResponse> householdRoomTypeForCustomerList;
    private List<ServiceDetailResponse> serviceDetailForCustomerList;

    public HouseholdDetailForCustomer(Long householdId, String householdName, String avatar, String coverImage, String phoneNumberFirst, String phoneNumberSecond, String email, String linkFacebook, String linkTiktok, String description, String linkYoutube) {
        this.householdId = householdId;
        this.householdName = householdName;
        this.avatar = avatar;
        this.coverImage = coverImage;
        this.phoneNumberFirst = phoneNumberFirst;
        this.phoneNumberSecond = phoneNumberSecond;
        this.email = email;
        this.linkFacebook = linkFacebook;
        this.linkTiktok = linkTiktok;
        this.description = description;
        this.linkYoutube = linkYoutube;
    }
}
