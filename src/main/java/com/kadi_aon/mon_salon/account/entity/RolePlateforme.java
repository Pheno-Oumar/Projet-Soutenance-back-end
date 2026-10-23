package com.kadi_aon.mon_salon.account.entity;

import com.kadi_aon.mon_salon.account.enums.TypeRolePlateforme;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles_plateforme")
public class RolePlateforme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private TypeRolePlateforme role;

    public RolePlateforme() {
    }

    public RolePlateforme(TypeRolePlateforme role) {
        this.role = role;
    }

    public RolePlateforme(Long id, TypeRolePlateforme role) {
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeRolePlateforme getRole() {
        return role;
    }

    public void setRole(TypeRolePlateforme role) {
        this.role = role;
    }

    public static RolePlateformeBuilder builder() {
        return new RolePlateformeBuilder();
    }

    public static class RolePlateformeBuilder {
        private Long id;
        private TypeRolePlateforme role;

        RolePlateformeBuilder() {
        }

        public RolePlateformeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RolePlateformeBuilder role(TypeRolePlateforme role) {
            this.role = role;
            return this;
        }

        public RolePlateforme build() {
            return new RolePlateforme(this.id, this.role);
        }
    }
}
