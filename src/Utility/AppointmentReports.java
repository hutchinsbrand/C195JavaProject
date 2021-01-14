package Utility;

import Models.Appointment;
import javafx.collections.ObservableList;

//The functional interface for report lambdas
public interface AppointmentReports {
    ObservableList<Appointment> generateReport();
}