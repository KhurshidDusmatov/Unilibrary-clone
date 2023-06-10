package com.company.dto.profile;

import com.company.dto.attach.AttachResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProfileResponseDTO2 {
    private Integer id;
    private String name;
    private String surname;
    private AttachResponseDTO photo;
}
