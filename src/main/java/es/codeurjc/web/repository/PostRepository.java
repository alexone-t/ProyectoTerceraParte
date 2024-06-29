package es.codeurjc.web.repository;
import es.codeurjc.web.Model.Post;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface PostRepository extends JpaRepository <Post, Long>{
//         //List<Post> findByTitle(String title);
//         //List<Post> findByImage(String image);
//
//         @Override
//         <S extends Post> @NotNull List<S> findAll(@NotNull Example<S> example);
//
//         @Override
//         <S extends Post, R> @NotNull R findBy(@NotNull Example<S> example, @NotNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
//
//         //<S extends Post, R> R findTitle(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
//
//         //<S extends Post, R> R findImage(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
     }
