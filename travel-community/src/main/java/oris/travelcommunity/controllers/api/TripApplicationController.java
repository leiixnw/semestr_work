package oris.travelcommunity.controllers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import oris.travelcommunity.dto.request.TripApplicationRequest;
import oris.travelcommunity.models.TripApplication;
import oris.travelcommunity.models.enums.ApplicationStatus;
import oris.travelcommunity.services.TripApplicationService;
import oris.travelcommunity.services.UserService;
import oris.travelcommunity.models.User;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class TripApplicationController {

    private final TripApplicationService applicationService;
    private final UserService userService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForTrip(@RequestBody TripApplicationRequest request,
                                          @AuthenticationPrincipal UserDetails currentUser) {
        User traveler = userService.getByEmail(currentUser.getUsername());

        TripApplication application = applicationService.create(request, traveler.getId());
        return ResponseEntity.ok(application);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeApplicationStatus(@PathVariable("id") Long applicationId,
                                                     @RequestParam("status") ApplicationStatus status,
                                                     @AuthenticationPrincipal UserDetails currentUser) {
        User organizer = userService.getByEmail(currentUser.getUsername());

        TripApplication updated = applicationService.changeStatus(applicationId, organizer.getId(), status);
        return ResponseEntity.ok(updated);
    }
}