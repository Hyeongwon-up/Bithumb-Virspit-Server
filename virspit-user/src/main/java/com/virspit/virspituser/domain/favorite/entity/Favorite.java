package com.virspit.virspituser.domain.favorite.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.virspit.virspituser.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "member_id")
    private Member member;

    private Long productId;

    public Favorite(Member member, Long productId) {
        this.member = member;
        this.productId = productId;
    }
}
