package net.zomis.duga.chat;

import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.SubmitButton;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StackExchangeLogin implements LoginFunction {
    private static final Logger logger = LoggerFactory.getLogger(StackExchangeLogin.class.getName());

    @Override
    public MechanizeAgent constructAgent(BotConfiguration configuration) {
        MechanizeAgent agent = new MechanizeAgent();
        agent.getClient().setRedirectStrategy(new RedirectStrategy() {
            @Override
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                return response.getStatusLine().getStatusCode() == 302;
            }

            @Override
            public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                logger.info("Redirect. Headers: " + Arrays.toString(response.getAllHeaders()));
                String host = request.getFirstHeader("Host").getValue();
                String location = response.getFirstHeader("Location").getValue();
                String protocol = "https";
                if (location.startsWith("http://") || location.startsWith("https://")) {
                    logger.info("Redirecting to " + location);
                    return new HttpGet(location);
                } else {
                    logger.info("Redirecting to " + protocol + "://" + host + location);
                    return new HttpGet(protocol + "://" + host + location);
                }
            }
        });

        agent.getClient().addRequestInterceptor((request, context) -> {
            logger.info("Request to " + request.getRequestLine().getUri());
            if (request.getRequestLine().getUri().equals("/login/global-fallback")) {
                request.addHeader("Referer", configuration.getRootUrl() + "/users/chat-login");
            }
        });

        return agent;
    }

    @Override
    public String retrieveFKey(MechanizeAgent agent, BotConfiguration configuration) {
        loginOpenId(agent, configuration);
        loginRoot(agent, configuration);
        // loginChat(agent, configuration);
        return retrieveFKeyReal(agent, configuration);
    }

    private void loginOpenId(MechanizeAgent agent, BotConfiguration configuration) {
        HtmlDocument openIdLoginPage = agent.get("https://openid.stackexchange.com/account/login");
        logger.info("openIdLoginPage: " + openIdLoginPage);
        logger.info("openIdLoginPage.root: " + openIdLoginPage.getRoot());
        List<Form> forms = openIdLoginPage.forms().getAll();
        if (forms.isEmpty()) {
            logger.info("openIdLoginPage result: No form found.");
            return;
        }
        Form loginForm = forms.get(0);
        loginForm.get("email").setValue(configuration.getBotEmail());
        loginForm.get("password").setValue(configuration.getBotPassword());
        List<SubmitButton> submitButtons = loginForm.findAll("input[type=submit]", SubmitButton.class);
        HtmlDocument response = loginForm.submit(submitButtons.get(0));
        logger.info("OpenID login response title: {}", response.getTitle());
    }

    private void loginRoot(MechanizeAgent agent, BotConfiguration configuration) {
        Resource resource = agent.get(configuration.getRootUrl() + "/users/login");
        if (!(resource instanceof HtmlDocument)) {
            logger.error("Resource not HTML: " + resource);
            logger.error(resource.getResponse().toString());
            try {
                logger.error(IOUtils.toString(resource.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException("Cannot read input input stream.", e);
            }
            throw new RuntimeException("Resource is not HTML: " + resource);
        }
        HtmlDocument rootLoginPage = (HtmlDocument) resource;
        Form loginForm = rootLoginPage.forms().getAll().get(rootLoginPage.forms().getAll().size() - 1);
        loginForm.get("openid_identifier").setValue("https://openid.stackexchange.com/");
        List<SubmitButton> submitButtons = loginForm.findAll("input[type=submit]", SubmitButton.class);
        HtmlDocument response = loginForm.submit(submitButtons.get(submitButtons.size() - 1));
        logger.info(response.getTitle());
        logger.info("Root login attempted.");
    }

    private void loginChat(MechanizeAgent agent, BotConfiguration configuration) {
        HtmlDocument chatLoginPage = agent.get(configuration.getRootUrl() + "/users/chat-login");
        Form loginForm = chatLoginPage.forms().getAll().get(chatLoginPage.forms().getAll().size() - 1);
        List<SubmitButton> submitButtons = loginForm.findAll("input[type=submit]", SubmitButton.class);
        HtmlDocument response = loginForm.submit(submitButtons.get(submitButtons.size() - 1));
        logger.info(response.getTitle());
        logger.info("Chat login attempted.");
    }

    private String retrieveFKeyReal(MechanizeAgent agent, BotConfiguration configuration) {
        String url = configuration.getChatUrl() + "/chats/join/favorite";
        logger.info("Trying to fetch fkey from chatUrl: {}", url);
        HtmlDocument joinFavoritesPage = agent.get(url);
        Form joinForm = joinFavoritesPage.forms().getAll().get(joinFavoritesPage.forms().getAll().size() - 1);
        return joinForm.get("fkey").getValue();
    }

}
