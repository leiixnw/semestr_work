package oris.travelcommunity.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oris.travelcommunity.dto.TripProposalDto;
import oris.travelcommunity.dto.request.TripProposalRequest;
import oris.travelcommunity.services.CategoryService;
import oris.travelcommunity.services.TripProposalService;
import oris.travelcommunity.services.UserService;
import oris.travelcommunity.models.User;

import java.math.BigDecimal;

@Controller
@RequestMapping("/proposals")
@RequiredArgsConstructor
public class TripProposalController {

    private final TripProposalService tripProposalService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public String listProposals(Model model) {
        model.addAttribute("proposals", tripProposalService.getAllActiveProposals());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "proposals/list";
    }

    @GetMapping("/{id}")
    public String getProposalDetails(@PathVariable Long id,
                                     Model model,
                                     @AuthenticationPrincipal UserDetails currentUser) {
        TripProposalDto proposalDto = tripProposalService.getProposalById(id);
        model.addAttribute("proposal", proposalDto);

        BigDecimal priceInUsd = tripProposalService.getPriceInUsd(id);
        model.addAttribute("priceInUsd", priceInUsd);

        if (currentUser != null) {
            User user = userService.getByEmail(currentUser.getUsername());
            model.addAttribute("currentUser", user);
        }

        return "proposals/details";
    }

    @GetMapping("/create")
    public String createProposalPage(Model model) {
        model.addAttribute("proposalRequest", new TripProposalRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "proposals/create";
    }

    @PostMapping("/create")
    public String createProposal(@Valid @ModelAttribute("proposalRequest") TripProposalRequest request,
                                 BindingResult bindingResult,
                                 Model model,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "proposals/create";
        }

        TripProposalDto created = tripProposalService.create(request, currentUser.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Тур успешно создан!");
        return "redirect:/proposals/" + created.getId();
    }

    @GetMapping("/{id}/edit")
    public String editProposalPage(@PathVariable Long id,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails currentUser) {
        TripProposalDto proposal = tripProposalService.getProposalById(id);
        User organizer = userService.getByEmail(currentUser.getUsername());

        if (!proposal.getOrganizerId().equals(organizer.getId())) {
            return "redirect:/proposals/" + id;
        }

        TripProposalRequest request = new TripProposalRequest();
        request.setTitle(proposal.getTitle());
        request.setDescription(proposal.getDescription());
        request.setPrice(proposal.getPrice());
        request.setLocation(proposal.getLocation());
        request.setStartDate(proposal.getStartDate());
        request.setEndDate(proposal.getEndDate());
        request.setMaxParticipants(proposal.getMaxParticipants());

        model.addAttribute("proposalRequest", request);
        model.addAttribute("proposal", proposal);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "proposals/edit";
    }

    @PostMapping("/{id}/edit")
    public String editProposal(@PathVariable Long id,
                               @Valid @ModelAttribute("proposalRequest") TripProposalRequest request,
                               BindingResult bindingResult,
                               Model model,
                               @AuthenticationPrincipal UserDetails currentUser,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "proposals/edit";
        }

        tripProposalService.update(id, request, currentUser.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Тур успешно обновлён!");
        return "redirect:/proposals/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProposal(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 RedirectAttributes redirectAttributes) {
        tripProposalService.delete(id, currentUser.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Тур удалён.");
        return "redirect:/proposals";
    }
}
