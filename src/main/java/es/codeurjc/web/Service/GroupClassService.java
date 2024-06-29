package es.codeurjc.web.Service;


import es.codeurjc.web.Model.ClassUser;
import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.repository.GroupClassRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GroupClassService {

    @Autowired
    private GroupClassRepository groupClassRepository;


    @Autowired
    private EntityManager entityManager;


    @Autowired
    private UserService userService;

    /*@Autowired
    private ValidateService validateService;*/


    private AtomicLong nextId = new AtomicLong(1L);

    public List<GroupClass> findAll(){return groupClassRepository.findAll();}

    //no tocar :)
    public List<GroupClass> findAll(Boolean official,String name, String time) {

        StringBuilder queryBuilder = new StringBuilder("SELECT g FROM GroupClass g");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        if (official != null) {
            conditions.add("g.officialClass = :official");
            parameters.put("official", official);
        }
        if (name != null) {
            conditions.add("g.name = :name");
            parameters.put("name", name);
        }
        if (time != null) {
            conditions.add("g.time = :time");
            parameters.put("time", time);
        }
        if (!conditions.isEmpty()) {
            queryBuilder.append(" WHERE ");
            queryBuilder.append(String.join(" AND ", conditions));
        }

        TypedQuery<GroupClass> query = entityManager.createQuery(queryBuilder.toString(), GroupClass.class);

        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }

        return query.getResultList();
    }

    public GroupClass findGroupClassById(long id){
        return groupClassRepository.findById(id).orElseThrow();
    }

    public boolean exist(long id){return groupClassRepository.existsById(id);}
    public Optional<GroupClass> findById(long id) {

        if(this.exist(id)){
            return Optional.of(this.findGroupClassById(id));
        }
        return Optional.empty();

    }

    public GroupClass save(GroupClass groupClass) {

        long id = nextId.getAndIncrement();
        groupClass.setId(id);
        groupClassRepository.save(groupClass);
        return groupClass;
    }

    public void delete(long id) {
        groupClassRepository.deleteById(id);
    }

    public void edit(GroupClass groupClass){
        groupClassRepository.save(groupClass);
    }

    public void addUser(ClassUser user, long groupClassId){
        GroupClass groupClass = groupClassRepository.findById(groupClassId).orElseThrow();
        List <ClassUser> users = groupClass.getClassUsers();
        users.add(user);
        groupClass.setClassUsers(users);
        groupClassRepository.save(groupClass);
    }

    public void removeUser(long userId, long groupClassId){
        GroupClass groupClass = groupClassRepository.findById(groupClassId).orElseThrow();
        List <ClassUser> users = groupClass.getClassUsers();
        users.remove(userService.findById(userId).get());
        groupClass.setClassUsers(users);
        groupClassRepository.save(groupClass);
    }

    /*public Object findByType(String type) {
        TypedQuery<GroupClass> query = entityManager.createQuery(
                "SELECT g FROM GroupClass g WHERE g.type = :type", GroupClass.class
        );
        query.setParameter("type", type);
        return query.getResultList();
    }*/

    public List<GroupClass> findByCriteria(Boolean official, String name, String time) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<GroupClass> query = cb.createQuery(GroupClass.class);
        Root<GroupClass> root = query.from(GroupClass.class);

        List<Predicate> predicates = new ArrayList<>();

        if (official != null) {
            predicates.add(cb.equal(root.get("officialClass"), official));
        }
        if (name != null) {
            predicates.add(cb.equal(root.get("name"), name));
        }
        if (time != null) {
            predicates.add(cb.equal(root.get("time"), time));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));

        TypedQuery<GroupClass> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<GroupClass> dinamicQuerie(String day, String instructor){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<GroupClass> query = cb.createQuery(GroupClass.class);
        Root<GroupClass> root = query.from(GroupClass.class);

        List<Predicate> predicates = new ArrayList<>();

        if (day != null && !day.isEmpty()) {
            predicates.add(cb.equal(root.get("day"), day));
        }
        if (instructor != null && !instructor.isEmpty()) {
            predicates.add(cb.equal(root.get("instructor"), instructor));
        }

        // Ambos nulos -> muestra todas las clases
        if (predicates.isEmpty()) {
            query.select(root);
        } else {
            query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<GroupClass> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();

    }
}