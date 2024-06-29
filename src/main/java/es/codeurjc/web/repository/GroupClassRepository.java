package es.codeurjc.web.repository;
import es.codeurjc.web.Model.GroupClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface GroupClassRepository extends JpaRepository <GroupClass, Long>{
    @Override
    @NonNull
    <S extends GroupClass> List<S> findAll(@NonNull Example<S> example);

    @Query("select g from GroupClass g where g.instructor = ?1")
    List<GroupClass> findGroupClassByInstructor(String instructor);

    @Query("select g from GroupClass g where g.day = ?1")
    List<GroupClass> findGroupClassByDay(String day);


}
