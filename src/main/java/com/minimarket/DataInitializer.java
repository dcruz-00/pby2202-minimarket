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
        crearProducto("Mantequilla (200 g)", 1290.0, 45, lacteos);
        crearProducto("Helado de vainilla (1 L)", 2490.0, 30, lacteos);

        Producto detergente = crearProducto("Detergente en polvo (1 kg)", 2190.0, 55, limpieza);
        crearProducto("Cloro líquido (1 L)", 890.0, 70, limpieza);
        crearProducto("Esponja de cocina (x2)", 490.0, 90, limpieza);

        Producto shampoo = crearProducto("Shampoo (400 ml)", 3290.0, 40, cuidado);
        crearProducto("Jabón de tocador (x3)", 990.0, 85, cuidado);
        crearProducto("Pasta de dientes (90 ml)", 990.0, 75, cuidado);

        // INVENTARIO
        registrarMovimiento(arroz, 100, "Entrada");
        registrarMovimiento(arroz, 20, "Salida");
        registrarMovimiento(cocacola, 150, "Entrada");
        registrarMovimiento(cocacola, 50, "Salida");
        registrarMovimiento(leche, 120, "Entrada");
        registrarMovimiento(leche, 10, "Salida");
        registrarMovimiento(detergente, 80, "Entrada");
        registrarMovimiento(detergente, 25, "Salida");
        registrarMovimiento(shampoo, 60, "Entrada");
    }

    private void crearUsuario(String username, String password, Set<Rol> roles,
            UsuarioRepository repo, PasswordEncoder encoder) {
        repo.findByUsername(username).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(encoder.encode(password));
            u.setRoles(roles);
            return repo.save(u);
        });
    }

    private Categoria crearCategoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return categoriaRepository.save(c);
    }

    private Producto crearProducto(String nombre, Double precio, Integer stock, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setCategoria(categoria);
        return productoRepository.save(p);
    }

    private void registrarMovimiento(Producto producto, Integer cantidad, String tipo) {
        Inventario inv = new Inventario();
        inv.setProducto(producto);
        inv.setCantidad(cantidad);
        inv.setTipoMovimiento(tipo);
        inv.setFechaMovimiento(new Date());
        inventarioRepository.save(inv);
    }
}