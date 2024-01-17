package com.dev.station.controller.sidebar;

import com.dev.station.controller.forms.AddProgramFormController;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.LaunchManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.ScriptsModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ScriptsController {
    private final LaunchManager launchManager = new LaunchManager(new NotificationManager(LanguageManager.getResourceBundle()));
    private final Map<String, ProcessHolder> processHolders = new HashMap<>();
    @FXML private VBox programsContainer;
    @FXML public Button addProgramButton;
    ResourceBundle bundle;
    private ScriptsModel scriptsModel = new ScriptsModel();
    @FXML private TableView<ProgramData> programsTable;
    @FXML private TableColumn<ProgramData, Number> numberColumn;
    @FXML private TableColumn<ProgramData, String> nameColumn;
    @FXML private TableColumn<ProgramData, String> pathColumn;
    @FXML private TableColumn<ProgramData, ProgramData> actionColumn;
    @FXML private TableColumn<ProgramData, Void> editColumn;
    @FXML private TableColumn<ProgramData, Void> deleteColumn;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();

        numberColumn.setCellFactory(col -> new TableCell<ProgramData, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(Integer.toString(getIndex() + 1));
                }
            }
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("programName"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("programPath"));
        actionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        actionColumn.setCellFactory(col -> new TableCell<ProgramData, ProgramData>() {
            private final Button actionButton = new Button();

            {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/crud/start-30.png")));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                actionButton.setGraphic(icon);
            }

            @Override
            protected void updateItem(ProgramData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                    actionButton.setOnAction(event -> {
                        launchProgram(item);
                    });
                }
            }
        });

        editColumn.setCellFactory(col -> new TableCell<ProgramData, Void>() {
            private final Button editButton = new Button();

            {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/crud/edit-64.png")));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                editButton.setGraphic(icon);
                editButton.setOnAction(event -> {
                    ProgramData data = getTableView().getItems().get(getIndex());
                    handleEdit(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        deleteColumn.setCellFactory(col -> new TableCell<ProgramData, Void>() {
            private final Button deleteButton = new Button();

            {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/crud/delete-64.png")));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                deleteButton.setGraphic(icon);
                deleteButton.setOnAction(event -> {
                    ProgramData data = getTableView().getItems().get(getIndex());
                    handleDelete(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        setColumnWidths();
        loadSavedPrograms();
    }

    private void setColumnWidths() {
        numberColumn.setPrefWidth(30);
        nameColumn.setPrefWidth(100);
        pathColumn.setPrefWidth(250);
        actionColumn.setPrefWidth(60);
        editColumn.setPrefWidth(60);
        deleteColumn.setPrefWidth(60);
    }

    private void handleDelete(ProgramData programData) {
        // TODO: Logic for deleting the selected program
        // programsTable.getItems().remove(programData);
    }

    private void handleEdit(ProgramData programData) {
        // TODO: Logic for editing the selected program
    }

    private void loadSavedPrograms() {
        List<ProgramData> programDataList = scriptsModel.loadProgramData();
        programsTable.setItems(FXCollections.observableArrayList(programDataList));
    }

    @FXML private void handleAddProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/forms/AddProgramForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            AddProgramFormController addProgramFormController = loader.getController();
            addProgramFormController.setOnSave(programData -> {
                addProgramToTable(programData);
                saveProgramData(programData);
            });

            Stage stage = new Stage();
            stage.setTitle(getTranslate("scriptsAdditionFormTitle"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProgramToTable(ProgramData programData) {
        programsTable.getItems().add(programData);
    }

    private void saveProgramData(ProgramData programData) {
        scriptsModel.saveProgramData(programData);
    }

    private void launchProgram(ProgramData programData) {
        String path = programData.getProgramPath();
        String fileExtension = programData.getProgramExtension();

        ProcessHolder processHolder = processHolders.computeIfAbsent(path, k -> new ProcessHolder());

        if ("exe".equalsIgnoreCase(fileExtension)) {
            launchManager.launchApplication(path, path, processHolder);
        } else if ("jar".equalsIgnoreCase(fileExtension)) {
            launchManager.launchJarApplication(path, path, processHolder);
        }
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}