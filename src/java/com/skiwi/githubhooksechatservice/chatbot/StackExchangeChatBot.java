
package com.skiwi.githubhooksechatservice.chatbot;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.SubmitButton;
import com.gistlabs.mechanize.document.json.JsonDocument;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import com.skiwi.githubhooksechatservice.mvc.configuration.Configuration;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

/**
 *
 * @author Frank van Heeswijk
 */
public class StackExchangeChatBot implements ChatBot {
    private final static Logger LOGGER = Logger.getLogger(StackExchangeChatBot.class.getSimpleName());
    
    private final MechanizeAgent agent;
    
    private final Configuration configuration;
    
    private String chatFKey;
    
    public StackExchangeChatBot(final Configuration configuration) {
        this.configuration = configuration;
        
        this.agent = new MechanizeAgent();
        //TODO $agent.agent.http.verify_mode = OpenSSL::SSL::VERIFY_NONE
        
        this.agent.getClient().setRedirectStrategy(new RedirectStrategy() {
            @Override
            public boolean isRedirected(final HttpRequest httpRequest, final HttpResponse httpResponse, final HttpContext httpContext) throws ProtocolException {
                return (httpResponse.getStatusLine().getStatusCode() == 302);
            }

            @Override
            public HttpUriRequest getRedirect(final HttpRequest httpRequest, final HttpResponse httpResponse, final HttpContext httpContext) throws ProtocolException {
                httpRequest.getRequestLine().getProtocolVersion().getProtocol();
                String host = httpRequest.getFirstHeader("Host").getValue();
                String location = httpResponse.getFirstHeader("Location").getValue();
                String protocol = (httpRequest.getFirstHeader("Host").getValue().equals("openid.stackexchange.com")) ? "https" : "http";
                if (location.startsWith("http://") || location.startsWith("https://")) {
                    LOGGER.info("Redirecting to " + location);
                    return new HttpGet(location);
                }
                else {
                    LOGGER.info("Redirecting to " + protocol + "://" + host + location);
                    return new HttpGet(protocol + "://" + host + location);
                }
            }
        });
        
        this.agent.getClient().addRequestInterceptor((request, context) -> {
            LOGGER.info("Request to " + request.getRequestLine().getUri());
            if (request.getRequestLine().getUri().equals("/login/global-fallback")) {
                request.addHeader("Referer", configuration.getRootUrl() + "/users/chat-login");
            }
        });
    }
    
    @Override
    public void start() {        
        loginOpenId();
        
        loginRoot();
        
        loginChat();
        
        String fkey = getFKey();
        this.chatFKey = fkey;
        LOGGER.info("Found fkey: " + fkey);
    }
    
    private void loginOpenId() {
        HtmlDocument openIdLoginPage = agent.get("https://openid.stackexchange.com/account/login");
        Form loginForm = openIdLoginPage.forms().getAll().get(0);
        loginForm.get("email").setValue(configuration.getBotEmail());
        loginForm.get("password").setValue(configuration.getBotPassword());
        List<SubmitButton> submitButtons = loginForm.findAll("input[type=submit]", SubmitButton.class);
        HtmlDocument response = loginForm.submit(submitButtons.get(0));
        LOGGER.info(response.getTitle());
        LOGGER.info("OpenID login attempted.");
    }
    
    private void loginRoot() {
        HtmlDocument rootLoginPage = agent.get(configuration.getRootUrl() + "/users/login");
        Form loginForm = rootLoginPage.forms().getAll().get(rootLoginPage.forms().getAll().size() - 1);
        loginForm.get("openid_identifier").setValue("https://openid.stackexchange.com/");
        List<SubmitButton> submitButtons = loginForm.findAll("input[type=submit]", SubmitButton.class);
        HtmlDocument response = loginForm.submit(submitButtons.get(submitButtons.size() - 1));
        LOGGER.info(response.getTitle());
        LOGGER.info("Root login attempted.");
    }
    
    private void loginChat() {
        HtmlDocument chatLoginPage = agent.get(configuration.getRootUrl() + "/users/chat-login");
        Form loginForm = chatLoginPage.forms().getAll().get(chatLoginPage.forms().getAll().size() - 1);
        List<SubmitButton> submitButtons = loginForm.findAll("input[type=submit]", SubmitButton.class);
        HtmlDocument response = loginForm.submit(submitButtons.get(submitButtons.size() - 1));
        LOGGER.info(response.getTitle());
        LOGGER.info("Chat login attempted."); 
    }
    
    private String getFKey() {
        HtmlDocument joinFavoritesPage = agent.get(configuration.getChatUrl() + "/chats/join/favorite");
        Form joinForm = joinFavoritesPage.forms().getAll().get(joinFavoritesPage.forms().getAll().size() - 1);
        return joinForm.get("fkey").getValue();
    }
    
    @Override
    public void postMessage(final String text) {
        Objects.requireNonNull(text, "text");
        try {
            internalPostMessage(text);
        } catch (ClassCastException ex) {
            LOGGER.log(Level.INFO, "Error during posting", ex);
            LOGGER.info("Retrying to post message.");
            start();
            internalPostMessage(text);
        }
    }
    
    private void internalPostMessage(final String text) {
        String textCopy = text;
        while (textCopy.length() > 500) {
            internalInternalPostMessage(textCopy.substring(0, 497) + "...");
            textCopy = textCopy.substring(497);
        }
        internalInternalPostMessage(textCopy);
    }
    
    private void internalInternalPostMessage(final String text) {
        Objects.requireNonNull(text, "text");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("text", text);
        parameters.put("fkey", this.chatFKey);
        try {
            JsonDocument response = agent.post("http://chat.stackexchange.com/chats/" + configuration.getRoomId() + "/messages/new", parameters);
            LOGGER.info(response.getTitle());
        } catch (UnsupportedEncodingException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void stop() {
        
    }
}
