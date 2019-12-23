package edu.kit.dol.timetabling.utilities;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import edu.kit.dol.timetabling.model.Agency;
import edu.kit.dol.timetabling.model.Agent;
import edu.kit.dol.timetabling.model.Event;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static edu.kit.dol.timetabling.utilities.Dates.calendarToLocalDateTime;

@SuppressWarnings("unused")
public class CalendarViewer extends Application {

    private static final List<CalendarSource> eventTypeCalendarSource = new ArrayList< > ();

    public static void launch(String[] args, Agency agency, List<List> eventTypeDescriptionClassesList) {

        for (Agent agent : agency.getAgents()) {
            Calendar eventTypeItem;
            List<Calendar> eventTypeList = new ArrayList< > ();
            for (List list : eventTypeDescriptionClassesList) {
                Object item  = list.get(0);

                eventTypeItem = new Calendar(item.toString());
                eventTypeItem.setShortName(Integer.toString(eventTypeDescriptionClassesList.indexOf(list) + 1));
                eventTypeItem.setStyle(Style.getStyle(eventTypeDescriptionClassesList.indexOf(list)));
                eventTypeList.add(eventTypeItem);

            }

            Entry entry;
            LocalDateTime sDate, eDate;
            for (Event event : agent.getEventLog()) {
                entry = new Entry(event.toString());
                sDate = calendarToLocalDateTime(event.getDate());
                eDate = sDate.plus(event.getLength(), ChronoUnit.MINUTES);
                entry.setInterval(new Interval(sDate, eDate));
                entry.setLocation(agent.toString());
                eventTypeList.get(event.getType() - 1).addEntry(entry);
            }

            CalendarSource calendarSource = new CalendarSource(agent.getTag());
            calendarSource.getCalendars().setAll(eventTypeList);
            eventTypeCalendarSource.add(calendarSource);
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        CalendarView calendarView = new CalendarView();

        calendarView.getCalendarSources().setAll(eventTypeCalendarSource);
        calendarView.setRequestedTime(LocalTime.now());
        calendarView.showMonthPage();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(calendarView);

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();

        Scene scene = new Scene(stackPane);
        primaryStage.setTitle("dolTimetabling");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}