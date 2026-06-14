package com.minimarket;

import java.util.Date;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public DataInitializer(RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public void run(String... args) {

        // ROLES
        Rol gerente = rolRepository.findByNombre("ROLE_GERENTE").orElseGet(() -> {
            Rol r = new Rol();
            r.setNombre("ROLE_GERENTE");
            return rolRepository.save(r);
        });
        Rol empleado = rolRepository.findByNombre("ROLE_EMPLEADO").orElseGet(() -> {
            Rol r = new Rol();
            r.setNombre("ROLE_EMPLEADO");
            return rolRepository.save(r);
        });
        Rol cliente = rolRepository.findByNombre("ROLE_CLIENTE").orElseGet(() -> {
            Rol r = new Rol();
            r.setNombre("ROLE_CLIENTE");
            return rolRepository.save(r);
        });

        // USUARIOS
        crearUsuario("admin", "123456", Set.of(gerente), usuarioRepository, passwordEncoder);
        crearUsuario("jperez", "123456", Set.of(empleado), usuarioRepository, passwordEncoder);
        crearUsuario("mgarcia", "123456", Set.of(empleado), usuarioRepository, passwordEncoder);
        crearUsuario("cliente1", "123456", Set.of(cliente), usuarioRepository, passwordEncoder);
        crearUsuario("cliente2", "123456", Set.of(cliente), usuarioRepository, passwordEncoder);
        crearUsuario("cliente3", "123456", Set.of(cliente), usuarioRepository, passwordEncoder);

        // CATEGORÍAS
        if (categoriaRepository.count() > 0)
            return;

        Categoria abarrotes = crearCategoria("Abarrotes");
        Categoria bebidas = crearCategoria("Bebidas");
        Categoria lacteos = crearCategoria("Lácteos y Congelados");
        Categoria limpieza = crearCategoria("Artículos de Limpieza");
        Categoria cuidado = crearCategoria("Cuidado Personal");

        // PRODUCTOS
        Producto arroz = crearProducto("Arroz Grado 1 (1 kg)", 990.0, 80, abarrotes);
        crearProducto("Lentejas (500 g)", 750.0, 60, abarrotes);
        crearProducto("Atún en conserva", 890.0, 120, abarrotes);
        crearProducto("Aceite vegetal (1 L)", 1490.0, 50, abarrotes);
        crearProducto("Fideos spaghetti (400 g)", 650.0, 90, abarrotes);

        Producto cocacola = crearProducto("Coca-Cola (1.5 L)", 1290.0, 100, bebidas);
        crearProducto("Agua mineral (1.5 L)", 590.0, 150, bebidas);
        crearProducto("Jugo de naranja (1 L)", 1190.0, 70, bebidas);
        crearProducto("Cerveza lata (350 ml)", 890.0, 200, bebidas);

        Producto leche = crearProducto("Leche entera (1 L)", 990.0, 110, lacteos);
        crearProducto("Yogurt frutado (150 g)", 450.0, 80, lacteos);
        crearProducto("Mantequilla (200 g)",