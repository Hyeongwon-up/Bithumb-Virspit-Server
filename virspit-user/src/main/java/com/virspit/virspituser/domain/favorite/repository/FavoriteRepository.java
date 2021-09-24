package com.virspit.virspituser.domain.favorite.repository;

import com.virspit.virspituser.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Favorite findFavoriteByMemberIdAndProductId(Long memberId, Long productId);

    List<Favorite> findAllByMemberId(Long memberId);
}
