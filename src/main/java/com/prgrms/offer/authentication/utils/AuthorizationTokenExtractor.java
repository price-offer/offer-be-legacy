package com.prgrms.offer.authentication.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;

public class AuthorizationTokenExtractor {

    private static final Pattern JWT_TOKEN_PATTERN = Pattern.compile(
            "^Bearer \\b([A-Za-z\\d-_]*\\.[A-Za-z\\d-_]*\\.[A-Za-z\\d-_]*)$");
    private static final int TOKEN_INDEX = 1;

    private AuthorizationTokenExtractor() {
    }

    public static String extractToken(final HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Strings.isEmpty(header)) {
            return null;
        }

        Matcher matcher = JWT_TOKEN_PATTERN.matcher(header);
        if (matcher.find()) {
            return matcher.group(TOKEN_INDEX);
        }
        return null;
    }
}
