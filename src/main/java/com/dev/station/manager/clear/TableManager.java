package com.dev.station.manager.clear;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableManager {

    public void setupTable(TableColumn<PathData, Number> numberColumn, TableColumn<PathData, String> nameColumn, TableColumn<PathData, String> pathColumn, TableColumn<PathData, String> exclusionsColumn,
                           TableColumn<PathData, Void> editColumn,
                           TableColumn<PathData, Void> deleteColumn,
                           TableView<PathData> pathsTable) {

        numberColumn.setCellFactory(col -> new TableCell<PathData, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        exclusionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getExclusions())));

        double numberColumnWidth = pathsTable.widthProperty().multiply(0.05).doubleValue();
        numberColumn.prefWidthProperty().bind(pathsTable.widthProperty().multiply(0.05));

        int numDivide = 5;
        nameColumn.prefWidthProperty().bind(pathsTable.widthProperty().subtract(numberColumnWidth).divide(numDivide));
        pathColumn.prefWidthProperty().bind(pathsTable.widthProperty().subtract(numberColumnWidth).divide(numDivide));
        exclusionsColumn.prefWidthProperty().bind(pathsTable.widthProperty().subtract(numberColumnWidth).divide(numDivide));
        editColumn.prefWidthProperty().bind(pathsTable.widthProperty().multiply(0.17));
        deleteColumn.prefWidthProperty().bind(pathsTable.widthProperty().multiply(0.17));

        //pathsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}