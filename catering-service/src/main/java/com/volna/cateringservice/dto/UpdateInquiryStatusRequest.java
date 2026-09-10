package com.volna.cateringservice.dto;

import com.volna.cateringservice.entity.CateringInquiryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInquiryStatusRequest {

    @NotNull
    private CateringInquiryStatus status;
}
