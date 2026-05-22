package oris.travelcommunity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/{id}")
    public String getProposalDetails(@PathVariable("id") Long id,
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
}
