package com.example.vhomestay.service.impl;

import com.example.vhomestay.enums.BaseStatus;
import com.example.vhomestay.enums.PaymentStatus;
import com.example.vhomestay.enums.RoomStatus;
import com.example.vhomestay.model.dto.request.RoomCreateRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingDetailCreateManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingDetailRandomDormSlotManagerRequestDto;
import com.example.vhomestay.model.dto.request.booking.BookingRandomDormSlotManagerRequestDto;
import com.example.vhomestay.model.dto.response.*;
import com.example.vhomestay.model.dto.response.room.RoomSearchManagerResponseDto;
import com.example.vhomestay.model.entity.*;
import com.example.vhomestay.repository.BookingDetailRepository;
import com.example.vhomestay.repository.DormSlotRepository;
import com.example.vhomestay.repository.HouseholdRepository;
import com.example.vhomestay.repository.RoomRepository;
import com.example.vhomestay.security.SecurityUtil;
import com.example.vhomestay.service.HomestayService;
import com.example.vhomestay.service.HouseholdRoomTypeService;
import com.example.vhomestay.service.RoomService;
import com.example.vhomestay.util.exception.ResourceBadRequestException;
import com.example.vhomestay.util.exception.ResourceInternalServerErrorException;
import com.example.vhomestay.util.exception.ResourceNotFoundException;
import com.example.vhomestay.util.validation.Validation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RoomServiceImpl extends BaseServiceImpl<Room, Long, RoomRepository>
        implements RoomService {
    private final RoomRepository roomRepository;
    private final HomestayService homestayService;
    private final HouseholdRepository householdRepository;
    private final HouseholdRoomTypeService householdRoomTypeService;
    private final DormSlotRepository dormSlotRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final ModelMapper modelMapper;

    public RoomServiceImpl(RoomRepository roomRepository, HomestayService homestayService, HouseholdRepository householdRepository, HouseholdRoomTypeService householdRoomTypeService, DormSlotRepository dormSlotRepository, BookingDetailRepository bookingDetailRepository, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.homestayService = homestayService;
        this.householdRepository = householdRepository;
        this.householdRoomTypeService = householdRoomTypeService;
        this.dormSlotRepository = dormSlotRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.createTypeMap(Room.class, RoomInformationDto.class)
                .addMapping(src -> src.getHomestay().getHousehold().getId(), RoomInformationDto::setHouseholdId)
                .addMapping(Room::getId, RoomInformationDto::setRoomId)
                .addMapping(Room::getRoomName, RoomInformationDto::setRoomName)
                .addMapping(Room::getStatus, RoomInformationDto::setRoomStatus)
                .addMapping(src -> src.getHomestay().getHomestayCode(), RoomInformationDto::setHomestayCode)
                .addMapping(src -> src.getHouseholdRoomType().getRoomType().getRoomTypeName(), RoomInformationDto::setRoomTypeName)
                .addMapping(src -> src.getHouseholdRoomType().getPrice(), RoomInformationDto::setPrice)
                .addMapping(src -> src.getHouseholdRoomType().getCapacity(), RoomInformationDto::setCapacity)
                .addMapping(src -> src.getHouseholdRoomType().getRoomType().getSingleBed(), RoomInformationDto::setSingleBed)
                .addMapping(src -> src.getHouseholdRoomType().getRoomType().getDoubleBed(), RoomInformationDto::setDoubleBed)
                .addMapping(src -> src.getHouseholdRoomType().getIsChildrenAndBed(), RoomInformationDto::setIsChildrenAndBed)
                .addMapping(src -> src.getHouseholdRoomType().getRoomTypeFacilities(), RoomInformationDto::setFacilities)
                .addMapping(src -> src.getHouseholdRoomType().getHomestayMedias(), RoomInformationDto::setHomestayMedias)
                .addMapping(src -> src.getHouseholdRoomType().getRoomType().getIsDorm(), RoomInformationDto::setDorm);
    }

    @Override
    public Optional<Room> findRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    public boolean addRoom(RoomCreateRequestDto roomCreateRequestDto) {
        Optional<Homestay> homestay = homestayService.findById(roomCreateRequestDto.getHomestayId());
        Optional<HouseholdRoomType> householdRoomType = householdRoomTypeService.findById(roomCreateRequestDto.getHouseholdRoomTypeId());
        for (String roomName : roomCreateRequestDto.getRoomNameList()) {
            Room room = new Room();
            room.setRoomName(roomName);
            room.setHomestay(homestay.get());
            room.setHouseholdRoomType(householdRoomType.get());
            room.setStatus(RoomStatus.ACTIVE);
            roomRepository.save(room);
        }
        return true;
    }

    @Override
    public boolean addDormSlot(DormSlotFormDto dormSlotFormDto) {
        Optional<Room> roomOptional = roomRepository.findById(dormSlotFormDto.getRoomId());
        if (roomOptional.isEmpty()) {
            throw new ResourceNotFoundException("room.not.found");
        }
        Room room = roomOptional.get();
        Integer numberOfSlots = dormSlotFormDto.getNumberOfSlots();
        Integer index = dormSlotRepository.findFinalIndexByRoomId(dormSlotFormDto.getRoomId());
        IntStream.range(0, numberOfSlots)
                .forEach(i -> {
                    DormSlot dormSlot = new DormSlot();
                    dormSlot.setSlotNumber(index + i + 1);
                    dormSlot.setRoom(room);
                    dormSlot.setStatus(BaseStatus.ACTIVE);
                    dormSlotRepository.save(dormSlot);
                });
        return true;
    }

    @Override
    public boolean removeDormSlot(DormSlotFormDto dormSlotFormDto) {
        Optional<Room> roomOptional = roomRepository.findById(dormSlotFormDto.getRoomId());
        if (roomOptional.isEmpty()) {
            throw new ResourceNotFoundException("room.not.found");
        }
        Integer numberOfSlots = dormSlotFormDto.getNumberOfSlots();
        List<DormSlot> dormSlotFinalIndex = dormSlotRepository.findDormSlotFinalIndexByRoomIdAndNumberOfSlots(dormSlotFormDto.getRoomId(), numberOfSlots);
        for (DormSlot dormSlot : dormSlotFinalIndex) {
            dormSlot.setStatus(BaseStatus.DELETED);
            dormSlotRepository.save(dormSlot);
        }
        return true;
    }

    @Override
    public List<RoomInformationResponseDto> getRoomListByManagerEmail(String managerEmail) {
        return roomRepository.findRoomByManagerEmail(managerEmail);
    }

    @Override
    public List<DormInformationResponseDto> getDormListByManagerEmail(String managerEmail) {
        List<DormInformationResponseDto> dormInformationResponseDtoList = roomRepository.findDormByManagerEmail(managerEmail);
        int size = dormInformationResponseDtoList.size();
        int i;
        for (i = 0; i < size; i++) {
            dormInformationResponseDtoList.get(i).setTotalDormSlot(
                    dormSlotRepository.countDormSlotByRoomId(dormInformationResponseDtoList.get(i).getRoomId()));
        }
        return dormInformationResponseDtoList;
    }

    @Override
    public RoomInformationDto mapToDTO(Room room) {
        return modelMapper.map(room, RoomInformationDto.class);
    }

    @Override
    public Room mapToEntity(RoomInformationDto roomInformationDto) {
        return modelMapper.map(roomInformationDto, Room.class);
    }

    @Override
    public boolean editRoom(Long roomId, RoomEditDto roomEditDto) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new ResourceNotFoundException("room.not.found");
        }
        Room room = roomOptional.get();
        if (roomEditDto.getRoomName() != null) {
            room.setRoomName(roomEditDto.getRoomName());
        }
        if (roomEditDto.getRoomStatus() != null) {
            room.setStatus(roomEditDto.getRoomStatus());
        }
        roomRepository.save(room);
        return true;
    }

    @Override
    public boolean deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("room.not.found"));
        try {
            if (Boolean.TRUE.equals(room.getHouseholdRoomType().getRoomType().getIsDorm())) {
                dormSlotRepository.updateStatusByRoomId(roomId, BaseStatus.DELETED);
            }
            room.setStatus(RoomStatus.DELETED);
            roomRepository.save(room);
            return true;
        } catch (Exception e) {
            throw new ResourceInternalServerErrorException("room.delete.failed");
        }
    }

    @Override
    public Integer countAllDormSlotByRoomId(Long roomId) {
        return dormSlotRepository.countDormSlotByRoomId(roomId);
    }

    @Override
    public boolean hideOrShowRoom(Long roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new ResourceNotFoundException("room.not.found");
        }
        Room room = roomOptional.get();
        if (room.getStatus().equals(RoomStatus.ACTIVE)) {
            room.setStatus(RoomStatus.INACTIVE);
        } else {
            room.setStatus(RoomStatus.ACTIVE);
        }
        roomRepository.save(room);
        return true;
    }

    @Override
    public List<RoomSearchManagerResponseDto> searchAvailableRoomsWithTotalDormSlotByManager(String homestayIdString, String checkInDateString, String checkOutDateString) {
        Optional<String> getCurrentUserEmail = SecurityUtil.getCurrentUserLogin();
        String managerEmail = getCurrentUserEmail.get();

        // Kiểm tra ngày check in và check out có null hay không
        if (checkInDateString == null || checkOutDateString == null) {
            throw new ResourceNotFoundException("date.invalid");
        }

        // Kiểm tra ngày check in và check out có đúng định dạng hay không
        LocalDate checkInDate = Validation.parseDate(checkInDateString);
        LocalDate checkOutDate = Validation.parseDate(checkOutDateString);

        // Kiểm tra homestayId có null hay không
        Long homestayId = null;
        if (homestayIdString != null && !homestayIdString.isEmpty()) {
            homestayId = Long.parseLong(homestayIdString);
        }

        // Kiểm tra ngày check in và check out có hợp lệ hay không
        if (checkInDate == null || checkOutDate == null
                || checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)
                || checkInDate.isBefore(LocalDate.now()) || checkOutDate.isBefore(LocalDate.now())) {
            throw new ResourceNotFoundException("date.invalid");
        }

        List<Object[]> objects = roomRepository.searchAvailableRoomsWithTotalDormSlotByManager(managerEmail, homestayId, checkInDate, checkOutDate);
        if (objects.isEmpty()) {
            throw new ResourceNotFoundException("homestay.not.found");
        }
        List<RoomSearchManagerResponseDto> roomSearchManagerResponseDtoList = new ArrayList<>();

        for (Object[] obj : objects) {
            RoomSearchManagerResponseDto dto = new RoomSearchManagerResponseDto();

            dto.setHomestayId(Long.parseLong(obj[0].toString()));
            dto.setHomestayName(obj[1].toString());
            dto.setHouseholdRoomTypeId(Long.parseLong(obj[2].toString()));
            dto.setHouseholdRoomTypeName(obj[3].toString());
            dto.setTotalSlotDefault(obj[4] == null ? null : Integer.parseInt(obj[4].toString()));
            dto.setRoomId(Long.parseLong(obj[5].toString()));
            dto.setRoomName(obj[6].toString());
            dto.setCapacity(obj[7] == null ? null : Integer.parseInt(obj[7].toString()));
            dto.setSingleBed(obj[8] == null ? null : Integer.parseInt(obj[8].toString()));
            dto.setDoubleBed(obj[9] == null ? null : Integer.parseInt(obj[9].toString()));
            dto.setPrice(obj[10] == null ? null : BigDecimal.valueOf(Double.parseDouble(obj[10].toString())));
            dto.setIsDorm(obj[11] == null ? null : obj[11].toString().equals("1") ? true : false);

            roomSearchManagerResponseDtoList.add(dto);
        }
        return roomSearchManagerResponseDtoList;
    }

    @Override
    public boolean checkRoomAndTotalSlotAvailability(BookingRandomDormSlotManagerRequestDto bookingRandomDormSlotManagerRequestDto) {
        // Lấy danh sách phòng trống
        List<RoomSearchManagerResponseDto> availableRooms = searchAvailableRoomsWithTotalDormSlotByManager(
                null,
                bookingRandomDormSlotManagerRequestDto.getCheckInDate().toString(),
                bookingRandomDormSlotManagerRequestDto.getCheckOutDate().toString());

        // Nếu danh sách phòng trống rỗng thì trả về false
        if (availableRooms.isEmpty()) {
            throw new ResourceBadRequestException("booking.do.not.have.blank.room");
        }

        // Lấy danh sách booking detail được trả về từ client
        List<BookingDetailRandomDormSlotManagerRequestDto> bookingDetails = bookingRandomDormSlotManagerRequestDto.getBookingDetails();

        // Nếu số lượng phòng trống nhỏ hơn số lượng booking detail thì trả về false
        if (availableRooms.size() < bookingDetails.size()) {
            throw new ResourceBadRequestException("booking.blank.room.not.enough");
        }

        // Lấy danh sách booking detail hợp lệ
        List<BookingDetailRandomDormSlotManagerRequestDto> validBookings = availableRooms.stream()
                .flatMap(room -> bookingDetails.stream()
                        .filter(booking -> room.getRoomId().equals(booking.getRoomId()))
                        .filter(booking -> !room.getIsDorm() || room.getTotalSlotDefault() >= booking.getTotalSlotSelected()))
                .collect(Collectors.toList());

        // Nếu danh sách booking detail hợp lệ khác danh sách booking detail được trả về từ client thì có nghĩa là một trong các booking detail không hợp lệ
        if (validBookings.size() != bookingDetails.size()) {

            // Lấy danh sách booking detail không hợp lệ
            List<BookingDetailRandomDormSlotManagerRequestDto> invalidBookings = new ArrayList<>(bookingDetails);

            // Danh sách booking detail không hợp lệ = danh sách booking detail - danh sách booking detail hợp lệ
            invalidBookings.removeAll(validBookings);

            // Lấy danh sách tên phòng không hợp lệ
            String roomNames = invalidBookings.stream()
                    .filter(booking -> !booking.getIsDorm())
                    .map(BookingDetailRandomDormSlotManagerRequestDto::getRoomId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            // Lấy danh sách tên dorm không hợp lệ
            String dormNames = invalidBookings.stream()
                    .filter(booking -> booking.getIsDorm())
                    .map(BookingDetailRandomDormSlotManagerRequestDto::getRoomId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            if (!roomNames.isEmpty() && !dormNames.isEmpty()) {
                throw new ResourceBadRequestException("Phòng " + roomNames + " đã được đặt, dorm " + dormNames + " không đủ slot");
            } else if (!roomNames.isEmpty()) {
                throw new ResourceBadRequestException("Phòng " + roomNames + " đã được đặt");
            } else if (!dormNames.isEmpty()) {
                throw new ResourceBadRequestException("Dorm " + dormNames + " không đủ slot");
            }
        }
        return true;
    }

    @Override
    public BookingCreateManagerRequestDto randomDormSlotAvailableByManager(BookingRandomDormSlotManagerRequestDto bookingRandomDormSlotManagerRequestDto) {
        String emailManager = SecurityUtil.getCurrentUserLogin().get();

        Household household = householdRepository.findByManagerEmail(emailManager).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy household"));

        BookingCreateManagerRequestDto bookingCreateManagerRequestDto = new BookingCreateManagerRequestDto();
        bookingCreateManagerRequestDto.setHouseholdId(household.getId());
        bookingCreateManagerRequestDto.setCheckInDate(bookingRandomDormSlotManagerRequestDto.getCheckInDate());
        bookingCreateManagerRequestDto.setCheckOutDate(bookingRandomDormSlotManagerRequestDto.getCheckOutDate());

        // Tổng số đêm = checkOutDate - checkInDate
        int totalNight = (int) ChronoUnit.DAYS.between(bookingRandomDormSlotManagerRequestDto.getCheckInDate(), bookingRandomDormSlotManagerRequestDto.getCheckOutDate());

        bookingCreateManagerRequestDto.setTotalNight(totalNight);

        // Khi gửi lên client, totalOfGuest, checkInCustomerName,
        // checkInCustomerPhone, checkInCustomerEmail được set mặc định là null để người dùng nhập

        // Tổng số phòng = tổng số booking detail
        bookingCreateManagerRequestDto.setTotalRoom(bookingRandomDormSlotManagerRequestDto.getBookingDetails().size());

        // random slot
        // Lấy danh sách slot trống
        List<DormSlot> dormSlots = bookingDetailRepository.findAllDormSlotAvailable(household.getId(),
                bookingRandomDormSlotManagerRequestDto.getCheckInDate(),
                bookingRandomDormSlotManagerRequestDto.getCheckOutDate());

        List<DormSlot> dormSlotsRandom = new ArrayList<>();
        List<BookingDetailCreateManagerRequestDto> bookingDetails = new ArrayList<>();
        BookingDetailCreateManagerRequestDto bookingDetail;

        for (BookingDetailRandomDormSlotManagerRequestDto bd : bookingRandomDormSlotManagerRequestDto.getBookingDetails()) {
            // Nếu là phòng dorm thì random slot rồi thêm vào danh sách slot trống được random
            if (bd.getIsDorm()) {
                // Lấy danh sách slot trống của dorm
                List<DormSlot> dormSlotsAvailable = dormSlots.stream()
                        .filter(dormSlot -> dormSlot.getRoom().getId().equals(bd.getRoomId()))
                        .collect(Collectors.toList());

                // Lấy danh sách slot trống của dorm được random
                List<DormSlot> dormSlotsAvailableRandom = dormSlotsAvailable.stream()
                        .limit(bd.getTotalSlotSelected())
                        .collect(Collectors.toList());

                // Thêm danh sách slot trống của dorm được random vào danh sách slot trống được random
                dormSlotsRandom.addAll(dormSlotsAvailableRandom);
            } else if (!bd.getIsDorm()) {
                // Nếu là phòng thường thì lấy thông tin phòng không cần random slot
                bookingDetail = new BookingDetailCreateManagerRequestDto();

                bookingDetail.setHomestayId(bd.getHomestayId());
                bookingDetail.setHomestayName(bd.getHomestayName());
                bookingDetail.setPrice(bd.getPrice());
                bookingDetail.setSubTotal(bd.getPrice().multiply(BigDecimal.valueOf(totalNight)));
                bookingDetail.setHouseholdRoomTypeId(bd.getHouseholdRoomTypeId());
                bookingDetail.setHouseholdRoomTypeName(bd.getHouseholdRoomTypeName());
                bookingDetail.setRoomId(bd.getRoomId());
                bookingDetail.setRoomName(bd.getRoomName());
                bookingDetail.setIsDorm(bd.getIsDorm());

                bookingDetails.add(bookingDetail);
            }
        }

        // Tạo danh sách booking detail từ danh sách slot trống được random
        for (DormSlot ds : dormSlotsRandom) {
            bookingDetail = new BookingDetailCreateManagerRequestDto();
            bookingDetail.setHomestayId(ds.getRoom().getHomestay().getId());
            bookingDetail.setHomestayName(ds.getRoom().getHomestay().getHomestayCode());
            bookingDetail.setHouseholdRoomTypeId(ds.getRoom().getHouseholdRoomType().getId());
            bookingDetail.setHouseholdRoomTypeName(ds.getRoom().getHouseholdRoomType().getRoomType().getRoomTypeName());
            bookingDetail.setPrice(ds.getRoom().getHouseholdRoomType().getPrice());
            bookingDetail.setSubTotal(ds.getRoom().getHouseholdRoomType().getPrice().multiply(BigDecimal.valueOf(totalNight)));
            bookingDetail.setDormSlotId(ds.getId());
            bookingDetail.setDormSlotName(ds.getSlotNumber());
            bookingDetail.setRoomId(ds.getRoom().getId());
            bookingDetail.setRoomName(ds.getRoom().getRoomName());
            bookingDetail.setIsDorm(ds.getRoom().getHouseholdRoomType().getRoomType().getIsDorm());

            bookingDetails.add(bookingDetail);
        }

        bookingCreateManagerRequestDto.setBookingDetails(bookingDetails);

        // Tổng tiền bằng tổng số đêm * giá phòng
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal subTotalPrice;
        for (BookingDetailRandomDormSlotManagerRequestDto bd : bookingRandomDormSlotManagerRequestDto.getBookingDetails()) {
            subTotalPrice = BigDecimal.ZERO;
            if (bd.getIsDorm()) {
                // Nếu là dorm thì tổng tiền = tổng số slot * giá phòng * tổng số đêm
                subTotalPrice = bd.getPrice().multiply(BigDecimal.valueOf(bd.getTotalSlotSelected()).multiply(BigDecimal.valueOf(totalNight)));
            } else {
                subTotalPrice = bd.getPrice().multiply(BigDecimal.valueOf(totalNight));
            }

            totalPrice = totalPrice.add(subTotalPrice);
        }
        bookingCreateManagerRequestDto.setTotalPrice(totalPrice);

        return bookingCreateManagerRequestDto;
    }

    @Override
    public boolean checkRoomAndDormSlotAvailability(BookingCreateManagerRequestDto bookingCreateManagerRequestDto) {
        List<BookingDetailCreateManagerRequestDto> bookingDetails = bookingCreateManagerRequestDto.getBookingDetails();

        // Lấy danh sách slot trống
        List<DormSlot> dormSlots = bookingDetailRepository.findAllDormSlotAvailable(bookingCreateManagerRequestDto.getHouseholdId(),
                bookingCreateManagerRequestDto.getCheckInDate(),
                bookingCreateManagerRequestDto.getCheckOutDate());

        // Lấy danh sách phòng trống
        List<Room> rooms = bookingDetailRepository.findAllRoomAvailable(bookingCreateManagerRequestDto.getHouseholdId(),
                bookingCreateManagerRequestDto.getCheckInDate(),
                bookingCreateManagerRequestDto.getCheckOutDate());

        if (dormSlots.isEmpty()) throw new ResourceNotFoundException("Không có slot trống");
        if (rooms.isEmpty()) throw new ResourceNotFoundException("Không có phòng trống");

        // Kiểm tra xem có đủ phòng và slot trống không
        List<BookingDetailCreateManagerRequestDto> validBookingDetails = bookingDetails.stream()
                .filter(bd -> {
                    if (bd.getIsDorm()) {
                        return dormSlots.stream()
                                .anyMatch(ds -> ds.getRoom().getId().equals(bd.getRoomId()) && ds.getId().equals(bd.getDormSlotId()));
                    } else {
                        return rooms.stream()
                                .anyMatch(r -> r.getId().equals(bd.getRoomId()));
                    }
                }).toList();

        if (validBookingDetails.size() != bookingDetails.size()) {
            List<BookingDetailCreateManagerRequestDto> invalidBookingDetails = new ArrayList<>(bookingDetails);

            invalidBookingDetails.removeAll(validBookingDetails);

            String roomNames = invalidBookingDetails.stream()
                    .filter(bd -> !bd.getIsDorm())
                    .map(BookingDetailCreateManagerRequestDto::getRoomId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            // Lấy danh sách tên dorm không hợp lệ
            String dormNames = invalidBookingDetails.stream()
                    .filter(bd -> bd.getIsDorm())
                    .map(BookingDetailCreateManagerRequestDto::getDormSlotId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            if (!roomNames.isEmpty() && !dormNames.isEmpty()) {
                throw new ResourceBadRequestException("Phòng " + roomNames + " đã được đặt, mã đệm " + dormNames + " đã được đặt");
            } else if (!roomNames.isEmpty()) {
                throw new ResourceBadRequestException("Phòng " + roomNames + " đã được đặt");
            } else if (!dormNames.isEmpty()) {
                throw new ResourceBadRequestException("Mã đệm " + dormNames + " đã được đặt");
            }
        }
        return true;


//        for (BookingDetailCreateManagerRequestDto bd : bookingDetails) {
//            if (bd.getIsDorm()) {
//                for (DormSlot ds : dormSlots) {
//                    if (ds.getRoom().getId().equals(bd.getRoomId()) && ds.getId().equals(bd.getDormSlotId())) {
//                        validBookingDetails.add(bd);
//                    }
//                }
//            } else {
//                for (Room r : rooms) {
//                    if (r.getId().equals(bd.getRoomId())) {
//                        validBookingDetails.add(bd);
//                    }
//                }
//            }
//        }

//        return validBookingDetails.size() == bookingDetails.size();
    }
}
