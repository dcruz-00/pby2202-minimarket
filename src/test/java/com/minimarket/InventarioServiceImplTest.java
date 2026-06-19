package com.minimarket;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Inventario inventarioValido;
    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");

        inventarioValido = new Inventario();
        inventarioValido.setProducto(producto);
        inventarioValido.setCantidad(10);
        inventarioValido.setTipoMovimiento("Entrada");
    }

    @Test
    void saveDeberiaGuardarCuandoElMovimientoEsValido() {
        when(inventarioRepository.save(inventarioValido)).thenReturn(inventarioValido);

        Inventario resultado = inventarioService.save(inventarioValido);

        assertNotNull(resultado);
        assertEquals("Entrada", resultado.getTipoMovimiento());
        assertEquals(10, resultado.getCantidad());
        verify(inventarioRepository).save(inventarioValido);
    }

    @Test
    void saveDeberiaConservarElProductoCorrecto() {
        when(inventarioRepository.save(inventarioValido)).thenReturn(inventarioValido);

        Inventario resultado = inventarioService.save(inventarioValido);

        assertEquals(producto, resultado.getProducto());
        assertEquals("Arroz", resultado.getProducto().getNombre());
    }

    @Test
    void saveDeberiaLanzarExcepcionCuandoElProductoEsNulo() {
        inventarioValido.setProducto(null);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.save(inventarioValido));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void saveDeberiaLanzarExcepcionCuandoTipoMovimientoEsNulo() {
        inventarioValido.setTipoMovimiento(null);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.save(inventarioValido));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void saveDeberiaLanzarExcepcionCuandoTipoMovimientoEsVacio() {
        inventarioValido.setTipoMovimiento("   ");

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.save(inventarioValido));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void saveDeberiaLanzarExcepcionCuandoCantidadEsNula() {
        inventarioValido.setCantidad(null);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.save(inventarioValido));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void saveDeberiaLanzarExcepcionCuandoCantidadNoEsPositiva() {
        inventarioValido.setCantidad(0);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.save(inventarioValido));
        verify(inventarioRepository, never()).save(any());
    }
}
