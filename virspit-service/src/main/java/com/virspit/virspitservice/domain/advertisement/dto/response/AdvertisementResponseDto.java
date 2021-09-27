package com.virspit.virspitservice.domain.advertisement.dto.response;

import com.virspit.virspitservice.domain.advertisement.entity.AdvertisementDoc;
import com.virspit.virspitservice.domain.product.dto.ProductDto;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = {"id", "product", "description", "url"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class AdvertisementResponseDto {
    private String id;

    private ProductDto product;

    private String description;

    private String url;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    public static AdvertisementResponseDto entityToDto(AdvertisementDoc advertisement) {
        return AdvertisementResponseDto.builder()
                .id(advertisement.getId())
                .product(advertisement.getProduct() == null ? null : ProductDto.entityToDto(advertisement.getProduct()))
                .description(advertisement.getDescription())
                .createdDate(advertisement.getCreatedDate())
                .updatedDate(advertisement.getUpdatedDate())
                .build();
    }
}