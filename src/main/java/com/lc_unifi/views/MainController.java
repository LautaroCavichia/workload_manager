package com.lc_unifi.views;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import com.lc_unifi.services.Scheduler;
import com.lc_unifi.models.*;
import com.lc_unifi.enums.SchedulePreset;
import javafx.collections.FXCollections;

import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class MainController {

    @FXML
    public ComboBox<Store> storeComboBox;
    @FXML
    public ComboBox<Integer> monthComboBox;
    @FXML
    public VBox workersVBox;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private ComboBox<SchedulePreset> presetComboBox;
    @FXML
    private Label coperturaLabel;

    private List<Store> allStores;
    private Store currentStore;
    private Map<Worker, Color> workerColorMap = new HashMap<>();

    public void initialize() {
        allStores = new ArrayList<>();
        currentStore = new Store();

        presetComboBox.setItems(FXCollections.observableArrayList(SchedulePreset.values()));
        presetComboBox.getSelectionModel().select(SchedulePreset.TWO_SHORT); // Default selection
        monthComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)); //TODO use enum for months
        monthComboBox.getSelectionModel().select(LocalDate.now().getMonthValue());
    }


    @FXML
    private void handleLoadData() {
        // Load data (stores, workers, etc.)
        // For simplicity, we'll create mock data here
        createMockData();
        showAlert("Data Loaded", "Mock data has been loaded successfully.");
    }

    @FXML
    private void handleGenerateSchedule() {
        if (currentStore == null || currentStore.getWorkers().isEmpty()) {
            showAlert("No Data", "Please load data before generating the schedule.");
            return;
        }
        int month = monthComboBox.getSelectionModel().getSelectedItem();
        int year = LocalDate.now().getYear();
        SchedulePreset selectedPreset = presetComboBox.getSelectionModel().getSelectedItem();
        Scheduler scheduler = new Scheduler(
            currentStore,
            allStores,
            selectedPreset,
            LocalDate.of(year, month, 1),
                LocalDate.of(year, month, 30),
            true // addThirdPersonOnWeekends
        );

        Map<LocalDate, List<Shift>> schedule = scheduler.generateSchedule();

        displaySchedule(schedule);
        displayWorkersHoursBank();
    }

    private void displaySchedule(Map<LocalDate, List<Shift>> schedule) {
        calendarGrid.getChildren().clear();
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        int month = monthComboBox.getSelectionModel().getSelectedItem();
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        buildCalendar(startDate, endDate, schedule);

    }

    private void displayWorkersHoursBank() {
    workersVBox.getChildren().clear();

    Label header = new Label("Workers' Hours Bank");
    header.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
    workersVBox.getChildren().add(header);

    for (Worker worker : currentStore.getWorkers()) {
        String workerName = worker.getFirstName() + " " + worker.getLastName();
        String hoursBank = "Hours Bank: " + worker.getHoursBank();
        Label workerLabel = new Label(workerName + " - " + hoursBank);
        workerLabel.setStyle("-fx-padding: 5;");
        workerLabel.setTextFill(Paint.valueOf(toRgbString(getColorForWorker(worker))));
        workerLabel.setAlignment(Pos.CENTER);

        workerLabel.setTextFill(getColorForWorker(worker));

        workersVBox.getChildren().add(workerLabel);
        int totalCoperturaHours = currentStore.getTotalAvailableHours();
        coperturaLabel.setText("Total Copertura Hours: " + totalCoperturaHours);
    }
}

