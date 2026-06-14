package com.minimarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;

@ExtendWith(MockitoExtension.class)
public class VentaTest {

    @Mock
    private VentaRepository ventaRepository;

    @Test
    public void testVentaVinculadaAUsuarioValido() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente");
        usuario.setPassword("password123");

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());

        // Act & Assert
        assertNotNull(venta.getUsuario());
        assertEquals("cliente", venta.getUsuario().getUsername());
    }

    @Test
    public void testDetallesVentaVinculadosAProductos() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz Grado 1");
        producto.setPrecio(990.0);
        producto.setStock(50);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(2);
        detalle.setPrecio(producto.getPrecio());
        detalle.setProducto(producto);

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setFecha(new Date());
        venta.setDetalles(List.of(detalle));

        // Act & Assert
        assertNotNull(venta.getDetalles());
        assertEquals(1, venta.getDetalles().size());
        assertEquals("Arroz Grado 1", venta.getDetalles().get(0).getProducto().getNombre());
    }

    @Test
    public void testTotalVentaCalculadoCorrectamente() {
        // Arrange
        DetalleVenta detalle1 = new DetalleVenta();
        detalle1.setCantidad(2);
        detalle1.setPrecio(990.0);

        DetalleVenta detalle2 = new DetalleVenta();
        detalle2.setCantidad(3);
        detalle2.setPrecio(750.0);

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setFecha(new Date());
        venta.setDetalles(List.of(detalle1, detalle2));

        // Act
        double total = venta.getDetalles().stream()
                .mapToDouble(d -> d.getPrecio() * d.getCantidad())
                .sum();

        // Assert
        assertEquals(4230.0, total);
    }

    @Test
    public void testVentaNoRegistradaSinStockSuficiente() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(800.0);
        producto.setStock(2);

        int cantidadSolicitada = 5;

        // Act
        boolean stockSuficiente = producto.getStock() >= cantidadSolicitada;

        // Assert
        assertFalse(stockSuficiente, "No debería permitirse la venta si el stock es insuficiente");
    }

    @Test
    public void testBuscarVentaPorIdConMock() {
        // Arrange
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setFecha(new Date());

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        // Act
        Optional<Venta> resultado = ventaRepository.findById(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(ventaRepository, times(1)).findById(1L);
    }
}