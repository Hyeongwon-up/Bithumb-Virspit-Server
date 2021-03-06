package com.virspit.virspitorder.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long productId;

    @NotNull
    private LocalDateTime orderDate;

    private String tokenId;

    private String memberAddress; // 지갑 주소

    private String memo;

    public Orders(Long memberId, Long productId, String memberAddress, String tokenId) {
        this.memberId = memberId;
        this.productId = productId;
        this.memberAddress = memberAddress;
        this.orderDate = LocalDateTime.now();
        this.tokenId = tokenId;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
