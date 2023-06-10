package com.company.dto.profile;

import com.company.enums.GeneralStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;
import java.time.LocalDateTime;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {

    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private Role role;
    private GeneralStatus status;
    private Boolean visible;
    private LocalDateTime createdDate;
}
