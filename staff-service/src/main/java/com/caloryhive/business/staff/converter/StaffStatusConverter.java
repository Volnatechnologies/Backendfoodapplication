package com.caloryhive.business.staff.converter;

import com.caloryhive.business.staff.entity.enums.StaffStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StaffStatusConverter implements Converter<String, StaffStatus> {

    @Override
    public StaffStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        String normalized = source.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        for (StaffStatus status : StaffStatus.values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown staff status: " + source);
    }
}
