package com.example.biblioteca_horizonte.controller;

import com.example.biblioteca_horizonte.model.Equipo;
import com.example.biblioteca_horizonte.model.Reserva;
import com.example.biblioteca_horizonte.model.Usuario;
import com.example.biblioteca_horizonte.repository.EquipoRepository;
import com.example.biblioteca_horizonte.repository.ReservaRepository;
import com.example.biblioteca_horizonte.repository.UsuarioRepository;
import com.example.biblioteca_horizonte.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite que React se conecte sin problemas de CORS
public class BibliotecaController {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaService reservaService;

    // 1. Obtener equipos activos (proyectores y notebooks)
    @GetMapping("/equipos")
    public List<Equipo> listarEquipos() {
        return equipoRepository.findByActivoTrue();
    }

    // 2. Login simple
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoElectronico(email);
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
            return ResponseEntity.ok(usuarioOpt.get());
        }
        return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
    }

    // 3. Listar reservas (si pasan docenteId, trae solo las de ese docente; si no, todas)
    @GetMapping("/reservas")
    public List<Reserva> listarReservas(@RequestParam(required = false) Long docenteId) {
        if (docenteId != null) {
            return reservaRepository.findByDocenteId(docenteId);
        }
        return reservaService.obtenerTodas();
    }

    // 4. Crear una nueva solicitud de reserva
    @PostMapping("/reservas")
    public Reserva crearReserva(@RequestBody Reserva reserva) {
        return reservaService.crearReserva(reserva);
    }

    // 5. Confirmar reserva (Rol Bibliotecaria - con validación de unicidad)
    @PatchMapping("/reservas/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
        try {
            Reserva actualizada = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6. Rechazar reserva (Rol Bibliotecaria)
    @PatchMapping("/reservas/{id}/rechazar")
    public Reserva rechazarReserva(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String motivo = body.get("motivo");
        return reservaService.rechazarReserva(id, motivo);
    }
}