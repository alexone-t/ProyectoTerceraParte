package es.codeurjc.web.repository;
import es.codeurjc.web.Model.ClassUser;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ClassUserRepository extends JpaRepository <ClassUser, Long> {

}
