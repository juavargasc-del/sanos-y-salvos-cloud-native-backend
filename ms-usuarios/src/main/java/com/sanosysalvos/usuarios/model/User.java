package com.sanosysalvos.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_usuarios_external_identity",
        columnNames = {"external_tenant_id", "external_object_id"}
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Email(message = "El correo debe ser válido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol;

    @Column(name = "external_tenant_id")
    private String externalTenantId;

    @Column(name = "external_object_id")
    private String externalObjectId;

    @PrePersist
    void asignarRolPorDefecto() {
        if (rol == null || rol.isBlank()) {
            rol = "USUARIO";
        }
    }
}