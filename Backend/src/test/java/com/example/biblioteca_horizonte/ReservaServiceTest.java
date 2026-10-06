package com.example.biblioteca_horizonte;

import com.example.biblioteca_horizonte.exception.RecursoNoEncontradoException;
import com.example.biblioteca_horizonte.exception.ReglaNegocioException;
import com.example.biblioteca_horizonte.model.Equipo;
import com.example.biblioteca_horizonte.model.Reserva;
import com.example.biblioteca_horizonte.model.Usuario;
import com.example.biblioteca_horizonte.model.enums.EstadoReserva;
import com.example.biblioteca_horizonte.repository.EquipoRepository;
import com.example.biblioteca_horizonte.repository.ReservaRepository;
import com.example.biblioteca_horizonte.repository.UsuarioRepository;
import com.example.biblioteca_horizonte.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservaServiceTest {

    @Autowired private ReservaService reservaService;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private Equipo equipo;
    private Usuario docente;
    private final LocalDate fecha = LocalDate.now().plusDays(7);

    @BeforeEach
    void preparar() {
        equipo = equipoRepository.findByActivoTrue().get(0);
        docente = usuarioRepository.findByCorreoElectronico("docente@horizonte.com").orElseThrow();
    }

    private Reserva datos(String modulo, LocalDate fechaSolicitud, Long equipoId) {
        Equipo e = new Equipo();
        e.setId(equipoId);
        Usuario d = new Usuario();
        d.setId(docente.getId());
        Reserva r = new Reserva();
        r.setEquipo(e);
        r.setDocente(d);
        r.setFecha(fechaSolicitud);
        r.setModuloHorario(modulo);
        return r;
    }

    private Reserva solicitar(String modulo) {
        return reservaService.crearReserva(datos(modulo, fecha, equipo.getId()));
    }

    private EstadoReserva estadoDe(Reserva r) {
        return reservaRepository.findById(r.getId()).orElseThrow().getEstado();
    }

    @Test
    void crearSolicitudValidaQuedaPendiente() {
        assertEquals(EstadoReserva.PENDIENTE, solicitar("Módulo 1").getEstado());
    }

    @Test
    void dosSolicitudesPendientesParaLoMismoSonPermitidas() {
        Reserva a = solicitar("Módulo 1");
        Reserva b = solicitar("Módulo 1");
        assertEquals(EstadoReserva.PENDIENTE, estadoDe(a));
        assertEquals(EstadoReserva.PENDIENTE, estadoDe(b));
    }

    @Test
    void confirmarSolicitudPendientePasaAConfirmada() {
        Reserva a = solicitar("Módulo 1");
        reservaService.confirmarReserva(a.getId());
        assertEquals(EstadoReserva.CONFIRMADA, estadoDe(a));
    }

    @Test
    void noPermiteDosConfirmadasParaMismoEquipoFechaYModulo() {
        Reserva a = solicitar("Módulo 1");
        Reserva b = solicitar("Módulo 1");
        reservaService.confirmarReserva(a.getId());

        assertThrows(ReglaNegocioException.class, () -> reservaService.confirmarReserva(b.getId()));
        assertEquals(EstadoReserva.CONFIRMADA, estadoDe(a));
        assertEquals(EstadoReserva.PENDIENTE, estadoDe(b));
    }

    @Test
    void rechazarGuardaElMotivo() {
        Reserva a = solicitar("Módulo 2");
        Reserva rechazada = reservaService.rechazarReserva(a.getId(), "Equipo en reparación");
        assertEquals(EstadoReserva.RECHAZADA, rechazada.getEstado());
        assertEquals("Equipo en reparación", rechazada.getMotivoRechazo());
    }

    @Test
    void noSePuedeConfirmarUnaSolicitudRechazada() {
        Reserva a = solicitar("Módulo 3");
        reservaService.rechazarReserva(a.getId(), "No disponible");
        assertThrows(ReglaNegocioException.class, () -> reservaService.confirmarReserva(a.getId()));
        assertEquals(EstadoReserva.RECHAZADA, estadoDe(a));
    }

    @Test
    void noSePuedeRechazarUnaSolicitudConfirmada() {
        Reserva a = solicitar("Módulo 4");
        reservaService.confirmarReserva(a.getId());
        assertThrows(ReglaNegocioException.class, () -> reservaService.rechazarReserva(a.getId(), "x"));
        assertEquals(EstadoReserva.CONFIRMADA, estadoDe(a));
    }

    @Test
    void rechazarUnaReservaInexistenteDevuelveNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.rechazarReserva(999999L, "x"));
    }

    @Test
    void solicitudSinFechaEsRechazada() {
        assertThrows(ReglaNegocioException.class,
                () -> reservaService.crearReserva(datos("Módulo 1", null, equipo.getId())));
    }

    @Test
    void solicitudConFechaPasadaEsRechazada() {
        assertThrows(ReglaNegocioException.class,
                () -> reservaService.crearReserva(datos("Módulo 1", LocalDate.now().minusDays(1), equipo.getId())));
    }

    @Test
    void solicitudConModuloInvalidoEsRechazada() {
        assertThrows(ReglaNegocioException.class,
                () -> reservaService.crearReserva(datos("Módulo 9", fecha, equipo.getId())));
    }

    @Test
    void solicitudConEquipoInexistenteDevuelveNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class,
                () -> reservaService.crearReserva(datos("Módulo 1", fecha, 999999L)));
    }

    @Test
    void solicitudConEquipoInactivoEsRechazada() {
        equipo.setActivo(false);
        equipoRepository.save(equipo);
        assertThrows(ReglaNegocioException.class, () -> solicitar("Módulo 1"));
    }
}