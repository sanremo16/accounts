package org.san.home.accounts.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * @author sanremo16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AccountDto extends RepresentationModel<AccountDto> {
    @ApiModelProperty(notes = "The database generated account ID")
    private Long id;
    @ApiModelProperty(notes = "Account number")
    @Pattern(regexp = "(\\d)*")
    private String num;
    @ApiModelProperty(notes = "Currency type by ISO code")
    private String currencyType;
    @Valid
    @ApiModelProperty(notes = "Account balance")
    private MoneyDto balance;
}
