package com.caloryhive.business.catering.service;

import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.entity.*;
import com.caloryhive.business.catering.mapper.CateringMapper;
import com.caloryhive.business.catering.repository.*;
import com.caloryhive.business.common.enums.BookingStatus;
import com.caloryhive.business.common.enums.InquiryStatus;
import com.caloryhive.business.common.enums.PaymentType;
import com.caloryhive.business.common.enums.RoleName;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.notification.entity.Notification;
import com.caloryhive.business.notification.repository.NotificationRepository;
import com.caloryhive.business.payment.entity.Payment;
import com.caloryhive.business.payment.repository.PaymentRepository;
import com.caloryhive.business.user.entity.User;
import com.caloryhive.business.user.repository.RoleRepository;
import com.caloryhive.business.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CateringService {

    private final CateringEventTypeRepository eventTypeRepository;
    private final CateringMenuPackageRepository menuPackageRepository;
    private final CateringCustomOptionRepository customOptionRepository;
    private final CateringBookingRepository bookingRepository;
    private final BookingCustomOptionRepository bookingCustomOptionRepository;
    private final CateringInquiryRepository inquiryRepository;
    private final BookingStatusHistoryRepository statusHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PricingService pricingService;
    private final CateringMapper cateringMapper;

    @Value("${app.pricing.deposit-percent:0.30}")
    private BigDecimal depositPercent = new BigDecimal("0.30");

    // =========================================================================
    // 1. CREATE BOOKING
    // =========================================================================

    @Transactional
    public BookingDetailsResponse createBookingDetails(CreateBookingRequest request) {
        CateringBooking booking = createBookingInternal(request, BookingStatus.PENDING);
        return cateringMapper.toBookingDetails(booking);
    }

    @Transactional
    public CateringBooking createBooking(CreateBookingRequest request) {
        return createBookingInternal(request, BookingStatus.PENDING);
    }

    private CateringBooking createBookingInternal(CreateBookingRequest request, BookingStatus bookingStatus) {
        if (request.getGuestCount() == null || request.getGuestCount() <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than 0");
        }
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }

        CateringEventType eventType = eventTypeRepository.findById(request.getEventTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + request.getEventTypeId()));
        if (!eventType.isActive()) {
            throw new IllegalArgumentException("Selected event type is currently inactive");
        }

        CateringMenuPackage menuPackage = menuPackageRepository.findById(request.getMenuPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + request.getMenuPackageId()));
        if (!menuPackage.isActive()) {
            throw new IllegalArgumentException("Selected menu package is currently inactive");
        }

        List<CateringCustomOption> customOptions = new ArrayList<>();
        if (request.getCustomOptionIds() != null && !request.getCustomOptionIds().isEmpty()) {
            for (UUID customOptionId : request.getCustomOptionIds()) {
                CateringCustomOption customOption = customOptionRepository.findById(customOptionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + customOptionId));
                if (!customOption.isActive()) {
                    throw new IllegalArgumentException("Selected custom option is inactive: " + customOption.getName());
                }
                customOptions.add(customOption);
            }
        }

        CateringBooking booking = new CateringBooking();
        booking.setBookingCode(generateBookingCode());
        booking.setEventName(request.getEventName());
        booking.setEventType(eventType);
        booking.setEventDate(request.getEventDate());
        booking.setEventTime(request.getEventTime());
        booking.setGuestCount(request.getGuestCount());
        booking.setVenueAddress(request.getVenueAddress());
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setMenuPackage(menuPackage);
        booking.setCreatedBy(getCurrentUser());
        booking.setStatus(bookingStatus);

        pricingService.applyPricing(booking, menuPackage, customOptions);

        CateringBooking saved = bookingRepository.save(booking);

        saved.getBookingCustomOptions().clear();
        for (CateringCustomOption option : customOptions) {
            BookingCustomOption bco = new BookingCustomOption();
            bco.setBooking(saved);
            bco.setCustomOption(option);
            saved.getBookingCustomOptions().add(bco);
        }

        saved.getBookingMenuPackages().clear();
        BookingMenuPackage bmp = new BookingMenuPackage();
        bmp.setBooking(saved);
        bmp.setMenuPackage(menuPackage);
        saved.getBookingMenuPackages().add(bmp);

        saved = bookingRepository.save(saved);

        addStatusHistory(saved, null, "Booking created in status: " + bookingStatus.name());

        return saved;
    }

    // =========================================================================
    // 2. SAVE BOOKING AS DRAFT
    // =========================================================================

    @Transactional
    public BookingDetailsResponse saveBookingAsDraft(DraftBookingRequest request) {
        CateringEventType eventType = null;
        if (request.getEventTypeId() != null) {
            eventType = eventTypeRepository.findById(request.getEventTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event type not found"));
        }

        CateringMenuPackage menuPackage = null;
        if (request.getMenuPackageId() != null) {
            menuPackage = menuPackageRepository.findById(request.getMenuPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu package not found"));
        }

        List<CateringCustomOption> customOptions = new ArrayList<>();
        if (request.getCustomOptionIds() != null && !request.getCustomOptionIds().isEmpty()) {
            for (UUID customOptionId : request.getCustomOptionIds()) {
                CateringCustomOption customOption = customOptionRepository.findById(customOptionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + customOptionId));
                customOptions.add(customOption);
            }
        }

        int guestCount = (request.getGuestCount() != null && request.getGuestCount() > 0) ? request.getGuestCount() : 1;

        CateringBooking booking = new CateringBooking();
        booking.setBookingCode(generateBookingCode());
        booking.setEventName(request.getEventName());
        booking.setEventType(eventType);
        booking.setEventDate(request.getEventDate());
        booking.setEventTime(request.getEventTime());
        booking.setGuestCount(guestCount);
        booking.setVenueAddress(request.getVenueAddress());
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setMenuPackage(menuPackage);
        booking.setCreatedBy(getCurrentUser());
        booking.setStatus(BookingStatus.DRAFT);

        if (menuPackage != null) {
            pricingService.applyPricing(booking, menuPackage, customOptions);
        } else {
            booking.setBasePrice(BigDecimal.ZERO.setScale(2));
            booking.setCustomOptionsTotal(BigDecimal.ZERO.setScale(2));
            booking.setSubtotal(BigDecimal.ZERO.setScale(2));
            booking.setServiceFee(BigDecimal.ZERO.setScale(2));
            booking.setTax(BigDecimal.ZERO.setScale(2));
            booking.setFinalTotal(BigDecimal.ZERO.setScale(2));
            booking.setDepositPercentage(depositPercent);
            booking.setDepositRequired(BigDecimal.ZERO.setScale(2));
        }

        CateringBooking saved = bookingRepository.save(booking);

        if (!customOptions.isEmpty()) {
            for (CateringCustomOption option : customOptions) {
                BookingCustomOption bco = new BookingCustomOption();
                bco.setBooking(saved);
                bco.setCustomOption(option);
                saved.getBookingCustomOptions().add(bco);
            }
        }

        if (menuPackage != null) {
            BookingMenuPackage bmp = new BookingMenuPackage();
            bmp.setBooking(saved);
            bmp.setMenuPackage(menuPackage);
            saved.getBookingMenuPackages().add(bmp);
        }

        saved = bookingRepository.save(saved);
        addStatusHistory(saved, null, "Draft booking saved");

        return cateringMapper.toBookingDetails(saved);
    }

    @Transactional
    public CateringBooking createDraftBooking(CreateBookingRequest request) {
        return createBookingInternal(request, BookingStatus.DRAFT);
    }

    // =========================================================================
    // 3. UPDATE BOOKING
    // =========================================================================

    @Transactional
    public BookingDetailsResponse updateBookingDetails(UUID id, UpdateBookingRequest request) {
        CateringBooking booking = updateBookingInternal(id, request);
        return cateringMapper.toBookingDetails(booking);
    }

    @Transactional
    public CateringBooking updateBooking(UUID id, UpdateBookingRequest request) {
        return updateBookingInternal(id, request);
    }

    private CateringBooking updateBookingInternal(UUID id, UpdateBookingRequest request) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        if (booking.getStatus() != BookingStatus.DRAFT && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only draft or pending bookings can be updated. Current status: " + booking.getStatus());
        }

        checkBookingAccess(booking, getCurrentUser());

        if (request.getGuestCount() == null || request.getGuestCount() <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than 0");
        }
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }

        CateringEventType eventType = eventTypeRepository.findById(request.getEventTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + request.getEventTypeId()));
        if (!eventType.isActive()) {
            throw new IllegalArgumentException("Selected event type is currently inactive");
        }

        CateringMenuPackage menuPackage = menuPackageRepository.findById(request.getMenuPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + request.getMenuPackageId()));
        if (!menuPackage.isActive()) {
            throw new IllegalArgumentException("Selected menu package is currently inactive");
        }

        List<CateringCustomOption> customOptions = new ArrayList<>();
        if (request.getCustomOptionIds() != null && !request.getCustomOptionIds().isEmpty()) {
            for (UUID customOptionId : request.getCustomOptionIds()) {
                CateringCustomOption option = customOptionRepository.findById(customOptionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + customOptionId));
                if (!option.isActive()) {
                    throw new IllegalArgumentException("Selected custom option is inactive: " + option.getName());
                }
                customOptions.add(option);
            }
        }

        booking.setEventName(request.getEventName());
        booking.setEventType(eventType);
        booking.setEventDate(request.getEventDate());
        booking.setEventTime(request.getEventTime());
        booking.setGuestCount(request.getGuestCount());
        booking.setVenueAddress(request.getVenueAddress());
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setMenuPackage(menuPackage);

        // Update custom options
        booking.getBookingCustomOptions().clear();
        for (CateringCustomOption option : customOptions) {
            BookingCustomOption bco = new BookingCustomOption();
            bco.setBooking(booking);
            bco.setCustomOption(option);
            booking.getBookingCustomOptions().add(bco);
        }

        // Update menu package relation
        booking.getBookingMenuPackages().clear();
        BookingMenuPackage bmp = new BookingMenuPackage();
        bmp.setBooking(booking);
        bmp.setMenuPackage(menuPackage);
        booking.getBookingMenuPackages().add(bmp);

        pricingService.applyPricing(booking, menuPackage, customOptions);

        addStatusHistory(booking, booking.getStatus(), "Booking details updated");
        return bookingRepository.save(booking);
    }

    // =========================================================================
    // 4. GET BOOKING DETAILS
    // =========================================================================

    public BookingDetailsResponse getBookingDetails(UUID id) {
        CateringBooking booking = bookingRepository.findByIdWithDetails(id);
        if (booking == null) {
            throw new ResourceNotFoundException("Booking not found: " + id);
        }
        checkBookingAccess(booking, getCurrentUser());
        return cateringMapper.toBookingDetails(booking);
    }

    public BookingDetailsResponse getBooking(UUID id) {
        return getBookingDetails(id);
    }

    // =========================================================================
    // 5. CALCULATE BOOKING FINANCIAL SUMMARY
    // =========================================================================

    public FinancialSummaryResponse calculateBookingFinancialSummary(PriceCalculationRequest request) {
        if (request.getGuestCount() == null || request.getGuestCount() <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than 0");
        }

        CateringMenuPackage menuPackage = menuPackageRepository.findById(request.getMenuPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + request.getMenuPackageId()));
        if (!menuPackage.isActive()) {
            throw new IllegalArgumentException("Selected menu package is inactive");
        }

        List<CateringCustomOption> options = new ArrayList<>();
        if (request.getCustomOptionIds() != null && !request.getCustomOptionIds().isEmpty()) {
            for (UUID optionId : request.getCustomOptionIds()) {
                CateringCustomOption option = customOptionRepository.findById(optionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + optionId));
                if (!option.isActive()) {
                    throw new IllegalArgumentException("Selected custom option is inactive: " + option.getName());
                }
                options.add(option);
            }
        }

        PricingService.PricingBreakdown breakdown = pricingService.calculatePricing(
                menuPackage, options, request.getGuestCount());

        return pricingService.toFinancialSummary(breakdown, request.getGuestCount());
    }

    public FinancialSummaryResponse getBookingFinancialSummary(UUID id) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        checkBookingAccess(booking, getCurrentUser());
        return cateringMapper.toFinancialSummary(booking);
    }

    // =========================================================================
    // 6. ADD / REMOVE CUSTOM MENU OPTIONS
    // =========================================================================

    @Transactional
    public BookingDetailsResponse addRemoveCustomMenuOptions(UUID id, UpdateCustomOptionsRequest request) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        if (booking.getStatus() != BookingStatus.DRAFT && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only draft or pending bookings can modify custom options. Current status: " + booking.getStatus());
        }

        checkBookingAccess(booking, getCurrentUser());

        List<CateringCustomOption> options = new ArrayList<>();
        if (request.getCustomOptionIds() != null && !request.getCustomOptionIds().isEmpty()) {
            for (UUID optionId : request.getCustomOptionIds()) {
                CateringCustomOption option = customOptionRepository.findById(optionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + optionId));
                if (!option.isActive()) {
                    throw new IllegalArgumentException("Custom option is not active: " + option.getName());
                }
                options.add(option);
            }
        }

        booking.getBookingCustomOptions().clear();
        for (CateringCustomOption option : options) {
            BookingCustomOption bco = new BookingCustomOption();
            bco.setBooking(booking);
            bco.setCustomOption(option);
            booking.getBookingCustomOptions().add(bco);
        }

        pricingService.applyPricing(booking, booking.getMenuPackage(), options);
        addStatusHistory(booking, booking.getStatus(), "Custom menu options updated");
        CateringBooking saved = bookingRepository.save(booking);

        return cateringMapper.toBookingDetails(saved);
    }

    // =========================================================================
    // 7. CONFIRM BOOKING
    // =========================================================================

    @Transactional
    public BookingConfirmationResponse confirmBooking(UUID id, ConfirmBookingRequest request) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking is already confirmed");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled booking");
        }

        if (booking.getVenueAddress() == null || booking.getVenueAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Venue address must not be blank when confirming a booking");
        }

        User currentUser = getCurrentUser();
        checkBookingAccess(booking, currentUser);

        // Recalculate pricing with latest package and custom option prices to guarantee integrity
        List<CateringCustomOption> currentOptions = booking.getBookingCustomOptions().stream()
                .map(BookingCustomOption::getCustomOption)
                .toList();
        pricingService.applyPricing(booking, booking.getMenuPackage(), currentOptions);

        if (booking.getBookingCode() == null || booking.getBookingCode().isBlank()) {
            booking.setBookingCode(generateBookingCode());
        }

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CONFIRMED);
        addStatusHistory(booking, oldStatus, "Booking confirmed and deposit recorded");

        // Record required deposit payment record in PENDING status (payment processed separately)
        Payment depositPayment = new Payment();
        depositPayment.setBooking(booking);
        depositPayment.setAmount(booking.getDepositRequired());
        depositPayment.setPaymentType(PaymentType.DEPOSIT);
        depositPayment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        if (request != null && request.getPaymentReference() != null && !request.getPaymentReference().isBlank()) {
            depositPayment.setTransactionReference(request.getPaymentReference());
        }
        paymentRepository.save(depositPayment);

        // Record notification
        Notification notification = new Notification();
        notification.setUser(currentUser);
        notification.setTitle("Booking Confirmed");
        notification.setMessage("Your booking " + booking.getBookingCode() + " for " + booking.getEventName() + " has been confirmed.");
        notification.setType("BOOKING");
        notificationRepository.save(notification);

        CateringBooking saved = bookingRepository.save(booking);
        return cateringMapper.toBookingConfirmationResponse(saved);
    }

    @Transactional
    public BookingConfirmationResponse confirmBooking(UUID id) {
        return confirmBooking(id, null);
    }

    // =========================================================================
    // 8. GET ACTIVE BOOKINGS
    // =========================================================================

    public Page<BookingListItemResponse> getActiveBookings(Pageable pageable) {
        Page<CateringBooking> page = bookingRepository.searchBookings(
                BookingStatus.CONFIRMED, null, null, pageable);
        List<BookingListItemResponse> content = page.getContent().stream()
                .map(cateringMapper::toBookingListItem)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public Page<BookingListItemResponse> listBookings(String status, String search, String eventType, Pageable pageable) {
        BookingStatus bookingStatus = (status == null || status.isBlank()) ? null : BookingStatus.valueOf(status.toUpperCase());
        Page<CateringBooking> page = bookingRepository.searchBookings(bookingStatus, search, eventType, pageable);
        List<BookingListItemResponse> content = page.getContent().stream()
                .map(cateringMapper::toBookingListItem)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    // =========================================================================
    // 9. GET UPCOMING EVENTS
    // =========================================================================

    public List<BookingListItemResponse> getUpcomingEvents(Pageable pageable) {
        List<CateringBooking> upcoming = bookingRepository.findUpcomingBookings(
                List.of(BookingStatus.CANCELLED, BookingStatus.DRAFT),
                LocalDate.now(),
                pageable);
        return upcoming.stream()
                .map(cateringMapper::toBookingListItem)
                .toList();
    }

    // =========================================================================
    // 10. GET DASHBOARD STATISTICS
    // =========================================================================

    public DashboardResponse getDashboardStatistics() {
        return getDashboard();
    }

    public DashboardResponse getDashboard() {
        long upcomingEvents = bookingRepository.countUpcomingEvents(
                List.of(BookingStatus.CANCELLED, BookingStatus.DRAFT),
                LocalDate.now());

        BigDecimal totalRevenue = bookingRepository.sumTotalRevenueByStatusNotIn(
                List.of(BookingStatus.CANCELLED, BookingStatus.DRAFT));
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO.setScale(2);
        }

        long newInquiries = inquiryRepository.countByStatus(InquiryStatus.NEW);

        List<BookingListItemResponse> activeBookings = bookingRepository.findUpcomingBookings(
                List.of(BookingStatus.CANCELLED, BookingStatus.DRAFT),
                LocalDate.now(),
                PageRequest.of(0, 5)
        ).stream().map(cateringMapper::toBookingListItem).toList();

        List<InquirySummaryResponse> recentInquiries = inquiryRepository.findTop5ByOrderByCreatedAtDesc()
                .stream().map(cateringMapper::toInquirySummaryResponse).toList();

        List<MenuPackageResponse> menuPackages = menuPackageRepository.findByActiveTrueOrderByNameAsc()
                .stream().map(cateringMapper::toMenuPackageResponse).toList();

        return DashboardResponse.builder()
                .upcomingEvents(upcomingEvents)
                .totalRevenue(totalRevenue)
                .newInquiries(newInquiries)
                .activeBookings(activeBookings)
                .recentInquiries(recentInquiries)
                .menuPackages(menuPackages)
                .build();
    }

    // =========================================================================
    // 11. GET CATERING PACKAGES
    // =========================================================================

    public List<MenuPackageResponse> getCateringPackages() {
        return menuPackageRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(cateringMapper::toMenuPackageResponse)
                .toList();
    }

    public List<Object> getMenuPackages() {
        return menuPackageRepository.findByActiveTrueOrderByNameAsc().stream().map(packageItem -> (Object) Map.of(
                "id", packageItem.getId().toString(),
                "name", packageItem.getName(),
                "description", packageItem.getDescription() != null ? packageItem.getDescription() : "",
                "pricePerGuest", packageItem.getPricePerGuest(),
                "imageUrl", packageItem.getImageUrl() != null ? packageItem.getImageUrl() : "",
                "active", packageItem.isActive())).toList();
    }

    public MenuPackageResponse getCateringPackage(UUID id) {
        CateringMenuPackage menuPackage = menuPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + id));
        return cateringMapper.toMenuPackageResponse(menuPackage);
    }

    public Object getMenuPackage(UUID id) {
        return menuPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + id));
    }

    @Transactional
    public Object createMenuPackage(Object request) {
        Map<String, Object> input = (Map<String, Object>) request;
        CateringMenuPackage menuPackage = new CateringMenuPackage();
        menuPackage.setName(String.valueOf(input.getOrDefault("name", "")));
        menuPackage.setDescription((String) input.get("description"));
        menuPackage.setPricePerGuest(new BigDecimal(String.valueOf(input.getOrDefault("pricePerGuest", "0"))));
        menuPackage.setImageUrl((String) input.get("imageUrl"));
        menuPackage.setActive(Boolean.TRUE.equals(input.get("active")) || input.get("active") == null);
        return menuPackageRepository.save(menuPackage);
    }

    @Transactional
    public Object updateMenuPackage(UUID id, Object request) {
        CateringMenuPackage menuPackage = menuPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + id));
        Map<String, Object> input = (Map<String, Object>) request;
        if (input.containsKey("name")) menuPackage.setName(String.valueOf(input.get("name")));
        if (input.containsKey("description")) menuPackage.setDescription((String) input.get("description"));
        if (input.containsKey("pricePerGuest")) menuPackage.setPricePerGuest(new BigDecimal(String.valueOf(input.get("pricePerGuest"))));
        if (input.containsKey("imageUrl")) menuPackage.setImageUrl((String) input.get("imageUrl"));
        if (input.containsKey("active")) menuPackage.setActive(Boolean.parseBoolean(String.valueOf(input.get("active"))));
        return menuPackageRepository.save(menuPackage);
    }

    @Transactional
    public void deleteMenuPackage(UUID id) {
        CateringMenuPackage menuPackage = menuPackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu package not found: " + id));
        menuPackageRepository.delete(menuPackage);
    }

    // =========================================================================
    // 12. GET BOOKING CONFIRMATION DETAILS
    // =========================================================================

    public BookingConfirmationResponse getBookingConfirmationDetails(UUID id) {
        return getConfirmation(id);
    }

    public BookingConfirmationResponse getConfirmation(UUID id) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        checkBookingAccess(booking, getCurrentUser());
        return cateringMapper.toBookingConfirmationResponse(booking);
    }

    // =========================================================================
    // EVENT TYPES & CUSTOM OPTIONS MANAGEMENT
    // =========================================================================

    public List<EventTypeResponse> getCateringEventTypes() {
        return eventTypeRepository.findAll().stream()
                .map(cateringMapper::toEventTypeResponse)
                .toList();
    }

    public List<Object> getEventTypes() {
        return eventTypeRepository.findAll().stream().map(type -> (Object) Map.of(
                "id", type.getId().toString(),
                "name", type.getName(),
                "description", type.getDescription() != null ? type.getDescription() : "",
                "active", type.isActive())).toList();
    }

    @Transactional
    public Object createEventType(Object request) {
        Map<String, Object> input = (Map<String, Object>) request;
        CateringEventType type = new CateringEventType();
        type.setName(String.valueOf(input.getOrDefault("name", "")));
        type.setDescription((String) input.get("description"));
        type.setActive(Boolean.TRUE.equals(input.get("active")) || input.get("active") == null);
        return eventTypeRepository.save(type);
    }

    @Transactional
    public Object updateEventType(UUID id, Object request) {
        CateringEventType type = eventTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + id));
        Map<String, Object> input = (Map<String, Object>) request;
        if (input.containsKey("name")) type.setName(String.valueOf(input.get("name")));
        if (input.containsKey("description")) type.setDescription((String) input.get("description"));
        if (input.containsKey("active")) type.setActive(Boolean.parseBoolean(String.valueOf(input.get("active"))));
        return eventTypeRepository.save(type);
    }

    @Transactional
    public void deleteEventType(UUID id) {
        CateringEventType type = eventTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found: " + id));
        eventTypeRepository.delete(type);
    }

    public List<CustomOptionResponse> getCateringCustomOptions() {
        return customOptionRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(cateringMapper::toCustomOptionResponse)
                .toList();
    }

    public List<Object> getCustomOptions() {
        return customOptionRepository.findByActiveTrueOrderByNameAsc().stream().map(option -> (Object) Map.of(
                "id", option.getId().toString(),
                "name", option.getName(),
                "description", option.getDescription() != null ? option.getDescription() : "",
                "price", option.getPrice(),
                "active", option.isActive())).toList();
    }

    @Transactional
    public Object createCustomOption(Object request) {
        Map<String, Object> input = (Map<String, Object>) request;
        CateringCustomOption option = new CateringCustomOption();
        option.setName(String.valueOf(input.getOrDefault("name", "")));
        option.setDescription((String) input.get("description"));
        option.setPrice(new BigDecimal(String.valueOf(input.getOrDefault("price", "0"))));
        option.setActive(Boolean.TRUE.equals(input.get("active")) || input.get("active") == null);
        return customOptionRepository.save(option);
    }

    @Transactional
    public Object updateCustomOption(UUID id, Object request) {
        CateringCustomOption option = customOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + id));
        Map<String, Object> input = (Map<String, Object>) request;
        if (input.containsKey("name")) option.setName(String.valueOf(input.get("name")));
        if (input.containsKey("description")) option.setDescription((String) input.get("description"));
        if (input.containsKey("price")) option.setPrice(new BigDecimal(String.valueOf(input.get("price"))));
        if (input.containsKey("active")) option.setActive(Boolean.parseBoolean(String.valueOf(input.get("active"))));
        return customOptionRepository.save(option);
    }

    @Transactional
    public void deleteCustomOption(UUID id) {
        CateringCustomOption option = customOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Custom option not found: " + id));
        customOptionRepository.delete(option);
    }

    // =========================================================================
    // CANCEL BOOKING & TIMELINE & STATUS HISTORY
    // =========================================================================

    @Transactional
    public BookingDetailsResponse cancelBookingDetails(UUID id) {
        CateringBooking booking = cancelBookingInternal(id);
        return cateringMapper.toBookingDetails(booking);
    }

    @Transactional
    public CateringBooking cancelBooking(UUID id) {
        return cancelBookingInternal(id);
    }

    private CateringBooking cancelBookingInternal(UUID id) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        checkBookingAccess(booking, getCurrentUser());

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        addStatusHistory(booking, oldStatus, "Booking cancelled by user");
        return bookingRepository.save(booking);
    }

    public List<TimelineItemResponse> getTimeline(UUID id) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        checkBookingAccess(booking, getCurrentUser());
        List<TimelineItemResponse> items = new ArrayList<>();
        String[] titles = { "Menu Preparation", "Logistics Planning", "Final Check", "Delivery & Setup" };
        String[] descs = { "Prepare menu items and kitchen schedule", "Coordinate delivery and venue logistics",
                "Finalize details before service", "Setup and serve the event" };
        for (int i = 0; i < titles.length; i++) {
            items.add(TimelineItemResponse.builder()
                    .title(titles[i])
                    .description(descs[i])
                    .status(booking.getStatus().name())
                    .scheduledAt(LocalDateTime.now().plusDays(i + 1))
                    .completedAt(i < 2 ? LocalDateTime.now().minusDays(1) : null)
                    .build());
        }
        return items;
    }

    public List<Object> getHistory(UUID id) {
        CateringBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        return statusHistoryRepository.findByBookingOrderByChangedAtAsc(booking).stream().map(entry -> (Object) Map.of(
                "oldStatus", entry.getOldStatus() != null ? entry.getOldStatus().name() : "",
                "newStatus", entry.getNewStatus() != null ? entry.getNewStatus().name() : "",
                "changedBy", entry.getChangedBy() != null ? entry.getChangedBy().getEmail() : "system",
                "changedAt", entry.getChangedAt(),
                "comment", entry.getComment() != null ? entry.getComment() : "")).toList();
    }

    // =========================================================================
    // INQUIRIES & SEARCH
    // =========================================================================

    public Page<Object> getInquiries(Pageable pageable) {
        return inquiryRepository.findAllByOrderByCreatedAtDesc(pageable).map(inquiry -> Map.of(
                "id", inquiry.getId().toString(),
                "name", inquiry.getName(),
                "email", inquiry.getEmail(),
                "phone", inquiry.getPhone() != null ? inquiry.getPhone() : "",
                "eventType", inquiry.getEventType() != null ? inquiry.getEventType() : "",
                "status", inquiry.getStatus().name(),
                "createdAt", inquiry.getCreatedAt()));
    }

    public Object getInquiry(UUID id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found: " + id));
    }

    @Transactional
    public Object markInquiryRead(UUID id) {
        CateringInquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found: " + id));
        inquiry.setStatus(InquiryStatus.READ);
        inquiry.setReadAt(LocalDateTime.now());
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public Object updateInquiryStatus(UUID id, String status) {
        CateringInquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found: " + id));
        inquiry.setStatus(InquiryStatus.valueOf(status.toUpperCase()));
        return inquiryRepository.save(inquiry);
    }

    public Object search(String q) {
        String query = q.trim();
        List<CateringBooking> bookings = bookingRepository.findAll().stream()
                .filter(b -> (b.getEventName() != null && b.getEventName().toLowerCase().contains(query.toLowerCase()))
                        || (b.getBookingCode() != null && b.getBookingCode().toLowerCase().contains(query.toLowerCase()))
                        || (b.getEventType() != null && b.getEventType().getName() != null && b.getEventType().getName().toLowerCase().contains(query.toLowerCase())))
                .limit(10)
                .toList();

        List<CateringInquiry> inquiries = inquiryRepository.findAll().stream()
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(query.toLowerCase()))
                        || (i.getEmail() != null && i.getEmail().toLowerCase().contains(query.toLowerCase()))
                        || (i.getEventType() != null && i.getEventType().toLowerCase().contains(query.toLowerCase())))
                .limit(10)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("bookings", bookings.stream().map(cateringMapper::toBookingListItem).toList());
        result.put("inquiries", inquiries);
        return result;
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Authentication required");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void checkBookingAccess(CateringBooking booking, User currentUser) {
        if (booking.getCreatedBy() != null && !booking.getCreatedBy().getId().equals(currentUser.getId())) {
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName() == RoleName.ADMIN);
            if (!isAdmin) {
                throw new AccessDeniedException("You do not have permission to modify this booking");
            }
        }
    }

    private String generateBookingCode() {
        String code;
        do {
            int random = new Random().nextInt(9000) + 1000;
            code = "CH-" + random;
        } while (bookingRepository.existsByBookingCode(code));
        return code;
    }

    private void addStatusHistory(CateringBooking booking, BookingStatus oldStatus, String comment) {
        User currentUser = null;
        try {
            currentUser = getCurrentUser();
        } catch (Exception e) {
            log.debug("No authenticated user found while recording status history: {}", e.getMessage());
        }

        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setOldStatus(oldStatus);
        history.setNewStatus(booking.getStatus());
        history.setChangedBy(currentUser != null ? currentUser : booking.getCreatedBy());
        history.setChangedAt(LocalDateTime.now());
        history.setComment(comment);
        statusHistoryRepository.save(history);
        booking.getStatusHistory().add(history);
    }
}
