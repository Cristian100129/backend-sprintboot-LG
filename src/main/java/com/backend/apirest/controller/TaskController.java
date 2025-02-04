package com.backend.apirest.controller;

import com.backend.apirest.squema.task;
import com.backend.apirest.squema.TaskStatus;
import com.backend.apirest.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    // utilizo Autowired para la inyeccion de dependencias
    @Autowired
    private TaskRepository taskRepository;

    // Endpoint para crear una nueva tarea
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody task nuevaTarea) {
        // Imprime la tarea recibida para depuración
        System.out.println("Recibiendo tarea: " + nuevaTarea);

        try {
            // Validación: verificar que 'title' y 'description' no sean nulos
            if (nuevaTarea.getTitle() == null || nuevaTarea.getDescription() == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "error", "message", "Datos incompletos"));
            }

            // Si no se especifica el estado, se asigna por defecto INICIADA
            if (nuevaTarea.getStatus() == null) {
                nuevaTarea.setStatus(TaskStatus.INICIADA);
            }

            // Guarda la tarea en la base de datos
            task savedTask = taskRepository.save(nuevaTarea);

            // Devuelve la respuesta con status 201 (CREATED) y la tarea guardada
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("status", "success", "task", savedTask));

        } catch (Exception e) {
            // En caso de error, devuelve status 500 y un mensaje
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al crear la tarea"));
        }
    }

    // Endpoint para listar todas las tareas o filtrarlas por estado (ordenadas de
    // más nuevas a más antiguas)
    @GetMapping("/searchAll")
    public ResponseEntity<Map<String, Object>> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        try {
            List<task> tasks;
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            if (status != null) {
                tasks = taskRepository.findByStatus(status, sort);
            } else {
                tasks = taskRepository.findAll(sort);
            }

            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "No se encontraron tareas"));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "success", "tareas", tasks));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al obtener tareas"));
        }
    }

    // Endpoint para buscar tareas cuyo título contenga cierta palabra
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByTitle(@RequestParam String keyword) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            List<task> tasks = taskRepository.findByTitleContaining(keyword, sort);
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message",
                                "No se encontraron tareas con el título que contenga: " + keyword));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "success", "tareas", tasks));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al buscar tareas por título"));
        }
    }

    // Endpoint para buscar tareas por estado y título
    @GetMapping("/search/advanced")
    public ResponseEntity<Map<String, Object>> searchByStatusAndTitle(@RequestParam TaskStatus status,
            @RequestParam String keyword) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            List<task> tasks = taskRepository.findByStatusAndTitleContaining(status, keyword, sort);
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message",
                                "No se encontraron tareas con estado " + status + " y que contengan: " + keyword));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "success", "tareas", tasks));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al buscar tareas avanzadas"));
        }
    }

    // Endpoint para obtener tareas filtradas solo por estado
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTasksByStatus(@RequestParam TaskStatus status) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            List<task> tasks = taskRepository.findByStatus(status, sort);
            if (tasks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message",
                                "No se encontraron tareas con el estado: " + status));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "success", "tareas", tasks));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al obtener tareas por estado"));
        }
    }

    // Endpoint para obtener una tarea por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable String id) {
        try {
            Optional<task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "Tarea no encontrada"));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "success", "task", optionalTask.get()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al obtener la tarea"));
        }
    }

    // Endpoint para actualizar una tarea existente
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable String id, @RequestBody task taskDetails) {
        try {
            Optional<task> optionalTask = taskRepository.findById(id);
            if (optionalTask.isPresent()) {
                task existingTask = optionalTask.get();
                existingTask.setTitle(taskDetails.getTitle());
                existingTask.setDescription(taskDetails.getDescription());
                existingTask.setStatus(taskDetails.getStatus());
                taskRepository.save(existingTask);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("status", "success", "task", existingTask));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "Tarea no encontrada"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al actualizar la tarea"));
        }
    }

    // Endpoint para eliminar una tarea
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable String id) {
        try {
            if (!taskRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "Tarea no encontrada"));
            }

            taskRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("status", "success", "message", "Tarea eliminada"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error al eliminar la tarea"));
        }
    }
}
