package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito findById(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public List<Carrito> findByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Carrito agregarProducto(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito no puede ser nulo");
        }

        Producto producto = carrito.getProducto();
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        Integer cantidad = carrito.getCantidad();
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Integer stockDisponible = producto.getStock();
        if (stockDisponible == null || stockDisponible < cantidad) {
            throw new IllegalStateException(
                    "Stock insuficiente para el producto: " + producto.getNombre());
        }

        return carritoRepository.save(carrito);
    }
}