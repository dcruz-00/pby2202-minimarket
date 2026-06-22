package com.minimarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;

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

    @Test
    void testFindAll() {
        List<Inventario> lista = List.of(new Inventario());
        when(inventarioRepository.findAll()).thenReturn(lista);
        assertEquals(lista, inventarioService.findAll());
    }

    @Test
    void testFindById() {
        Inventario inventario = new Inventario();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        assertEquals(inventario, inventarioService.findById(1L));
    }

    @Test
    void testDeleteById() {
        inventarioService.deleteById(1L);
        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByProductoId() {
        List<Inventario> lista = List.of(new Inventario());
        when(inventarioRepository.findByProductoId(1L)).thenReturn(lista);
        assertEquals(lista, inventarioService.findByProductoId(1L));
    }
}
