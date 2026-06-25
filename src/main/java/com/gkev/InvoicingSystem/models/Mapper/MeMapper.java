package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.MeDTO;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;

public class MeMapper {
  public static MeDTO toMeDto(UsersEntity user) {
      return new MeDTO(
              user.getFirstName(),
              user.getLastName(),
              user.getEmail(),
              user.getPhoneNumber(),
              user.getUserNo()
      );
  }
}
