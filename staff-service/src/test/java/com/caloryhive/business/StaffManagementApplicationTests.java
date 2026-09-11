package com.caloryhive.business;

import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.ShiftRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;
import com.caloryhive.business.staff.entity.enums.ShiftCategory;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import com.caloryhive.business.staff.entity.enums.ShiftType;
import com.caloryhive.business.staff.entity.enums.StaffStatus;
import com.caloryhive.business.staff.repository.ShiftRepository;
import com.caloryhive.business.staff.repository.ShiftRequestRepository;
import com.caloryhive.business.staff.repository.StaffRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class StaffManagementApplicationTests {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftRequestRepository shiftRequestRepository;

    @Test
    @DisplayName("Context loads and JPA repositories perform CRUD successfully")
    void testEntityLifecycleAndRelationships() {
        // 1. Create Staff
        Staff staff = Staff.builder()
                .name("Jane Smith")
                .role("Head Chef")
                .status(StaffStatus.CLOCKED_IN)
                .performance(BigDecimal.valueOf(4.90))
                .email("jane.smith@caloryhive.com")
                .stationArea("Kitchen")
                .avatarInitials("JS")
                .build();

        Staff savedStaff = staffRepository.save(staff);
        assertNotNull(savedStaff.getId());
        assertNotNull(savedStaff.getCreatedAt());
        assertEquals("Jane Smith", savedStaff.getName());
        assertEquals(StaffStatus.CLOCKED_IN, savedStaff.getStatus());

        // 2. Create Shift
        Shift shift = Shift.builder()
                .staff(savedStaff)
                .shiftDate(LocalDate.of(2023, 10, 23))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .shiftRole("Head Chef")
                .stationArea("Prep")
                .shiftType(ShiftType.REGULAR)
                .shiftCategory(ShiftCategory.MORNING)
                .status(ShiftStatus.PUBLISHED)
                .notes("Morning kitchen prep shift")
                .build();

        Shift savedShift = shiftRepository.save(shift);
        assertNotNull(savedShift.getId());
        assertEquals(savedStaff.getId(), savedShift.getStaff().getId());
        assertEquals(8.0, savedShift.getDurationInHours());

        // 3. Test Overlap Query
        List<Shift> overlapping = shiftRepository.findOverlappingShifts(
                savedStaff.getId(),
                LocalDate.of(2023, 10, 23),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                null
        );
        assertEquals(1, overlapping.size());

        // 4. Create ShiftRequest
        ShiftRequest request = ShiftRequest.builder()
                .staff(savedStaff)
                .requestType(RequestType.TIME_OFF)
                .requestedDate(LocalDate.of(2023, 11, 2))
                .endDate(LocalDate.of(2023, 11, 4))
                .reason("Requesting Nov 2nd - Nov 4th off for family event.")
                .status(RequestStatus.PENDING)
                .build();

        ShiftRequest savedRequest = shiftRequestRepository.save(request);
        assertNotNull(savedRequest.getId());
        assertEquals(RequestType.TIME_OFF, savedRequest.getRequestType());
        assertEquals(RequestStatus.PENDING, savedRequest.getStatus());

        // 5. Verification counts
        long totalStaff = staffRepository.countByActiveTrue();
        assertTrue(totalStaff >= 1);
        long pendingRequests = shiftRequestRepository.countByStatus(RequestStatus.PENDING);
        assertTrue(pendingRequests >= 1);
    }
}
