
package com.denkitronik.clienteservice.domain.services;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import com.denkitronik.clienteservice.domain.exception.ClienteNotFoundException;
import com.denkitronik.clienteservice.domain.exception.ClienteServiceException;
import com.denkitronik.clienteservice.domain.repositories.IClienteDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de ClienteServiceImpl")
class ClienteServiceImplTest {

    @Mock
    private IClienteDao clienteDao;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private Region region;

    @BeforeEach
    void setUp() {
        region = new Region();
        region.setId(1L);
        region.setNombre("Sudamérica");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@kernel.org");
        cliente.setRegion(region);
    }

    @Test
    @DisplayName("Busca un cliente existente por ID")
    void findByIdClienteExistente() {
        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.findById(1L);

        assertThat(resultado).isEqualTo(cliente);
        verify(clienteDao).findById(1L);
    }

    @Test
    @DisplayName("Lanza excepción si el cliente no existe")
    void findByIdClienteInexistente() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.findById(99L))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(clienteDao).findById(99L);
    }

    @Test
    @DisplayName("Convierte el error de datos duplicados al guardar")
    void saveDatosDuplicados() {
        when(clienteDao.save(cliente))
                .thenThrow(new DataIntegrityViolationException("Correo duplicado"));

        assertThatThrownBy(() -> clienteService.save(cliente))
                .isInstanceOf(ClienteServiceException.class)
                .hasMessageContaining("Error al guardar el cliente");

        verify(clienteDao).save(cliente);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar un cliente inexistente")
    void deleteClienteInexistente() {
        when(clienteDao.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.delete(99L))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(clienteDao).existsById(99L);
        verify(clienteDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Actualiza un cliente existente")
    void updateClienteExistente() {
        Cliente datosActualizados = new Cliente();
        datosActualizados.setNombre("Linus");
        datosActualizados.setApellido("Torvalds");
        datosActualizados.setEmail("linus.nuevo@kernel.org");
        datosActualizados.setRegion(region);

        when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteDao.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.update(1L, datosActualizados);

        assertThat(resultado.getEmail()).isEqualTo("linus.nuevo@kernel.org");
        verify(clienteDao).findById(1L);
        verify(clienteDao).save(cliente);
    }

    @Test
    @DisplayName("No actualiza un cliente inexistente")
    void updateClienteInexistente() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.update(99L, cliente))
                .isInstanceOf(ClienteNotFoundException.class);

        verify(clienteDao).findById(99L);
        verify(clienteDao, never()).save(any(Cliente.class));
    }
    @Test
@DisplayName("Lista todos los clientes")
void findAllClientes() {
    when(clienteDao.findAll()).thenReturn(java.util.List.of(cliente));

    var resultado = clienteService.findAll();

    assertThat(resultado).hasSize(1);
    assertThat(resultado.get(0)).isEqualTo(cliente);
    verify(clienteDao).findAll();
}

@Test
@DisplayName("Lista todas las regiones")
void findAllRegiones() {
    when(clienteDao.findAllRegiones()).thenReturn(java.util.List.of(region));

    var resultado = clienteService.findAllRegiones();

    assertThat(resultado).hasSize(1);
    assertThat(resultado.get(0)).isEqualTo(region);
    verify(clienteDao).findAllRegiones();
}

@Test
@DisplayName("Guarda un cliente correctamente")
void saveClienteExitoso() {
    when(clienteDao.save(cliente)).thenReturn(cliente);

    Cliente resultado = clienteService.save(cliente);

    assertThat(resultado).isEqualTo(cliente);
    verify(clienteDao).save(cliente);
}

@Test
@DisplayName("Elimina un cliente existente")
void deleteClienteExistente() {
    when(clienteDao.existsById(1L)).thenReturn(true);

    clienteService.delete(1L);

    verify(clienteDao).existsById(1L);
    verify(clienteDao).deleteById(1L);
}

@Test
@DisplayName("Actualiza la foto del cliente")
void updateClienteActualizaFoto() {
    Cliente datosActualizados = new Cliente();
    datosActualizados.setNombre("Linus");
    datosActualizados.setApellido("Torvalds");
    datosActualizados.setEmail("linus@kernel.org");
    datosActualizados.setFoto("foto-actualizada.jpg");
    datosActualizados.setRegion(region);

    when(clienteDao.findById(1L)).thenReturn(Optional.of(cliente));
    when(clienteDao.save(cliente)).thenReturn(cliente);

    Cliente resultado = clienteService.update(1L, datosActualizados);

    assertThat(resultado.getFoto()).isEqualTo("foto-actualizada.jpg");
    verify(clienteDao).save(cliente);
}
}
