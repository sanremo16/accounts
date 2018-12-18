package org.san.home.accounts.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MoneyDto {
    @ApiModelProperty(notes = "Major balance value")
    private Integer major;
    @ApiModelProperty(notes = "Minor balance value")
    private Integer minor;
}
