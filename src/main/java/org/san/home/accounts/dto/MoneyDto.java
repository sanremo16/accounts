package org.san.home.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MoneyDto extends RepresentationModel<MoneyDto> {
    @Schema(description = "Major balance value")
    @PositiveOrZero
    private Integer major;
    @Schema(description = "Minor balance value")
    @PositiveOrZero
    private Integer minor;
}
