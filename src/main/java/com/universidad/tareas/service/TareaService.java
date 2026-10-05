package com.universidad.tareas.service;

import com.universidad.tareas.model.Prioridad;
import com.universidad.tareas.model.Tarea;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service  // Spring gestiona esta instancia como singleton: la misma
          // instancia se inyecta en TareaController y en TareaApiController
public class TareaService {

    private final Map<Long, Tarea> tareas = new LinkedHashMap<>();
    private Long contadorId = 1L;

    public TareaService() {
        // Datos de ejemplo para arrancar
        guardar(new Tarea(null, "Configurar entorno Spring Boot", "Instalar JDK 17, Maven y el IDE",
                Prioridad.ALTA, LocalDate.now().plusDays(1), false));
        guardar(new Tarea(null, "Diseñar el modelo de dominio", "Definir clase Tarea y enum Prioridad",
                Prioridad.MEDIA, LocalDate.now().plusDays(3), false));
        guardar(new Tarea(null, "Escribir pruebas unitarias", "Cubrir TareaService con JUnit",
                Prioridad.BAJA, LocalDate.now().plusDays(7), true));
    }

    public List<Tarea> obtenerTodas() {
        return new ArrayList<>(tareas.values());
    }

    // Filtro combinable: si un parámetro es null, no se aplica esa condición
    public List<Tarea> filtrar(Prioridad prioridad, Boolean completada) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : tareas.values()) {
            boolean coincidePrioridad = (prioridad == null) || t.getPrioridad() == prioridad;
            boolean coincideEstado = (completada == null) || t.isCompletada() == completada;
            if (coincidePrioridad && coincideEstado) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public Optional<Tarea> buscarPorId(Long id) {
        return Optional.ofNullable(tareas.get(id));
    }

    public Tarea guardar(Tarea tarea) {
        if (tarea.getId() == null) {
            tarea.setId(contadorId++);
        }
        tareas.put(tarea.getId(), tarea);
        return tarea;
    }

    public Optional<Tarea> marcarCompletada(Long id) {
        Tarea tarea = tareas.get(id);
        if (tarea == null) {
            return Optional.empty();
        }
        tarea.setCompletada(true);
        return Optional.of(tarea);
    }

    public boolean eliminar(Long id) {
        return tareas.remove(id) != null;
    }
}
