package com.prgrms.offer.documentation;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.offer.authentication.application.AuthService;
import com.prgrms.offer.authentication.application.JwtTokenProvider;
import com.prgrms.offer.authentication.application.KakaoOAuthClient;
import com.prgrms.offer.authentication.application.OAuthWebClient;
import com.prgrms.offer.authentication.presentation.AuthenticationContext;
import com.prgrms.offer.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureRestDocs
public abstract class ApiDocumentationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected KakaoOAuthClient kakaoOAuthClient;

    @MockBean
    protected OAuthWebClient oAuthWebClient;

    @MockBean
    protected AuthenticationContext authenticationContext;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected MemberService memberService;

    protected OperationResponsePreprocessor getDocumentResponse() {
        return Preprocessors.preprocessResponse(prettyPrint());
    }

    protected OperationRequestPreprocessor getDocumentRequest() {
        return Preprocessors.preprocessRequest(prettyPrint());
    }

}
