package com.gilberto009199.optaplanner.domain;

import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public class TimeTableEasyScoreCalculator implements EasyScoreCalculator<TimeTable,	HardSoftScore>{

	@Override
	public HardSoftScore calculateScore(TimeTable solution) {
		List<Lesson> lessonList = solution.getLessonList();
		int hardScore = 0;
		
		for (Lesson a : lessonList) {
			for (Lesson b : lessonList) {
				if(	a.getTimeslot() != null &&
					a.getTimeslot().equals(b.getTimeslot()) &&
					a.getId() < b.getId()
				) {
				  // Uma sala pode acomodar no máximo uma aula no mesmo horário.
				  if (a.getRoom() != null && a.getRoom().equals(b.getRoom())) {
					  hardScore--;
				  }
				  // Um professor pode lecionar no máximo uma aula no mesmo horário.
				  if (a.getTeacher().equals(b.getTeacher())) {
					  hardScore--;
				  }
				  // Um aluno pode assistir no máximo uma aula no mesmo horário.
				  if (a.getStudentGroup().equals(b.getStudentGroup())) {
					  hardScore--;
				  }
				}
			}
		}
		
		int softScore = 0;
		
		// Restrições flexíveis (soft constraints) são implementadas apenas no código do `optaplanner-quickstarts`.
		return HardSoftScore.of(hardScore, softScore);
		
	}

}
