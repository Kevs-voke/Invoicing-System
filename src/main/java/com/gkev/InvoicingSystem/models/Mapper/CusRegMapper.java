package com.gkev.InvoicingSystem.models.Mapper;

import com.gkev.InvoicingSystem.models.DTO.CusRegDTO;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;
import org.springframework.stereotype.Component;

@Component
public class CusRegMapper {

    public UsersEntity toUserEntity(CusRegDTO cusRegDTO) {
        UsersEntity user = new UsersEntity();
        user.setEmail(cusRegDTO.email());
        user.setFirstName(cusRegDTO.firstName());
        user.setLastName(cusRegDTO.lastName());
        user.setPhoneNumber(cusRegDTO.phoneNumber());
        return user;
    }
}
