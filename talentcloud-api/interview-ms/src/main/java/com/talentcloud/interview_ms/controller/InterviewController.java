@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final ProfileClient profileClient;

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String id, Authentication auth) {
        String jwt = ((JwtAuthenticationToken) auth).getToken().getTokenValue();
        return ResponseEntity.ok(profileClient.getProfile(id, jwt));
    }
}
