package com.gilberto009199.optaplanner.domain;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {

	@Override
	public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
		
		return new Constraint[] {
		  // Restrições rígidas (Hard constraints)
		  roomConflict(constraintFactory),
		  teacherConflict(constraintFactory),
		  studentGroupConflict(constraintFactory),
		  // Restrições flexíveis (Soft constraints) são implementadas apenas no código do optaplanner-quickstarts
		};
		
	}
	
	private Constraint roomConflict(ConstraintFactory constraintFactory) {
		  // Uma sala pode acomodar no máximo uma aula no mesmo horário.
		  // Selecione uma aula ...
		  return constraintFactory.forEach(Lesson.class)
		  // ... e combine-a com outra aula ...
		  .join(Lesson.class,
		  // ... no mesmo horário (timeslot) ...
		   Joiners.equal(Lesson::getTimeslot),
		  // ... na mesma sala ...
		   Joiners.equal(Lesson::getRoom),
		  // ... e o par é único (id diferente, sem pares reversos)
		   Joiners.lessThan(Lesson::getId)
		  )
		  // ... então penalize cada par com um peso rígido (hard weight).
		  .penalize(HardSoftScore.ONE_HARD)
		  .asConstraint("Room conflict");
	}
	
	private Constraint teacherConflict(ConstraintFactory constraintFactory) {
		  // Um professor pode lecionar no máximo uma aula no mesmo horário.
		  return constraintFactory.forEach(Lesson.class)
		  .join(Lesson.class,
			  Joiners.equal(Lesson::getTimeslot),
			  Joiners.equal(Lesson::getTeacher),
			  Joiners.lessThan(Lesson::getId)
		  )
		  .penalize(HardSoftScore.ONE_HARD)
		  .asConstraint("Teacher conflict");
	}
	
	private Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
		  // A student can attend at most one lesson at the same time.
		  return constraintFactory.forEach(Lesson.class)
		  .join(Lesson.class,
			   Joiners.equal(Lesson::getTimeslot),
			   Joiners.equal(Lesson::getStudentGroup),
			   Joiners.lessThan(Lesson::getId)
		  )
		  .penalize(HardSoftScore.ONE_HARD)
		  .asConstraint("Student group conflict");
	}
	
}
