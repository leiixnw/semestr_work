package oris.travelcommunity.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import oris.travelcommunity.services.TripProposalService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TripProposalService tripProposalService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("hotTrips", tripProposalService.getHotTrips());
        return "redirect:/proposals";
    }
}
