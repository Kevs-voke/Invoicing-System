package com.gkev.InvoicingSystem.models.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
@Data
public class RolesEntity {
    @Id
    private Long id;
    @Column("role_name")
    private String roleName;
}

