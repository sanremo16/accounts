package org.san.home.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author sanremo16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue()
    private Long id;

    @NotNull
    @Column(name="first_name")
    @Length(max=128)
    private String firstName;

    @Column(name="second_name")
    @Length(max=128)
    private String secondName;

    @NotNull
    @Column(name="last_name")
    @Length(max=128)
    private String lastName;


}
