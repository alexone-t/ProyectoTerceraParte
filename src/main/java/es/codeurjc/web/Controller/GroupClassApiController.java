package es.codeurjc.web.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import es.codeurjc.web.Model.ClassUser;

import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Service.GroupClassService;

//import es.codeurjc.web.repository.GroupClassRepository;
import es.codeurjc.web.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("api/groupClasses")
public class GroupClassApiController {

    /*@Autowired
    private GroupClassRepository GroupClass;*/

    @Autowired
    private GroupClassService apiGroupClassService;

    @Autowired
    private ValidateService validateService;

    interface GroupClassDetails extends GroupClass.Basic, GroupClass.Users, ClassUser.Basic{}

    /////////////////GET(all)/////////////////

    @JsonView(GroupClassDetails.class)
    @GetMapping("/")
    public Collection<GroupClass> getGroupClasses() {
        return apiGroupClassService.findAll();
    }

    ////////////////GET(ByType)////////////////

    @JsonView(GroupClassDetails.class)
    @GetMapping("/criteria")
    public ResponseEntity<List<GroupClass>> getByCriteria(
            @RequestParam(required = false) Boolean official,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String time
    ) {
        List<GroupClass> result = apiGroupClassService.findByCriteria(official, name, time);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @JsonView(GroupClassDetails.class)
    @GetMapping("/find")
    public ResponseEntity<List<GroupClass>> dinamicQuerie(
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String instructor
    ) {
        validateService.cleanInput(day);
        validateService.cleanInput(instructor);
        List<GroupClass> result = apiGroupClassService.dinamicQuerie(day, instructor);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    /////////////////GET(byId)/////////////////

    @JsonView(GroupClassDetails.class)
    @GetMapping("/{id}")
    public ResponseEntity<GroupClass> getGroupClass(@PathVariable long id) {

        GroupClass groupClass = apiGroupClassService.findById(id).orElse(null);

        if(groupClass != null){
            return ResponseEntity.ok(groupClass);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    /////////////////POST/////////////////

    @JsonView(GroupClass.Basic.class)
    @PostMapping("/")
    public ResponseEntity<?> createGroupClass(@RequestBody GroupClass groupClass) throws IOException{
        /*Lo resume como:
        * @JsonView(GroupClass.Basic.class)
    @PostMapping("/")
    public ResponseEntity<?> createGroupClass(@RequestBody GroupClass groupClass) {
        apiGroupClassService.save(groupClass);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(groupClass.getId()).toUri();
        return ResponseEntity.created(location).body(groupClass);
    }*/
        String error = validateService.validateClass(groupClass);

        if(error != null){
            Map <String, Object> response = new HashMap<>();
            response.put("error",error);
            response.put("groupClass",groupClass);
            return ResponseEntity.badRequest().body(response);
        }else{
            apiGroupClassService.save(groupClass);
            URI location = fromCurrentRequest().path("{id}").buildAndExpand(groupClass.getId()).toUri();
            return ResponseEntity.created(location).body(groupClass);
        }


    }

    /////////////////DELETE/////////////////

    @JsonView(GroupClass.Basic.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Optional<GroupClass>> deleteGroupClass(@PathVariable long id) {

        Optional <GroupClass> groupClass = apiGroupClassService.findById(id);

        if(groupClass.isPresent()){
            //apiGroupClassService.deleteById(id);
            apiGroupClassService.delete(id);
            return ResponseEntity.ok(groupClass);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    /////////////////PUT/////////////////

    @JsonView(GroupClass.Basic.class)
    @PutMapping("/{id}")
    public ResponseEntity<?> replaceGroupClass(@PathVariable long id,@RequestBody GroupClass newGroupClass) throws IOException{
        /*Lo resume como:
        * public ResponseEntity<?> replaceGroupClass(@PathVariable long id, @RequestBody GroupClass newGroupClass) {
        Optional<GroupClass> groupClassOptional = apiGroupClassService.findById(id);
        if (groupClassOptional.isPresent()) {
            newGroupClass.setId(id);
            apiGroupClassService.edit(newGroupClass);
            return ResponseEntity.ok(newGroupClass);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/
        String error = validateService.validateClass(newGroupClass);

        if(error != null){
            Map <String, Object> response = new HashMap<>();
            response.put("error",error);
            response.put("groupClass",newGroupClass);
            return ResponseEntity.badRequest().body(response);
        }else{
            Optional <GroupClass> groupClassOptional = apiGroupClassService.findById(id);
            if(groupClassOptional.isPresent()){
                newGroupClass.setId(id);
                //apiGroupClassService.editClass(newGroupClass,id);
                apiGroupClassService.edit(new GroupClass());
                return ResponseEntity.ok(newGroupClass);
            }else{
                return ResponseEntity.notFound().build();
            }
        }
    }
}
