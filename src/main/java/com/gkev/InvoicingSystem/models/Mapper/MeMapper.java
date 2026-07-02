package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.MeDTO;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;
import org.springframework.stereotype.Component;

@Component
public class MeMapper {
  public static MeDTO toMeDto(UsersEntity user) {
      return new MeDTO(
              user.getFirstName(),
              user.getLastName(),
              user.getPhoneNumber(),
              user.getEmail(),
              user.getUserNo()
      );
  }
}
