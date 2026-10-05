package com.universidad.tareas.controller;

import com.universidad.tareas.model.Prioridad;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService servicio;

    // Inyección por constructor: se evita @Autowired sobre el campo porque
    // acopla la clase al contenedor de Spring y obliga a levantar el
    // contexto completo para poder probarla; con un único constructor,
    // Spring inyecta la dependencia automáticamente (implícito desde la
    // versión 4.3) y la clase también puede instanciarse a mano en un test
    // unitario pasando un TareaService de prueba.
    public TareaController(TareaService servicio) {
        this.servicio = servicio;
    }

    // GET /tareas?prioridad=ALTA&completada=false → lista filtrada
    @GetMapping
    public String listar(
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Boolean completada,
            Model model) {
        model.addAttribute("tareas", servicio.filtrar(prioridad, completada));
        model.addAttribute("prioridades", Prioridad.values());
        model.addAttribute("prioridadSeleccionada", prioridad);
        model.addAttribute("completadaSeleccionada", completada);
        return "tareas/lista";
    }

    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("prioridades", Prioridad.values());
        model.addAttribute("accion", "Crear");
        return "tareas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Tarea tarea = servicio.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada: " + id));
        model.addAttribute("tarea", tarea);
        model.addAttribute("prioridades", Prioridad.values());
        model.addAttribute("accion", "Editar");
        return "tareas/formulario";
    }

    // POST /tareas/guardar → valida con @Valid; si hay errores, vuelve al formulario (PRG solo en éxito)
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("tarea") Tarea tarea,
            BindingResult resultado,
            Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("prioridades", Prioridad.values());
            model.addAttribute("accion", tarea.getId() == null ? "Crear" : "Editar");
            return "tareas/formulario";
        }
        servicio.guardar(tarea);
        return "redirect:/tareas";
    }

    // POST en lugar de GET: completar y eliminar modifican estado del
    // servidor. Una petición GET debe ser segura (sin efectos secundarios);
    // de lo contrario, el prefetch del navegador o un rastreador web podría
    // completar o borrar tareas simplemente al visitar el enlace.
    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Long id) {
        servicio.marcarCompletada(id)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada: " + id));
        return "redirect:/tareas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return "redirect:/tareas";
    }
}
