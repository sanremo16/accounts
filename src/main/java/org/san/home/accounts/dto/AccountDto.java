package org.san.home.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "The database generated account ID")
    private Long id;
    @Schema(description = "Account number")
    @Pattern(regexp = "(\\d)*")
    private String num;
    @Schema(description = "Currency type by ISO code")
    private String currencyType;
    @Valid
    @Schema(description = "Account balance")
    private MoneyDto balance;
}
