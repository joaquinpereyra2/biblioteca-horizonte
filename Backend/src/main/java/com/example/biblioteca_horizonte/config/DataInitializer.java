package com.example.biblioteca_horizonte.config;

import com.example.biblioteca_horizonte.model.Equipo;
import com.example.biblioteca_horizonte.model.Usuario;
import com.example.biblioteca_horizonte.model.enums.RolUsuario;
import com.example.biblioteca_horizonte.model.enums.TipoEquipo;
import com.example.biblioteca_horizonte.repository.EquipoRepository;
import com.example.biblioteca_horizonte.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepo, EquipoRepository equipoRepo) {
        return args -> {
            // Crear a Lucía (Bibliotecaria)
            if (usuarioRepo.findByCorreoElectronico("lucia@horizonte.com").isEmpty()) {
                Usuario lucia = new Usuario();
                lucia.setNombre("Lucía");
                lucia.setCorreoElectronico("lucia@horizonte.com");
                lucia.setPassword("1234");
                lucia.setRol(RolUsuario.BIBLIOTECARIA);
                usuarioRepo.save(lucia);
            }

            // Crear un Docente de prueba
            if (usuarioRepo.findByCorreoElectronico("docente@horizonte.com").isEmpty()) {
                Usuario docente = new Usuario();
                docente.setNombre("Juan Docente");
                docente.setLegajo("DOC-999");
                docente.setCorreoElectronico("docente@horizonte.com");
                docente.setPassword("1234");
                docente.setRol(RolUsuario.DOCENTE);
                usuarioRepo.save(docente);
            }

            // Crear los equipos de la biblioteca (2 proyectores, 4 notebooks)
            if (equipoRepo.count() == 0) {
                Equipo p1 = new Equipo(); p1.setNombre("Proyector 1"); p1.setTipo(TipoEquipo.PROYECTOR); equipoRepo.save(p1);
                Equipo p2 = new Equipo(); p2.setNombre("Proyector 2"); p2.setTipo(TipoEquipo.PROYECTOR); equipoRepo.save(p2);
                Equipo n1 = new Equipo(); n1.setNombre("Notebook 1"); n1.setTipo(TipoEquipo.NOTEBOOK); equipoRepo.save(n1);
                Equipo n2 = new Equipo(); n2.setNombre("Notebook 2"); n2.setTipo(TipoEquipo.NOTEBOOK); equipoRepo.save(n2);
                Equipo n3 = new Equipo(); n3.setNombre("Notebook 3"); n3.setTipo(TipoEquipo.NOTEBOOK); equipoRepo.save(n3);
                Equipo n4 = new Equipo(); n4.setNombre("Notebook 4"); n4.setTipo(TipoEquipo.NOTEBOOK); equipoRepo.save(n4);
            }
        };
    }
}