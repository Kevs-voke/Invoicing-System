package com.gkev.InvoicingSystem.models.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user_with_roles")
@Data
public class UserWithRolesEntity {
    @Id
    private Long id;
    @Column("role_id")
    private Long roleId;
    @Column("user_id")
    private UUID userId;
}
