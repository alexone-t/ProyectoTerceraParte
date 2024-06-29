package es.codeurjc.web.controller;
import es.codeurjc.web.exceptions.ResourceNotFoundException;

import es.codeurjc.web.Model.User;
import es.codeurjc.web.Model.GroupClass;
import es.codeurjc.web.Service.GroupClassService;
import es.codeurjc.web.Service.UserService;
import es.codeurjc.web.Service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
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
        model.addAttribute("GroupClasses", groupClassService.findAllForIndex(null, null));
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
            groupClassService.save(groupClass);
            return "redirect:/GroupClasses/" + groupClass.getName() + "/" + groupClass.getId();
        }
    }

    @GetMapping("/GroupClasses/{name}/{id}")
    public String showClass(Model model, @PathVariable String name, @PathVariable long id) {
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
    public String showGroupClass(Model model, @PathVariable String name) {
        List<GroupClass> listOfClasses = groupClassService.findAllForIndex(name, null);
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
    @PostMapping("/GroupClasses/{name}/JoinClassConfirmation-{id}")
    public String joinClassProcess(Model model, @PathVariable String name, @RequestParam Long userId, @PathVariable Long id,HttpServletRequest request) throws IOException {

        name = validateService.cleanInput(name);
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        }

        Long loggedInUserId = getUserIdFromPrincipal(principal);

        if (!loggedInUserId.equals(userId)) {
            return "redirect:/login";
        }

        Optional<GroupClass> groupClassOpt = groupClassService.findById(id);
        if (!groupClassOpt.isPresent()) {
            throw new ResourceNotFoundException("Clase no encontrada");
        }

        groupClassService.joinClass(userId,id);
        return "redirect:/GroupClasses/{name}/{id}";
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

    @PostMapping("/GroupClasses/{name}/LeaveClassConfirmation-{id}")
    public String leaveClassProcess(Model model, @PathVariable String name, @PathVariable Long id, @RequestParam Long userId, Principal principal )throws IOException {

        name = validateService.cleanInput(name);

        if (principal == null) {
            //throw new UnauthorizedException("El usuario no está autenticado");
        }

        Long loggedInUserId = getUserIdFromPrincipal(principal);

        if (!loggedInUserId.equals(userId)) {
            //throw new UnauthorizedException("No tienes permiso para salir de esta clase");
        }

        Optional<GroupClass> groupClassOpt = groupClassService.findById(id);
        if (!groupClassOpt.isPresent()) {
            throw new ResourceNotFoundException("Clase no encontrada");
        }

        groupClassService.joinClass(userId,id);
        return "redirect:/GroupClasses/{name}";
    }

    @GetMapping("/GroupClasses/find")
    public String searchClass(Model model, @RequestParam(required = false) String day, @RequestParam(required = false) String instructor){

        model.addAttribute("GroupClass", groupClassService.dinamicQuerie(day,instructor));

        return "class";
    }
    private Long getUserIdFromPrincipal(Principal principal) {

        return Long.parseLong(principal.getName());
    }
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            String name = principal.getName();
            Optional<User> userOptional = userService.findByUsername(name);
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }
}
