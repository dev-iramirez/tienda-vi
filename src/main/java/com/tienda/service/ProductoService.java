package com.tienda.service;


import com.tienda.domain.Producto;
import com.tienda.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {
    //Se enlza el repositorio de producto
    private final ProductoRepository productoRepository;
    //Se hace uso del repositorio firebaseStorageRepository
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    //Recupera en un ArrayList todos los registros de categoría -o sólo activos-
    @Transactional(readOnly=true)
    public List<Producto> getProductos(boolean activo) {
        if (activo){ //Solo quiero las productos activas
            return productoRepository.findByActivoTrue();
        }

        return productoRepository.findAll();
    }
    
     //Recupera en un registro de categoría -si existe-
    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }
    
     //Si producto, trae un idCategoría... se actualiza el registro, sino se crea
    @Transactional
    public  void save (Producto producto, MultipartFile imagenFile) {
        productoRepository.save(producto);
        if (!imagenFile.isEmpty()) { //Nos pasan una imagen...
            try {
                String ruta = firebaseStorageService.uploadImage(
                    imagenFile,
                    "producto", producto.getIdProducto());
                producto.setRutaImagen(ruta);
                productoRepository.save(producto);
            } catch (IOException e){
            }
        }
    }
    //Si idProducto, se elimina... si no tiene productos asociados
    @Transactional
    public  void delete (Integer idProducto) {
        //Se valida que la categoría exista...
        if (!productoRepository.existsById(idProducto)) {
           //Se lanza una excepcion para indicarle al usuario que no se eliminó
           throw new IllegalArgumentException("La producto con ID "+idProducto+" no existe");
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la producto, tiene productos asociados");
        }
    }
    
    //Metodo de servicio para la consulta derivada
      @Transactional(readOnly=true)
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }
    
    //Metodo de servicio para la consulta JPQL
      @Transactional(readOnly=true)
    public List<Producto> consultaJPQL(double precioInf, double precioSup) {
        return productoRepository.consultaJPQL(precioInf, precioSup);
    }
    
    //Metodo de servicio para la consulta SQL
      @Transactional(readOnly=true)
    public List<Producto> consultaSQL(double precioInf, double precioSup) {
        return productoRepository.consultaSQL(precioInf, precioSup);
    }
    
}
