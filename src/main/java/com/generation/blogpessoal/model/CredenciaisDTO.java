// src/main/java/com/generation/blogpessoal/model/CredenciaisDTO.java

package com.generation.blogpessoal.model;

import jakarta.validation.constraints.NotBlank;

public class CredenciaisDTO {

    @NotBlank(message = "O campo usuário é obrigatório.")
    private String usuario;

    @NotBlank(message = "O campo senha é obrigatório.")
    private String senha;

    // Getters e Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
