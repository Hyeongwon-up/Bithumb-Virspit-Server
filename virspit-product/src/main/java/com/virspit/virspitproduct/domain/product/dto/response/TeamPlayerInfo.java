package com.virspit.virspitproduct.domain.product.dto.response;

import com.virspit.virspitproduct.domain.teamplayer.entity.TeamPlayerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApiModel(description = "팀/플레이어 정보")
@AllArgsConstructor
@Getter
public class TeamPlayerInfo {
    @ApiModelProperty("팀/플레이어 ID")
    private Long id;

    @ApiModelProperty("팀/플레이어 이름")
    private String name;

    private TeamPlayerType type;
}
