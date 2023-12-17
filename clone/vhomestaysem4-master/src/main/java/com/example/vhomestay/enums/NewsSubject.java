package com.example.vhomestay.enums;

public enum NewsSubject {
    TRAVEL_NEWS("Tin tức du lịch"),
    DESTINATION("Điểm đến"),
    TRAVEL_GUIDE("Cẩm nang du lịch"),
    CULTURE_AND_FOOD("Văn hóa và Ẩm thực"),
    VOLUNTEER("Tình nguyện"),
    OTHER("Khác");

    private String subject;

    NewsSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }


}
