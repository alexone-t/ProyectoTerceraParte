package es.codeurjc.web.Controller;
import jakarta.servlet.http.HttpSession;

import es.codeurjc.web.Model.ClassUser;
import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Service.GroupClassService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class GroupClassWebController {

    @Autowired
    private GroupClassService groupClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;

    @GetMapping("/")
    public String showGroupClasses(Model model) {
        model.addAttribute("GroupClasses", groupClassService.findAll(true, null, null));
        model.addAttribute("ImagineClass", groupClassService.findAll(false, null, null));
        return "index";
    }

    @GetMapping("/GroupClasses/CreateAClass")
    public String newClass(Model model) {
        model.addAttribute("groupClass", new GroupClass());
        return "newClassPage";
    }

    @PostMapping("/GroupClasses/CreateAClass")
    public String newPageProcess(Model model, @ModelAttribute("groupClass") GroupClass groupClass) throws IOException {

        // Clean user entry before validation
        groupClass.setName(validateService.cleanInput(groupClass.getName()));
        groupClass.setDay(validateService.cleanInput(groupClass.getDay()));
        groupClass.setTime(validateService.cleanInput(groupClass.getTime()));
        groupClass.setInstructor(validateService.cleanInput(groupClass.getInstructor()));

        String validationError = validateService.validateClass(groupClass);
        if (validationError != null) {
            model.addAttribute("error", validationError);
            return "newClassPage";
        } else {
            groupClass.setOfficialClass(false);
            groupClassService.save(groupClass);
            return "redirect:/GroupClasses/" + groupClass.getName() + "/" + groupClass.getId();
        }
    }

    @GetMapping("/GroupClasses/{name}/{id}")
    public String showClass(Model model, @PathVariable String name, @PathVariable long id, HttpSession session) {
        Optional<GroupClass> optionalGroupClass = groupClassService.findById(id);
        if (optionalGroupClass.isPresent()) {
            GroupClass groupClass = optionalGroupClass.get();
            model.addAttribute("GroupClass", groupClass);
            return "class";
        } else {
            return "index";
        }
    }

    @GetMapping("/GroupClasses/DeleteClass/{id}")
    public String deleteClass(@PathVariable long id) {
        groupClassService.delete(id);
        return "deleted_class";
    }

    @GetMapping("/GroupClasses/EditClass/{id}")
    public String showEditClass(@PathVariable long id, Model model) {
        GroupClass groupClass = groupClassService.findById(id).orElse(null);
        model.addAttribute("groupClass", groupClass);
        model.addAttribute("edit", true);
        return "newClassPage";
    }

    @PostMapping("/GroupClasses/EditClass/{id}")
    public String processEditClassForm(Model model, @PathVariable long id, @ModelAttribute("groupClass") GroupClass groupClass) throws IOException {
        groupClass.setName(validateService.cleanInput(groupClass.getName()));
        groupClass.setDay(validateService.cleanInput(groupClass.getDay()));
        groupClass.setTime(validateService.cleanInput(groupClass.getTime()));
        groupClass.setInstructor(validateService.cleanInput(groupClass.getInstructor()));

        String validationError = validateService.validateClass(groupClass);
        if (validationError != null) {
            model.addAttribute("error", validationError);
            return "newClassPage";
        } else {
            groupClassService.edit(groupClass);
            return "redirect:/GroupClasses/" + groupClass.getName() + "/" + groupClass.getId();
        }
    }

    @GetMapping("/GroupClasses/{name}")
    public String showGroupClass(Model model, @PathVariable String name, HttpSession session) {
        List<GroupClass> listOfClasses = groupClassService.findAll(null, name, null);
        model.addAttribute("GroupClass", listOfClasses);
        for (GroupClass groupClass : listOfClasses) {
            model.addAttribute("isMaxCapacityReached", groupClass.maxCapacityReached());
        }
        return "class";
    }

    @GetMapping("/GroupClasses/{name}/JoinClass-{id}")
    public String joinClass(Model model, @PathVariable Long id) {
        Optional<GroupClass> optionalGroupClass = groupClassService.findById(id);
        if (optionalGroupClass.isPresent()) {
            model.addAttribute("GroupClass", optionalGroupClass.get());
            return "joinClass";
        } else {
            return "index";
        }
    }

    @GetMapping("/GroupClasses/{name}/LeaveClass-{id}")
    public String leaveClass(Model model, @PathVariable Long id) {
        Optional<GroupClass> optionalGroupClass = groupClassService.findById(id);
        if (optionalGroupClass.isPresent()) {
            model.addAttribute("GroupClass", optionalGroupClass.get());
            return "leaveClass";
        } else {
            return "index";
        }
    }

    @PostMapping("/GroupClasses/{name}/LeaveClass-{id}")
    public String leaveClassProcess(Model model, @PathVariable String name, @PathVariable Long id) {

        name = validateService.cleanInput(name);

        GroupClass groupClass = groupClassService.findById(id).orElse(null);
        if (groupClass != null) {
            List<ClassUser> listOfUsers = groupClass.getClassUsers();
            ClassUser userWithHighestId = listOfUsers.get(0);
            for (ClassUser user : listOfUsers) {
                if (user.getUserid() > userWithHighestId.getUserid()) {
                    userWithHighestId = user;
                }
            }
            groupClassService.removeUser(userWithHighestId.getUserid(), id);
            userService.delete(userWithHighestId.getUserid());
            groupClass.setAlreadyJoined(false);
            groupClass.setCurrentCapacity(groupClass.getCurrentCapacity() - 1);
        }
        return "redirect:/GroupClasses/{name}";
    }

    @PostMapping("/GroupClasses/{name}/JoinClass-{id}")
    public String joinClassProcess(Model model, @PathVariable String name, @RequestParam String username, @PathVariable Long id) throws IOException {

        username = validateService.cleanInput(username);

        ClassUser user = new ClassUser(username);
        Optional<GroupClass> optionalGroupClass = groupClassService.findById(id);
        if (optionalGroupClass.isPresent()) {
            GroupClass groupClass = optionalGroupClass.get();
            groupClass.setAlreadyJoined(true);
            user.getListOfClasses().add(groupClass);
            groupClassService.addUser(user, groupClass.getId());
            userService.save(user);
            groupClassService.save(groupClass);
        }
        return "redirect:/GroupClasses/{name}/{id}";
    }

    @GetMapping("/GroupClasses/find")
    public String searchClass(Model model, @RequestParam(required = false) String day, @RequestParam(required = false) String instructor){

        model.addAttribute("GroupClass", groupClassService.dinamicQuerie(day,instructor));

        return "class";
    }
}
