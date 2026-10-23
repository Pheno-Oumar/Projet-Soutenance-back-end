package com.kadi_aon.mon_salon.salon.entity;

import com.kadi_aon.mon_salon.salon.enums.TypeRoleSalon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles_salon")
public class RoleSalon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private TypeRoleSalon role;

    public RoleSalon() {
    }

    public RoleSalon(TypeRoleSalon role) {
        this.role = role;
    }

    public RoleSalon(Long id, TypeRoleSalon role) {
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeRoleSalon getRole() {
        return role;
    }

    public void setRole(TypeRoleSalon role) {
        this.role = role;
    }

    public static RoleSalonBuilder builder() {
        return new RoleSalonBuilder();
    }

    public static class RoleSalonBuilder {
        private Long id;
        private TypeRoleSalon role;

        RoleSalonBuilder() {
        }

        public RoleSalonBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoleSalonBuilder role(TypeRoleSalon role) {
            this.role = role;
            return this;
        }

        public RoleSalon build() {
            return new RoleSalon(this.id, this.role);
        }
    }
}
