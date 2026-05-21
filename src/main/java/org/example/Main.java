package org.example;

// Importación JavaFX
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Importación JDBC
import java.sql.*;

// Clase principal JavaFX
public class Main extends Application {

    // Tabla donde se muestran los empleados
    private TableView<Empleado> tableView;

    // Lista observable (se sincroniza con la UI)
    private ObservableList<Empleado> empleados =
            FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle(
                "Ejemplo JDBC con filtro y eliminación"
        );

        // Crear TableView
        tableView = new TableView<>();

        // Columna nombre
        TableColumn<Empleado, String> nombreCol =
                new TableColumn<>("Nombre");

        // Columna salario
        TableColumn<Empleado, Integer> salarioCol =
                new TableColumn<>("Salario");

        // Vincular columnas con getters del modelo
        nombreCol.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        salarioCol.setCellValueFactory(
                new PropertyValueFactory<>("salario")
        );

        // Añadir columnas a la tabla
        tableView.getColumns().addAll(
                nombreCol,
                salarioCol
        );

        // Campo de búsqueda
        TextField searchField = new TextField();

        searchField.setPromptText(
                "Buscar empleado por nombre..."
        );

        // Lista filtrada (envuelve la lista original)
        FilteredList<Empleado> filteredData =
                new FilteredList<>(empleados, p -> true);

        // Listener para filtrar en tiempo real
        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> {

            filteredData.setPredicate(empleado -> {

                // Si no hay texto, mostrar todo
                if (newValue == null ||
                        newValue.isEmpty()) {
                    return true;
                }

                // Comparación sin mayúsculas/minúsculas
                return empleado.getNombre()
                        .toLowerCase()
                        .contains(
                                newValue.toLowerCase()
                        );
            });
        });

        // Conectar tabla con datos filtrados
        tableView.setItems(filteredData);

        // Botón eliminar
        Button btnEliminar =
                new Button("Eliminar empleado");

        // Evento eliminar
        btnEliminar.setOnAction(
                e -> eliminarEmpleado()
        );

        // Layout principal
        VBox vbox =
                new VBox(
                        10,
                        searchField,
                        tableView,
                        btnEliminar
                );

        // Crear escena
        Scene scene =
                new Scene(vbox, 400, 350);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Cargar datos desde BD
        cargarDatos();
    }

    // =========================
    // CARGAR DATOS DESDE ORACLE
    // =========================
    private void cargarDatos() {

        String url =
                "jdbc:oracle:thin:@localhost:1521:xe";

        String user = "RIBERA";
        String password = "ribera";

        try (

                Connection conn =
                        DriverManager.getConnection(
                                url,
                                user,
                                password
                        );

                Statement stmt =
                        conn.createStatement();

                ResultSet rs =
                        stmt.executeQuery(
                        "SELECT nombre, salario FROM empleado2"
                        )

        ) {

            while (rs.next()) {

                empleados.add(
                        new Empleado(
                                rs.getString("nombre"),
                                rs.getInt("salario")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ELIMINAR EMPLEADO
    // =========================
    private void eliminarEmpleado() {

        // Obtener empleado seleccionado en la tabla
        Empleado seleccionado =
                tableView.getSelectionModel()
                        .getSelectedItem();

        // Si no hay selección
        if (seleccionado == null) {

            Alert alert =
                    new Alert(
                            Alert.AlertType.WARNING
                    );

            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Debes seleccionar un empleado"
            );

            alert.showAndWait();
            return;
        }

        String url =
                "jdbc:oracle:thin:@localhost:1521:xe";

        String user = "RIBERA";
        String password = "ribera";

        String sql =
                "DELETE FROM empleado2 WHERE nombre = ?";

        try (

                Connection conn =
                        DriverManager.getConnection(
                                url,
                                user,
                                password
                        );

                PreparedStatement ps =
                        conn.prepareStatement(sql)

        ) {

            // Asignar parámetro SQL
            ps.setString(
                    1,
                    seleccionado.getNombre()
            );

            int filas =
                    ps.executeUpdate();

            if (filas > 0) {

                // Eliminar de la lista visual
                empleados.remove(seleccionado);

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setTitle("Correcto");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Empleado eliminado correctamente"
                );

                alert.showAndWait();
            }

        } catch (SQLException e) {

            e.printStackTrace();

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Error al eliminar el empleado"
            );

            alert.showAndWait();
        }
    }

    // Método principal
    public static void main(String[] args) {
        launch(args);
    }

    // =========================
    // MODELO EMPLEADO
    // =========================
    public static class Empleado {

        private final String nombre;
        private final int salario;

        public Empleado(
                String nombre,
                int salario
        ) {
            this.nombre = nombre;
            this.salario = salario;
        }

        public String getNombre() {
            return nombre;
        }

        public int getSalario() {
            return salario;
        }
    }
}
