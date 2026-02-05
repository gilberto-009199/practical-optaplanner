package com.gilberto009199.optaplanner;


import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gilberto009199.optaplanner.domain.Lesson;
import com.gilberto009199.optaplanner.domain.Room;
import com.gilberto009199.optaplanner.domain.TimeTable;
import com.gilberto009199.optaplanner.domain.TimeTableConstraintProvider;
import com.gilberto009199.optaplanner.domain.Timeslot;

public class TimeTableApp implements Runnable{

	private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableApp.class);
	
	@Override
	public void run() {
		SolverFactory<TimeTable> solverFactory = SolverFactory.create(
				new SolverConfig()
				.withSolutionClass(TimeTable.class)
				.withEntityClasses( Lesson.class )
				.withConstraintProviderClass(TimeTableConstraintProvider.class)
				
				// O resolvedor (solver) roda por apenas 10 segundos neste pequeno conjunto de dados.
				// É recomendado executar por pelo menos 5 minutos ("5m") em outros casos.
				
				.withTerminationSpentLimit(Duration.ofSeconds(10))
		);
		
		
		var problem = generateDemoData();
		
		var solver = solverFactory.buildSolver();
		TimeTable solution = solver.solve(problem);
		
		printTimetable(solution);
		
		
	}

	private TimeTable generateDemoData() {
		
		var timeslotList = Arrays.asList(
			new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30),
					LocalTime.of(9, 30)),
			new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30),
					LocalTime.of(10, 30)),
			new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30),
					LocalTime.of(11, 30)),
			new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30),
					LocalTime.of(14, 30)),
			new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30),
					LocalTime.of(15, 30)),
			new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30),
					LocalTime.of(9, 30)),
			new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30),
					LocalTime.of(10, 30)),
			new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30),
					LocalTime.of(11, 30)),
			new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30),
					LocalTime.of(14, 30)),
			new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30),
					LocalTime.of(15, 30))
		);
		
		var roomList = Arrays.asList(
			new Room("Room A"),
			new Room("Room B"),
			new Room("Room C")
		);
		
		var id = 0l;
		var lessonList = Arrays.asList(
			new Lesson(id++, "Math", "A. Turing", "9th grade"),
			new Lesson(id++, "Math", "A. Turing", "9th grade"),
			new Lesson(id++, "Physics", "M. Curie", "9th grade"),
			new Lesson(id++, "Chemistry", "M. Curie", "9th grade"),
			new Lesson(id++, "Biology", "C. Darwin", "9th grade"),
			new Lesson(id++, "History", "I. Jones", "9th grade"),
			new Lesson(id++, "English", "I. Jones", "9th grade"),
			new Lesson(id++, "English", "I. Jones", "9th grade"),
			new Lesson(id++, "Spanish", "P. Cruz", "9th grade"),
			new Lesson(id++, "Spanish", "P. Cruz", "9th grade"),
			new Lesson(id++, "Math", "A. Turing", "10th grade"),
			new Lesson(id++, "Math", "A. Turing", "10th grade"),
			new Lesson(id++, "Math", "A. Turing", "10th grade"),
			new Lesson(id++, "Physics", "M. Curie", "10th grade"),
			new Lesson(id++, "Chemistry", "M. Curie", "10th grade"),
			new Lesson(id++, "French", "M. Curie", "10th grade"),
			new Lesson(id++, "Geography", "C. Darwin", "10th grade"),
			new Lesson(id++, "History", "I. Jones", "10th grade"),
			new Lesson(id++, "English", "P. Cruz", "10th grade"),
			new Lesson(id++, "Spanish", "P. Cruz", "10th grade")
		);
		
		return new TimeTable(timeslotList, roomList, lessonList);
	}

	private static void printTimetable(TimeTable timeTable) {
		  LOGGER.info("========");
		  List<Room> roomList = timeTable.getRoomList();
		  List<Lesson> lessonList = timeTable.getLessonList();
		  Map<Timeslot, Map<Room, List<Lesson>>> lessonMap = lessonList.stream().filter(lesson -> lesson.getTimeslot() != null && lesson.getRoom() !=
				  null)
				  .collect(Collectors.groupingBy(Lesson::getTimeslot, Collectors
				.groupingBy(Lesson::getRoom)));
				  LOGGER.info("| | " + roomList.stream()
				  .map(room -> String.format("%-10s", room.getName())).collect
				(Collectors.joining(" | ")) + " |");
				  LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
				  for (Timeslot timeslot : timeTable.getTimeslotList()) {
					  List<List<Lesson>> cellList = roomList.stream()
					  .map(room -> {
						  Map<Room, List<Lesson>> byRoomMap = lessonMap.get(timeslot);
						  if (byRoomMap == null) {
						  return Collections.<Lesson>emptyList();
						  }
						List<Lesson> cellLessonList = byRoomMap.get(room);
						  if (cellLessonList == null) {
						  return Collections.<Lesson>emptyList();
						  }
						return cellLessonList;
					  })
					  .collect(Collectors.toList());
					  LOGGER.info("| " + String.format("%-10s",
					  timeslot.getDayOfWeek().toString().substring(0, 3) + " " +
					timeslot.getStartTime()) + " | "
					  + cellList.stream().map(cellLessonList -> String.format("%-10s",
					  cellLessonList.stream().map(Lesson::getSubject).collect
					(Collectors.joining(", "))))
					  .collect(Collectors.joining(" | "))
					  + " |");
					  LOGGER.info("| | "
					  + cellList.stream().map(cellLessonList -> String.format("%-10s",
					  cellLessonList.stream().map(Lesson::getTeacher).collect
					(Collectors.joining(", "))))
					  .collect(Collectors.joining(" | "))
					  + " |");
					  LOGGER.info("| | "
					  + cellList.stream().map(cellLessonList -> String.format("%-10s",
					  cellLessonList.stream().map(Lesson::getStudentGroup)
					.collect(Collectors.joining(", "))))
					  .collect(Collectors.joining(" | "))
					  + " |");
					  LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
				  }
				  List<Lesson> unassignedLessons = lessonList.stream()
				  .filter(lesson -> 
					  lesson.getTimeslot() == null 
					  || 
					  lesson.getRoom() ==null
				  )
				  .collect(Collectors.toList());
				  if (!unassignedLessons.isEmpty()) {
					  LOGGER.info("");
					  LOGGER.info("Unassigned lessons");
					  for (Lesson lesson : unassignedLessons) {
						  LOGGER.info(" " + lesson.getSubject() + " - " + lesson.getTeacher() +
						" - " + lesson.getStudentGroup());
					  }
				  }
	}
	
}
