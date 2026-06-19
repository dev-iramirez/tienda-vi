package com.tienda.service;


import com.tienda.domain.Categoria;
import com.tienda.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {
    //Se enlza el repositorio de categoria
    private final CategoriaRepository categoriaRepository;
    //Se hace uso del repositorio firebaseStorageRepository
    private final FirebaseStorageService firebaseStorageService;

    public CategoriaService(CategoriaRepository categoriaRepository, FirebaseStorageService firebaseStorageService) {
        this.categoriaRepository = categoriaRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    //Recupera en un ArrayList todos los registros de categoría -o sólo activos-
    @Transactional(readOnly=true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo){ //Solo quiero las categorias activas
            return categoriaRepository.findByActivoTrue();
        }

        return categoriaRepository.findAll();
    }
    
     //Recupera en un registro de categoría -si existe-
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }
    
     //Si categoria, trae un idCategoría... se actualiza el registro, sino se crea
    @Transactional
    public  void save (Categoria categoria, MultipartFile imagenFile) {
        categoriaRepository.save(categoria);
        if (!imagenFile.isEmpty()) { //Nos pasan una imagen...
            try {
                String ruta = firebaseStorageService.uploadImage(
                    imagenFile,
                    "categoria", categoria.getIdCategoria());
                categoria.setRutaImagen(ruta);
                categoriaRepository.save(categoria);
            } catch (IOException e){
            }
        }
    }
    //Si idCategoria, se elimina... si no tiene productos asociados
    @Transactional
    public  void delete (Integer idCategoria) {
        //Se valida que la categoría exista...
        if (!categoriaRepository.existsById(idCategoria)) {
           //Se lanza una excepcion para indicarle al usuario que no se eliminó
           throw new IllegalArgumentException("La categoria con ID "+idCategoria+" no existe");
        }
        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la categoria, tiene productos asociados");
        }
    }
}
