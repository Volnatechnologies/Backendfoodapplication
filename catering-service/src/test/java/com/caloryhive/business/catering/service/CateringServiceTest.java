package com.caloryhive.business.catering.service;

import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.entity.*;
import com.caloryhive.business.catering.mapper.CateringMapper;
import com.caloryhive.business.catering.repository.*;
import com.caloryhive.business.common.enums.BookingStatus;
import com.caloryhive.business.common.enums.InquiryStatus;
import com.caloryhive.business.common.enums.PaymentType;
import com.caloryhive.business.common.enums.RoleName;
import com.caloryhive.business.notification.entity.Notification;
import com.caloryhive.business.notification.repository.NotificationRepository;
import com.caloryhive.business.payment.entity.Payment;
import com.caloryhive.business.payment.repository.PaymentRepository;
import com.caloryhive.business.user.entity.Role;
import com.caloryhive.business.user.entity.User;
import com.caloryhive.business.user.repository.RoleRepository;
import com.caloryhive.business.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CateringServiceTest {

    @Mock
    private CateringEventTypeRepository eventTypeRepository;
    @Mock
    private CateringMenuPackageRepository menuPackageRepository;
    @Mock
    private CateringCustomOptionRepository customOptionRepository;
    @Mock
    private CateringBookingRepository bookingRepository;
    @Mock
    private BookingCustomOptionRepository bookingCustomOptionRepository;
    @Mock
    private CateringInquiryRepository inquiryRepository;
    @Mock
    private BookingStatusHistoryRepository statusHistoryRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @Spy
    private PricingService pricingService = new PricingService();

    @Spy
    private CateringMapper cateringMapper = new CateringMapper();

    @InjectMocks
    private CateringService cateringService;

    private User testUser;
    private CateringEventType corporateEvent;
    private CateringMenuPackage premiumPlated;
    private CateringCustomOption liveCooking;
    private CateringCustomOption dessertBar;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("organizer@example.com");
        testUser.setFirstName("Event");
        testUser.setLastName("Organizer");
        Role role = new Role();
        role.setName(RoleName.BUSINESS_OWNER);
        testUser.setRoles(Set.of(role));

        corporateEvent = new CateringEventType();
        corporateEvent.setId(UUID.randomUUID());
        corporateEvent.setName("Corporate Event");
        corporateEvent.setActive(true);

        premiumPlated = new CateringMenuPackage();
        premiumPlated.setId(UUID.randomUUID());
        premiumPlated.setName("Premium Plated");
        premiumPlated.setPricePerGuest(new BigDecimal("90.00"));
        premiumPlated.setActive(true);

        liveCooking = new CateringCustomOption();
        liveCooking.setId(UUID.randomUUID());
        liveCooking.setName("Live Cooking Station");
        liveCooking.setPrice(new BigDecimal("400.00"));
        liveCooking.setActive(true);

        dessertBar = new CateringCustomOption();
        dessertBar.setId(UUID.randomUUID());
        dessertBar.setName("Premium Dessert Bar");
        dessertBar.setPrice(new BigDecimal("250.00"));
        dessertBar.setActive(true);
    }

    private void mockSecurityUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("organizer@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void shouldCalculatePricingMatchingUiScreenshot3Exactly() {
        // UI Screenshot 3 specs:
        // Package: Premium Plated ($90/guest) x 50 guests = $4,500.00
        // Custom Options: Live Cooking Station ($400) + Premium Dessert Bar ($250) = $650.00
        // Subtotal = $5,150.00
        // Service Fee (10%) = $515.00
        // Tax (8.25%) = $467.36
        // Final Total = $6,132.36
        // Deposit Required (30%) = $1,839.71

        when(menuPackageRepository.findById(premiumPlated.getId())).thenReturn(Optional.of(premiumPlated));
        when(customOptionRepository.findById(liveCooking.getId())).thenReturn(Optional.of(liveCooking));
        when(customOptionRepository.findById(dessertBar.getId())).thenReturn(Optional.of(dessertBar));

        PriceCalculationRequest request = PriceCalculationRequest.builder()
                .menuPackageId(premiumPlated.getId())
                .guestCount(50)
                .customOptionIds(List.of(liveCooking.getId(), dessertBar.getId()))
                .build();

        FinancialSummaryResponse response = cateringService.calculateBookingFinancialSummary(request);

        assertThat(response.getBasePrice()).isEqualByComparingTo("4500.00");
        assertThat(response.getCustomOptionsTotal()).isEqualByComparingTo("650.00");
        assertThat(response.getSubtotal()).isEqualByComparingTo("5150.00");
        assertThat(response.getServiceFee()).isEqualByComparingTo("515.00");
        assertThat(response.getTax()).isEqualByComparingTo("467.36");
        assertThat(response.getFinalTotal()).isEqualByComparingTo("6132.36");
        assertThat(response.getDepositRequired()).isEqualByComparingTo("1839.71");
    }

    @Test
    void shouldRejectZeroGuestCount() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventName("Corporate Gala");
        request.setGuestCount(0);

        assertThatThrownBy(() -> cateringService.createBooking(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Guest count must be greater than 0");
    }

    @Test
    void shouldRejectPastEventDate() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventName("Corporate Gala");
        request.setGuestCount(50);
        request.setEventDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> cateringService.createBooking(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event date cannot be in the past");
    }

    @Test
    void shouldCreateBookingWithPendingStatusAndCorrectPricing() {
        mockSecurityUser();

        when(eventTypeRepository.findById(corporateEvent.getId())).thenReturn(Optional.of(corporateEvent));
        when(menuPackageRepository.findById(premiumPlated.getId())).thenReturn(Optional.of(premiumPlated));
        when(customOptionRepository.findById(liveCooking.getId())).thenReturn(Optional.of(liveCooking));
        when(bookingRepository.save(any(CateringBooking.class))).thenAnswer(inv -> {
            CateringBooking b = inv.getArgument(0);
            if (b.getId() == null) {
                b.setId(UUID.randomUUID());
            }
            return b;
        });

        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventName("Tech Annual Gala");
        request.setEventTypeId(corporateEvent.getId());
        request.setEventDate(LocalDate.now().plusMonths(1));
        request.setEventTime(LocalTime.of(18, 30));
        request.setGuestCount(50);
        request.setVenueAddress("Silicon Tower, 5th Floor");
        request.setMenuPackageId(premiumPlated.getId());
        request.setCustomOptionIds(List.of(liveCooking.getId()));

        BookingDetailsResponse response = cateringService.createBookingDetails(request);

        assertThat(response).isNotNull();
        assertThat(response.getEventName()).isEqualTo("Tech Annual Gala");
        assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.getBookingCode()).startsWith("CH-");
        assertThat(response.getFinancials().getBasePrice()).isEqualByComparingTo("4500.00");
        assertThat(response.getFinancials().getCustomOptionsTotal()).isEqualByComparingTo("400.00");

        verify(statusHistoryRepository).save(any(BookingStatusHistory.class));
    }

    @Test
    void shouldSaveDraftBookingSuccessfully() {
        mockSecurityUser();

        when(eventTypeRepository.findById(corporateEvent.getId())).thenReturn(Optional.of(corporateEvent));
        when(menuPackageRepository.findById(premiumPlated.getId())).thenReturn(Optional.of(premiumPlated));
        when(bookingRepository.save(any(CateringBooking.class))).thenAnswer(inv -> {
            CateringBooking b = inv.getArgument(0);
            if (b.getId() == null) {
                b.setId(UUID.randomUUID());
            }
            return b;
        });

        DraftBookingRequest request = DraftBookingRequest.builder()
                .eventName("Draft Meeting")
                .eventTypeId(corporateEvent.getId())
                .menuPackageId(premiumPlated.getId())
                .guestCount(20)
                .build();

        BookingDetailsResponse response = cateringService.saveBookingAsDraft(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BookingStatus.DRAFT);
        assertThat(response.getBookingCode()).startsWith("CH-");
        assertThat(response.getFinancials().getBasePrice()).isEqualByComparingTo("1800.00");
    }

    @Test
    void shouldPreventUpdateOnConfirmedBooking() {
        CateringBooking confirmedBooking = new CateringBooking();
        confirmedBooking.setId(UUID.randomUUID());
        confirmedBooking.setStatus(BookingStatus.CONFIRMED);
        confirmedBooking.setCreatedBy(testUser);

        when(bookingRepository.findById(confirmedBooking.getId())).thenReturn(Optional.of(confirmedBooking));

        UpdateBookingRequest request = new UpdateBookingRequest();
        request.setEventName("Updated Name");

        assertThatThrownBy(() -> cateringService.updateBooking(confirmedBooking.getId(), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only draft or pending bookings can be updated");
    }

    @Test
    void shouldConfirmBookingAndCreateDepositPaymentAndNotification() {
        mockSecurityUser();

        CateringBooking booking = new CateringBooking();
        booking.setId(UUID.randomUUID());
        booking.setBookingCode("CH-8821");
        booking.setEventName("Executive Summit Dinner");
        booking.setEventType(corporateEvent);
        booking.setMenuPackage(premiumPlated);
        booking.setGuestCount(50);
        booking.setVenueAddress("742 Evergreen Terrace");
        booking.setEventDate(LocalDate.now().plusWeeks(2));
        booking.setEventTime(LocalTime.of(19, 0));
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedBy(testUser);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(CateringBooking.class))).thenAnswer(inv -> inv.getArgument(0));

        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .paymentReference("TXN-TEST-9988")
                .notes("Confirmed by executive assistant")
                .build();

        BookingConfirmationResponse response = cateringService.confirmBooking(booking.getId(), request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(response.getBookingCode()).isEqualTo("CH-8821");

        // Verify deposit payment was saved
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment capturedPayment = paymentCaptor.getValue();
        assertThat(capturedPayment.getPaymentType()).isEqualTo(PaymentType.DEPOSIT);
        assertThat(capturedPayment.getPaymentStatus()).isEqualTo(Payment.PaymentStatus.PENDING);
        assertThat(capturedPayment.getTransactionReference()).isEqualTo("TXN-TEST-9988");

        // Verify notification was dispatched
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notifCaptor.capture());
        assertThat(notifCaptor.getValue().getTitle()).isEqualTo("Booking Confirmed");
    }

    @Test
    void shouldRejectConfirmationIfVenueAddressIsMissing() {
        CateringBooking booking = new CateringBooking();
        booking.setId(UUID.randomUUID());
        booking.setStatus(BookingStatus.PENDING);
        booking.setVenueAddress("   "); // blank venue
        booking.setCreatedBy(testUser);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> cateringService.confirmBooking(booking.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Venue address must not be blank when confirming a booking");
    }

    @Test
    void shouldReturnDashboardMetricsCorrectly() {
        when(bookingRepository.countUpcomingEvents(any(), any())).thenReturn(14L);
        when(bookingRepository.sumTotalRevenueByStatusNotIn(any())).thenReturn(new BigDecimal("42500.00"));
        when(inquiryRepository.countByStatus(InquiryStatus.NEW)).thenReturn(6L);
        when(bookingRepository.findUpcomingBookings(any(), any(), any())).thenReturn(List.of());
        when(inquiryRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of());
        when(menuPackageRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(premiumPlated));

        DashboardResponse dashboard = cateringService.getDashboardStatistics();

        assertThat(dashboard.getUpcomingEvents()).isEqualTo(14L);
        assertThat(dashboard.getTotalRevenue()).isEqualByComparingTo("42500.00");
        assertThat(dashboard.getNewInquiries()).isEqualTo(6L);
        assertThat(dashboard.getMenuPackages()).hasSize(1);
    }
}
