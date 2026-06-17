    package com.gkev.InvoicingSystem.models.entity;

    import lombok.Data;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.relational.core.mapping.Column;
    import org.springframework.data.relational.core.mapping.Table;

    import java.time.LocalDateTime;
    import java.util.UUID;

    @Table("users")
    @Data
    public class UsersEntity {
        @Id
        private UUID id;
        @Column("user_no")
        private String userNo;
       @Column("first_name")
        private String firstName;
       @Column("last_name")
        private String lastName;
       @Column("phone_number")
       private String phoneNumber;

      private String email;
      private String password;
      private Boolean disabled;

      @Column("account_non_expired")
        private Boolean accountNonExpired;

      @Column("account_non_locked")
        private Boolean accountNonLocked;

      @Column("created_at")
        private LocalDateTime createdAt;


    }

