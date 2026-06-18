package com.minimarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private Producto producto;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    /**
     * Stock suficiente: el producto tiene stock >= cantidad solicitada,
     * por lo que el carrito debe persistirse correctamente.
     */
    @Test
    public void testAgregarProductoConStockSuficiente() {
        // Arrange
        when(producto.getStock()).thenReturn(10);

        Carrito carrito = new Carrito();
        carrito.setProducto(producto);
        carrito.setCantidad(5);

        when(carritoRepository.save(carrito)).thenReturn(carrito);

        // Act
        Carrito resultado = carritoService.agregarProducto(carrito);

        // Assert
        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        verify(carritoRepository, times(1)).save(carrito);
    }

    /**
     * Stock insuficiente: la cantidad solicitada supera el stock disponible,
     * por lo que debe lanzarse IllegalStateException y NO persistirse el carrito.
     */
    @Test
    public void testAgregarProductoConStockInsuficienteLanzaExcepcion() {
        // Arrange
        when(producto.getStock()).thenReturn(2);

        Carrito carrito = new Carrito();
        carrito.setProducto(producto);
        carrito.setCantidad(5);

        // Act & Assert
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarProducto(carrito));

        assertEquals("Stock insuficiente para el producto: " + producto.getNombre(),
                ex.getMessage());
        // El repositorio nunca debe invocarse si la validación falla
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    /**
     * Relación Producto-Usuario: el usuario asociado al carrito persistido
     * debe ser exactamente el que se agregó (el correcto).
     */
    @Test
    public void testAgregarProductoMantieneUsuarioCorrecto() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente");

        Producto productoReal = new Producto();
        productoReal.setNombre("Arroz Grado 1");
        productoReal.setStock(50);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(productoReal);
        carrito.setCantidad(3);

        // El repositorio devuelve el mismo carrito que recibe
        when(carritoRepository.save(carrito)).thenReturn(carrito);

        // Act
        Carrito resultado = carritoService.agregarProducto(carrito);

        // Assert
        assertNotNull(resultado.getUsuario());
        assertSame(usuario, resultado.getUsuario());
        assertEquals("cliente", resultado.getUsuario().getUsername());
        assertEquals(1L, resultado.getUsuario().getId());
    }

    /**
     * Producto nulo: si el carrito no tiene producto asociado,
     * debe lanzarse IllegalArgumentException y NO persistirse nada.
     */
    @Test
    public void testAgregarProductoNuloLanzaExcepcion() {
        // Arrange
        Carrito carrito = new Carrito();
        carrito.setProducto(null);
        carrito.setCantidad(1);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(carrito));

        assertEquals("El producto no puede ser nulo", ex.getMessage());
        verify(carritoRepository, never()).save(any(Carrito.class));
    }
}