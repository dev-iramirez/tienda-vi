package com.tienda.repository;

import com.tienda.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{
    
    //Se crea una consulta derivada para recuperar los registros de categorias activas...
    public List<Categoria> findByActivoTrue();
}
