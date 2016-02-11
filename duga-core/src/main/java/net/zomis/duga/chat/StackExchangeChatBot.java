package net.zomis.duga.chat;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.HtmlElement;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.SubmitButton;
import com.gistlabs.mechanize.document.json.JsonDocument;
import com.gistlabs.mechanize.impl.MechanizeAgent;

public class StackExchangeChatBot implements ChatBot {

	private final static Logger LOGGER = Logger.getLogger(StackExchangeChatBot.class.getSimpleName());

    @Deprecated
	private static final WebhookParameters debugRoom = WebhookParameters.toRoom("20298");

	private static final int MAX_MESSAGE_LENGTH = 500;
	private static final String MESSAGE_CONTINUATION = "...";

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final BlockingQueue<List<ChatMessage>> messagesQueue = new LinkedBlockingQueue<>();

	private final MechanizeAgent agent;

	private final BotConfiguration configuration;

	private String chatFKey;

	private String undeployGoodbyeText;

	public StackExchangeChatBot(BotConfiguration config) {
        this.configuration = config;
		this.agent = new MechanizeAgent();

        this.agent.getClient().setRedirectStrategy(new RedirectStrategy() {
            @Override
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                return response.getStatusLine().getStatusCode() == 302;
            }

            @Override
            public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                System.out.println(Arrays.toString(response.getAllHeaders()));
                String host = request.getFirstHeader("Host").getValue();
				String location = response.getFirstHeader("Location").getValue();
				String protocol = host.equals("openid.stackexchange.com") ? "https" : "http";
				if (location.startsWith("http://") || location.startsWith("https://")) {
					LOGGER.info("Redirecting to " + location);
					return new HttpGet(location);
				} else {
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

	public void start() {
		this.executorService.submit(() -> {
			try {
				startBackground();
				System.out.println("Start draining");
				drainMessagesQueue();
			} catch (Exception ex) {
				System.out.println("Error!! " + ex);
				ex.printStackTrace();
			}
		});
	}

	private void startBackground() {
        login();

		String deployGreeting = ""; // TODO: configService.getConfig("deployGreeting", "");
		if (!deployGreeting.isEmpty()) {
            // String deployGreetingRooms = configService.getConfig("deployGreetingRooms", "");
            String deployGreetingRooms = "16134";
			for (String greetingRoom : deployGreetingRooms.split(",")) {
				if (greetingRoom.matches("^\\d+$")) {
					WebhookParameters params = new WebhookParameters();
					params.setRoomId(greetingRoom);
					params.setPost(true);
					postMessages(params, Collections.singletonList(deployGreeting));
				} else {
					LOGGER.warning("Deploy, No valid room: " + greetingRoom);
				}
			}
		}

		this.undeployGoodbyeText = ""; // configService.getConfig("undeployGoodbyeText", "");
	}

    private void login() {
        loginOpenId();
        loginRoot();
        loginChat();

        String fkey = retrieveFKey();
        this.chatFKey = fkey;
        LOGGER.info("Found fkey: " + fkey);
    }

	private void loginOpenId() {
		HtmlDocument openIdLoginPage = agent.get("https://openid.stackexchange.com/account/login");
        System.out.println(openIdLoginPage);
        System.out.println(openIdLoginPage.getRoot());
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

	private String retrieveFKey() {
		HtmlDocument joinFavoritesPage = agent.get(configuration.getChatUrl() + "/chats/join/favorite");
		Form joinForm = joinFavoritesPage.forms().getAll().get(joinFavoritesPage.forms().getAll().size() - 1);
		return joinForm.get("fkey").getValue();
	}

	public Future<List<ChatMessageResponse>> postMessages(WebhookParameters params, final List<String> messages) {
		if (params == null) {
			throw new NullPointerException("Params cannot be null, unable to post " + messages);
		}
		// params.useDefaultRoom(configuration.getRoomId());
		Objects.requireNonNull(messages, "messages");
		List<ChatMessage> shortenedMessages = new ArrayList<>();
		for (String message : messages) {
			if (message.length() > MAX_MESSAGE_LENGTH) {
				final List<String> messageTokens = ChatMessageHelper.splitToTokens(message);
				for (final String reassembled : ChatMessageHelper.reassembleTokens(messageTokens, MAX_MESSAGE_LENGTH,
					MESSAGE_CONTINUATION)) {
					shortenedMessages.add(new ChatMessage(params, reassembled));
				}
			}
			else {
				shortenedMessages.add(new ChatMessage(params, message));
			}
		}
		if (!params.getPost()) {
			for (ChatMessage message : shortenedMessages) {
				LOGGER.info("Ignoring message for " + message.getRoom() + ": " + message.getMessage());
			}
			return null;
		}

		System.out.println("Adding messages to queue: " + shortenedMessages);
		messagesQueue.add(shortenedMessages);
        return null;
	}

	private void attemptPostMessageToChat(final ChatMessage message) {
		System.out.println("Real message post: " + message);
        Objects.requireNonNull(message, "message");
        ChatMessageResponse response = postMessageToChat(message);
        if (response.getException() instanceof ChatThrottleException) {
            ChatThrottleException ex = (ChatThrottleException) response.getException();
			System.out.println("Chat throttle");
            LOGGER.info("Sleeping for " + ex.getThrottleTiming() + " seconds, then reposting");
			try {
				TimeUnit.SECONDS.sleep(ex.getThrottleTiming());
			} catch(InterruptedException ex1) {
				Thread.currentThread().interrupt();
			}
            ChatMessageResponse response2 = postMessageToChat(message);
            if (response2.hasException()) {
                LOGGER.log(Level.SEVERE, "Failed to post message on retry", response2.getException());
            }
		}
        if (response.getException() instanceof ProbablyNotLoggedInException) {
			System.out.println("Probably not logged in");
			LOGGER.info("Not logged in, logging in and then reposting");
			login();
            ChatMessageResponse response2 = postMessageToChat(message);
            if (response2.hasException()) {
                LOGGER.log(Level.SEVERE, "Failed to post message on retry", response2.getException());
            }
		}
	}

    @Override
    public Future<List<ChatMessageResponse>> postChat(WebhookParameters params, List<String> messages) {
        return postMessages(params, messages);
    }

    @Override
    public void postSingle(WebhookParameters params, String message) {
        postChat(params, Collections.singletonList(message));
    }

    @Override
    public Future<ChatMessageResponse> postAsync(ChatMessage message) {
        this.messagesQueue.add(Collections.singletonList(message));
        return null;
    }

    @Override
    public ChatMessageResponse postNowOnce(ChatMessage message) {
        return postMessageToChat(message);
    }

    @Override
    public ChatMessageResponse postNow(ChatMessage message) {
        return null;
    }

    private ChatMessageResponse postMessageToChat(final ChatMessage message) {
		Objects.requireNonNull(message, "message");
		Map<String, String> parameters = new HashMap<>();
        String text = message.getMessage();
        text = text.replaceAll("access_token=([0-9a-f]+)", "access_token=xxxxxxxxxxxxxx");
        parameters.put("text", text);
		parameters.put("fkey", this.chatFKey);
		System.out.println("Okay, here we go!");
        Resource response;
        try {
            response = agent.post("http://chat.stackexchange.com/chats/" +
                    message.getRoom() + "/messages/new", parameters);
        } catch (UnsupportedEncodingException e) {
            return new ChatMessageResponse(e.toString(), e);
        }

        System.out.println("Response: " + response.getTitle());
        if (response instanceof JsonDocument) {
            System.out.println(response);
            JsonDocument json = (JsonDocument) response;
            System.out.println("Success: " + json.getRoot());
            message.onSuccess(json);
            return new ChatMessageResponse(Long.parseLong(json.getRoot().getChild("id").getValue()),
                    Long.parseLong(json.getRoot().getChild("time").getValue()), json.getRoot().toString());
        }

        if (response instanceof HtmlDocument) {
            //failure
            HtmlDocument htmlDocument = (HtmlDocument) response;
            System.out.println("Failure: " + htmlDocument);
            HtmlElement body = htmlDocument.find("body");
            if (body.getInnerHtml().contains("You can perform this action again in")) {
                int timing =
                        Integer.parseInt(body.getInnerHtml().replaceAll("You can perform this action again in", "")
                            .replaceAll("seconds", "").trim());
                return new ChatMessageResponse(body.getInnerHtml(), new ChatThrottleException(timing));
            }

            System.out.println(body.getInnerHtml());
            return new ChatMessageResponse(body.getInnerHtml(), new ProbablyNotLoggedInException());
        }

        System.out.println("Unknown response: " + response);
        //even harder failure
        throw new IllegalStateException("unexpected response, response.getClass() = " + response.getClass());
	}

	public void stop() {
		if (!this.undeployGoodbyeText.isEmpty()) {
			// TODO: Use specific chat parameters
			postMessages(null, Collections.singletonList(this.undeployGoodbyeText));
		}
		this.executorService.shutdown();
	}

	private void drainMessagesQueue() {
		try {
			while (true) {
				System.out.println("Posting drained messages...");
				try {
                    List<ChatMessage> mess = messagesQueue.take();
					System.out.println("Retrieved: " + mess);
					postDrainedMessages(mess);
					System.out.println("Posted: " + mess);
				}
				catch (RuntimeException ex) {
					System.out.println("Exception: " + ex);
                    ex.printStackTrace(System.out);
					try {
						LOGGER.warning("Error in drainMessagesQueue: " + ex.toString());
						List<ChatMessage> messages = new ArrayList<ChatMessage>();
						messages.add(new ChatMessage(debugRoom, ex.toString()));
						messages.addAll(Arrays.stream(ex.getStackTrace())
								.map(trace -> trace.toString())
								.map(msg -> new ChatMessage(debugRoom, msg))
								.limit(5)
								.collect(Collectors.toList()));
						postDrainedMessages(messages);
					} catch (RuntimeException ex2) {
						// ignored
					}
				}
			}
		} catch(InterruptedException ex) {
			List<List<ChatMessage>> drainedMessages = new ArrayList<>();
			messagesQueue.drainTo(drainedMessages);
			drainedMessages.forEach(it -> postDrainedMessages(it));
			Thread.currentThread().interrupt();
		}
	}

	private long lastPostedTime = 0L;
	private int currentBurst = 0;

	private void postDrainedMessages(final List<ChatMessage> messages) {
		Objects.requireNonNull(messages, "messages");
		System.out.println("Attempting to post");
		LOGGER.fine("Attempting to post " + messages);
		if (currentBurst + messages.size() >= configuration.getChatMaxBurst()
			|| System.currentTimeMillis() < lastPostedTime + configuration.getChatThrottle()) {
			long sleepTime = lastPostedTime + configuration.getChatThrottle() - System.currentTimeMillis();
			System.out.println("Sleeping for " + sleepTime);
			LOGGER.info("Sleeping for " + sleepTime + " milliseconds");
			try {
				TimeUnit.MILLISECONDS.sleep(sleepTime);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			currentBurst = 0;
		}
		else {
			currentBurst += messages.size();
		}
		messages.forEach(message -> {
			try {
				TimeUnit.MILLISECONDS.sleep(configuration.getChatMinimumDelay());
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			attemptPostMessageToChat(message);
		});
		lastPostedTime = System.currentTimeMillis();
	}

    public String getFKey() { return chatFKey; }
}
