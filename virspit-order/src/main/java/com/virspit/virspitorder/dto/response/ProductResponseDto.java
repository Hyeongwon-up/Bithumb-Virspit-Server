package com.virspit.virspitorder.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ProductResponseDto", description = "상품 응답 DTO")
@Getter
public class ProductResponseDto {
    @ApiModelProperty("상품 ID")
    private Long id;

    @ApiModelProperty("상품 제목")
    @NotBlank
    private String title;

    @ApiModelProperty("상품 설명")
    @NotBlank
    private String description;

    private SportsInfo sportsInfo;

    private String nftImageUrl;

    @ApiModelProperty("팀/플레이어 ID")
    @NotNull
    private TeamPlayerInfo teamPlayerInfo;

    @ApiModelProperty("상품 가격(Klay)")
    @NotNull
    @DecimalMin("1")
    private Integer price;

    @ApiModelProperty("상품 수량")
    @NotNull
    @DecimalMin("0")
    @DecimalMax("100")
    private Integer remainedCount;

    @ApiModelProperty(value = "상품 판매 시작 일", example = "2021-09-26 17:00:00")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @ApiModelProperty("상품 진열 여부")
    @NotNull
    private Boolean exhibition;

    @ApiModelProperty("상품 디테일 이미지 Url")
    private String detailImageUrl;

    @ApiModelProperty("상품 NFT 정보")
    @NotNull
    private NftInfo nftInfo;

}