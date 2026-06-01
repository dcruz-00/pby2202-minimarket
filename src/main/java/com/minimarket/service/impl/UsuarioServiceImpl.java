package com.minimarket.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    @PreAuthorize("hasRole('GERENTE')")
    public Usuario save(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @PreAuthorize("hasRole('GERENTE')")
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
