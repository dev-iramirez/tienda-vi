package com.tienda.repository;

import com.tienda.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{
    
    //Se crea una consulta derivada para recuperar los registros de productos activas...
    public List<Producto> findByActivoTrue();
    
    //Consulta derivada que obtiene los productos de un rango de precios ordenado por precio ASC
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(double precioInf, double precioSup);
    
    //Consulta JPQL que obtiene los productos de un rango de precios ordenado por precio ASC
    @Query(value="SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(double precioInf, double precioSup);
    
    //Consulta SQL que obtiene los productos de un rango de precios ordenado por precio ASC
    @Query(nativeQuery=true,
            value="SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(double precioInf, double precioSup);
    
}
