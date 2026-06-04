package com.minimarket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Rol gerente = rolRepository.findByNombre("ROLE_GERENTE").orElseGet(() -> {
            Rol nuevoRolGerente = new Rol();
            nuevoRolGerente.setNombre("ROLE_GERENTE");
            return rolRepository.save(nuevoRolGerente);
        });
        
        Rol empleado = rolRepository.findByNombre("ROLE_EMPLEADO").orElseGet(() -> {
            Rol nuevoRolEmpleado = new Rol();
            nuevoRolEmpleado.setNombre("ROLE_EMPLEADO");
            return rolRepository.save(nuevoRolEmpleado);
        });

        Rol cliente = rolRepository.findByNombre("ROLE_CLIENTE").orElseGet(() -> {
            Rol nuevoRolCliente = new Rol();
            nuevoRolCliente.setNombre("ROLE_CLIENTE");
            return rolRepository.save(nuevoRolCliente);
        });

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setUsername("admin");
        usuarioAdmin.setPassword("123456");
        usuarioAdmin.setPassword(passwordEncoder.encode(usuarioAdmin.getPassword()));
        usuarioAdmin.setRoles(Set.of(gerente));
        usuarioRepository.findByUsername("admin").orElseGet(() -> {
            return usuarioRepository.save(usuarioAdmin);
        });

        Usuario usuarioEmpleado = new Usuario();
        usuarioEmpleado.setUsername("empleado");
        usuarioEmpleado.setPassword("123456");
        usuarioEmpleado.setPassword(passwordEncoder.encode(usuarioEmpleado.getPassword()));
        usuarioEmpleado.setRoles(Set.of(empleado));
        usuarioRepository.findByUsername("empleado").orElseGet(() -> {
            return usuarioRepository.save(usuarioEmpleado);
        });

        Usuario usuarioCliente = new Usuario();
        usuarioCliente.setUsername("cliente");
        usuarioCliente.setPassword("123456");
        usuarioCliente.setPassword(passwordEncoder.encode(usuarioCliente.getPassword()));
        usuarioCliente.setRoles(Set.of(cliente));
        usuarioRepository.findByUsername("cliente").orElseGet(() -> {
            return usuarioRepository.save(usuarioCliente);
        });

    }

}