private void buildCalendar(LocalDate startDate, LocalDate endDate, Map<LocalDate, List<Shift>> schedule) {
    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    LocalDate firstMonday = startDate.with(java.time.DayOfWeek.MONDAY);
    LocalDate lastSunday = endDate.with(java.time.DayOfWeek.SUNDAY);
    int weeks = (int) java.time.temporal.ChronoUnit.WEEKS.between(firstMonday, lastSunday) + 1;

    for (int col = 0; col < 7; col++) {
        Label dayLabel = new Label(daysOfWeek[col]);
        dayLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        dayLabel.setAlignment(Pos.CENTER);
        dayLabel.setPadding(new Insets(5));
        calendarGrid.add(dayLabel, col + 1, 0);
    }

    LocalDate currentDate = firstMonday;
    for (int row = 0; row < weeks; row++) {
        Label weekLabel = new Label("Week " + (row + 1));
        weekLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        weekLabel.setAlignment(Pos.CENTER_LEFT);
        weekLabel.setPadding(new Insets(5));
        calendarGrid.add(weekLabel, 0, row + 1);

        for (int col = 0; col < 7; col++) {

            VBox dayCell = new VBox();
            dayCell.setSpacing(5);
            dayCell.setPadding(new Insets(5));
            dayCell.setStyle("-fx-border-color: lightgrey;");
            dayCell.setAlignment(Pos.CENTER);
            dayCell.setPrefWidth(250);
            dayCell.setPrefHeight(230);


            if (!currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)) {
                Label dateLabel = new Label(String.valueOf(currentDate.getDayOfMonth()));
                dateLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                dateLabel.setAlignment(Pos.CENTER);
                dateLabel.setTextAlignment(TextAlignment.CENTER);
                dayCell.getChildren().add(dateLabel);

                List<Shift> shifts = schedule.getOrDefault(currentDate, Collections.emptyList());

                for (Shift shift : shifts) {
                    String workerName;
                    int workerId;
                    if (shift.getAssignedWorker() != null) {
                        workerName = shift.getAssignedWorker().getFirstName() + " " + shift.getAssignedWorker().getLastName();
                        workerId = shift.getAssignedWorker().getId();
                    } else {
                        workerName = "Unassigned";
                        workerId = 0;
                    }

                    String shiftTime = shift.getStartTime() + " - " + shift.getEndTime();

                    Label shiftLabel = new Label(shiftTime + "\n" + workerName);
                    shiftLabel.setAlignment(Pos.CENTER);
                    VBox.setVgrow(shiftLabel, Priority.ALWAYS);
                    shiftLabel.setMaxWidth(Double.MAX_VALUE);
                    shiftLabel.setTextAlignment(TextAlignment.CENTER);
                    Color workerColor = getColorForWorker(shift.getAssignedWorker());
                    String colorString = toRgbString(workerColor);
                    shiftLabel.setStyle("-fx-background-color: " + colorString + "; -fx-padding: 3; -fx-border-radius: 5; -fx-background-radius: 5;");
                    dayCell.getChildren().add(shiftLabel);
                }


                Set<Worker> assignedWorkers = new HashSet<>();
                for (Shift shift : shifts) {
                    if (shift.getAssignedWorker() != null) {
                        assignedWorkers.add(shift.getAssignedWorker());
                    }
                }
                Set<Worker> freeWorkers = new HashSet<>(currentStore.getWorkers());
                freeWorkers.removeAll(assignedWorkers);
                for (Worker freeWorker : freeWorkers) {
                    Label freeLabel = new Label(freeWorker.getFirstName() + " " + freeWorker.getLastName() + " (Free)");
                    freeLabel.setStyle("-fx-background-color: #dd6060 ; -fx-padding: 3; -fx-border-radius: 5; -fx-background-radius: 5;");
                    dayCell.getChildren().add(freeLabel);
                }
            }


            calendarGrid.add(dayCell, col + 1, row + 1);


            currentDate = currentDate.plusDays(1);
        }
    }
}

private Color getColorForWorker(Worker worker) {
    if (workerColorMap.containsKey(worker)) {
        return workerColorMap.get(worker);
    } else {
        if (worker == null) {
            return Color.LIGHTGRAY;
        }
        String lastName = worker.getLastName();
        int hash = Math.abs(lastName.hashCode());
        double hue = hash % 360;
        Color color = Color.hsb(hue, 0.5, 0.8);
        workerColorMap.put(worker, color);
        return color;
    }
}

    private void createMockData() {
        Worker worker1 = new Worker("Raffaella", "Lo Cicero", 40);
        Worker worker2 = new Worker("Fatima", "Idrissi", 30);
        Worker worker3 = new Worker("Davide", "Giugliotta", 35);
        Worker worker4 = new Worker("Salvatore", "Larocca", 30);
        Worker copertura1 = new Worker("Copertura", "Needed", 30);

        currentStore.setStoreId("S001");
        currentStore.setOpeningTime(LocalTime.of(9, 30));
        currentStore.setClosingTime(LocalTime.of(21, 0));
        currentStore.setWorkers(new LinkedList<>(Arrays.asList(worker1, worker2, worker3, worker4)));

        Store store2 = new Store();
        store2.setStoreId("S002");
        store2.setName("Second Store");
        store2.setOpeningTime(LocalTime.of(9, 30));
        store2.setClosingTime(LocalTime.of(21, 0));
        store2.setWorkers(new LinkedList<>(List.of(copertura1)));

        boolean addThirdPersonOnWeekends = true;

        allStores.add(currentStore);
        allStores.add(store2);
    }

    private String toRgbString(Color c) {
    return String.format("rgb(%d, %d, %d)",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255));
}

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}