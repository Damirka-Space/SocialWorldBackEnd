package space.damirka.socialworldbackend.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import space.damirka.socialworldbackend.dtos.UserRegistrationDto;
import space.damirka.socialworldbackend.entities.UserEntity;
import space.damirka.socialworldbackend.services.UserService;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

@Service
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;


    @Autowired
    OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserRegistrationDto newUser = new UserRegistrationDto();

        if(authentication.getPrincipal() instanceof DefaultOAuth2User oauthUser) {
            if(Objects.nonNull(userService.getUserByUsername(oauthUser.getName()))) {
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }

            newUser.setUsername(oauthUser.getName());
        } else {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            if(Objects.nonNull(userService.getUserByUsername(oidcUser.getSubject()))) {
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }

            newUser.setUsername(oidcUser.getSubject());
        }

        newUser.setPassword(alphaNumericString(30));

        UserEntity registerUser = userService.registerUser(newUser);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private static String alphaNumericString(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
