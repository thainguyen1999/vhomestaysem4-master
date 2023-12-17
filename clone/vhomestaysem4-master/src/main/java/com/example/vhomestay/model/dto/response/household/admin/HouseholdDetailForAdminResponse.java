package com.example.vhomestay.model.dto.response.household.admin;

import com.example.vhomestay.enums.HouseholdStatus;
import com.example.vhomestay.model.dto.response.household.admin.HomestayDetailForAdminResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HouseholdDetailForAdminResponse {
    private Long householdId;
    private String householdName;
    private String managerFirstName;
    private String managerLastName;
    private String managerPhone;
    private String managerEmail;
    private Integer numberOfHomestay;
    private List<HomestayDetailForAdminResponse> homestayDetailForAdminList;
    private String avatar;
    private String coverImage;
    private String phoneNumberFirst;
    private String phoneNumberSecond;
    private String email;
    private String linkFacebook;
    private String linkTiktok;
    private String description;
    private String linkYoutube;
    private HouseholdStatus householdStatus;

    public HouseholdDetailForAdminResponse(Long householdId, String householdName, String managerFirstName, String managerLastName, String managerPhone, String managerEmail, String avatar, String coverImage, String phoneNumberFirst, String phoneNumberSecond, String email, String linkFacebook, String linkTiktok, String description, String linkYoutube, HouseholdStatus householdStatus) {
        this.householdId = householdId;
        this.householdName = householdName;
        this.managerFirstName = managerFirstName;
        this.managerLastName = managerLastName;
        this.managerPhone = managerPhone;
        this.managerEmail = managerEmail;
        this.avatar = avatar;
        this.coverImage = coverImage;
        this.phoneNumberFirst = phoneNumberFirst;
        this.phoneNumberSecond = phoneNumberSecond;
        this.email = email;
        this.linkFacebook = linkFacebook;
        this.linkTiktok = linkTiktok;
        this.description = description;
        this.linkYoutube = linkYoutube;
        this.householdStatus = householdStatus;
    }
}
