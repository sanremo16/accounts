package org.san.home.accounts.dto;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "Major balance value")
    @PositiveOrZero
    private Integer major;
    @ApiModelProperty(notes = "Minor balance value")
    @PositiveOrZero
    private Integer minor;
}
