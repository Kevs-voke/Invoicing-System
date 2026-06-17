package com.gkev.InvoicingSystem.models.DTO;

import com.gkev.InvoicingSystem.models.entity.RolesEntity;
import com.gkev.InvoicingSystem.models.entity.UsersEntity;

import java.util.List;

public record UserWithRolesDTO(UsersEntity user, List<RolesEntity> roles) {
}