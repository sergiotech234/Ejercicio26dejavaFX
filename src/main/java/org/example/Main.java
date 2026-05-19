package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    private TableView<Empleado> tableView;
    private ObservableList<Empleado> empleados = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Ejemplo JDBC con filtro y eliminación");

        tableView = new TableView<>();

        // Columnas
        TableColumn<Empleado, String> nombreCol = new TableColumn<>("Nombre");
        TableColumn<Empleado, Integer> salarioCol = new TableColumn<>("Salario");

        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        salarioCol.setCellValueFactory(new PropertyValueFactory<>("salario"));

        tableView.getColumns().addAll(nombreCol, salarioCol);

        // 🔎 Campo de búsqueda
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar empleado por nombre...");

        FilteredList<Empleado> filteredData = new FilteredList<>(empleados, p -> true);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filteredData.setPredicate(empleado -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return empleado.getNombre().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        tableView.setItems(filteredData);

        // 🗑️ BOTÓN ELIMINAR
        Button btnEliminar = new Button("Eliminar empleado");
        btnEliminar.setOnAction(e -> eliminarEmpleado());

        VBox vbox = new VBox(10, searchField, tableView, btnEliminar);
        Scene scene = new Scene(vbox, 400, 350);

        primaryStage.setScene(scene);
        primaryStage.show();

        cargarDatos();
    }

    // 📥 Cargar datos desde Oracle
    private void cargarDatos() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "RIBERA";
        String password = "ribera";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nombre, salario FROM empleado2")) {

            while (rs.next()) {
                empleados.add(new Empleado(
                        rs.getString("nombre"),
                        rs.getInt("salario")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🗑️ ELIMINAR EMPLEADO
    private void eliminarEmpleado() {

        Empleado seleccionado = tableView.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Debes seleccionar un empleado");
            alert.showAndWait();
            return;
        }

        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "RIBERA";
        String password = "ribera";

        String sql = "DELETE FROM empleado2 WHERE nombre = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, seleccionado.getNombre());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                empleados.remove(seleccionado);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Correcto");
                alert.setHeaderText(null);
                alert.setContentText("Empleado eliminado correctamente");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error al eliminar el empleado");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // 👤 Modelo Empleado
    public static class Empleado {
        private final String nombre;
        private final int salario;

        public Empleado(String nombre, int salario) {
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