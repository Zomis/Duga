package net.zomis.duga.chat;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.zomis.duga.chat.events.DugaEvent;
import net.zomis.duga.chat.events.DugaPrepostEvent;
import net.zomis.duga.chat.events.DugaStartedEvent;
import net.zomis.duga.chat.events.DugaStopEvent;

import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.HtmlElement;
import com.gistlabs.mechanize.document.json.JsonDocument;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import net.zomis.duga.chat.listen.ChatMessageRetriever;
import net.zomis.duga.chat.listen.StackExchangeFetch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackExchangeChatBot implements ChatBot {

	private final static Logger LOGGER = LoggerFactory.getLogger(StackExchangeChatBot.class);

    @Deprecated
	private static final BotRoom debugRoom = BotRoom.toRoom("20298");

	private static final int MAX_MESSAGE_LENGTH = 500;
	private static final String MESSAGE_CONTINUATION = "...";

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final BlockingQueue<List<ChatMessage>> messagesQueue = new LinkedBlockingQueue<>();

	private final MechanizeAgent agent;
    private final LoginFunction loginFunction = new StackExchangeLogin();

	private final BotConfiguration configuration;

	private String chatFKey;

    private final Map<Class<?>, List<Consumer<Object>>> handlers = new HashMap<>();

    public StackExchangeChatBot(BotConfiguration config) {
        this.configuration = config;
		this.agent = loginFunction.constructAgent(config);
    }

	public BotRoom room(String roomId) {
		return new BotRoom(this, roomId);
	}

	public void start() {
		this.executorService.submit(() -> {
			try {
                login();
				LOGGER.info("Start draining");
                executeEvent(new DugaStartedEvent(this));
				drainMessagesQueue();
			} catch (Exception ex) {
                LOGGER.error("Unable to start bot", ex);
			}
		});
	}

    private void login() {
        this.chatFKey = loginFunction.retrieveFKey(agent, configuration);
        LOGGER.info("Found fkey: " + chatFKey);
    }

    @Override
    @Deprecated
    public Future<List<ChatMessageResponse>> postChat(List<ChatMessage> messages) {
		Objects.requireNonNull(messages, "messages");
        if (messages.isEmpty()) {
            return null;
        }
		List<ChatMessage> shortenedMessages = new ArrayList<>();
        BotRoom params = BotRoom.toRoom(messages.get(0).getRoom());
		for (ChatMessage mess : messages) {
            String message = mess.getMessage();
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

        LOGGER.info("Adding messages to queue: " + shortenedMessages);
		messagesQueue.add(shortenedMessages);
        return null;
	}

	private ChatMessageResponse attemptPostMessageToChat(final ChatMessage message) {
        LOGGER.info("Real message post: " + message);
        Objects.requireNonNull(message, "message");
        ChatMessageResponse response = postMessageToChat(message);
        if (response.getException() instanceof ChatThrottleException) {
            ChatThrottleException ex = (ChatThrottleException) response.getException();
            LOGGER.info("Chat throttle. Sleeping for " + ex.getThrottleTiming() + " seconds, then reposting");
			try {
				TimeUnit.SECONDS.sleep(ex.getThrottleTiming());
			} catch(InterruptedException ex1) {
				Thread.currentThread().interrupt();
			}
            ChatMessageResponse response2 = postMessageToChat(message);
            if (response2.hasException()) {
                LOGGER.error("Failed to post message on retry", response2.getException());
            }
            return response2;
		}
        if (response.getException() instanceof ProbablyNotLoggedInException) {
            LOGGER.warn("Probably not logged in. Logging in and then reposting");
			login();
            ChatMessageResponse response2 = postMessageToChat(message);
            if (response2.hasException()) {
                LOGGER.error("Failed to post message on retry", response2.getException());
            }
            return response2;
		}
        return response;
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
        return attemptPostMessageToChat(message);
    }

    private ChatMessageResponse postMessageToChat(final ChatMessage message) {
		Objects.requireNonNull(message, "message");
		Map<String, String> parameters = new HashMap<>();
        DugaPrepostEvent prepostEvent = new DugaPrepostEvent(this, message);
        executeEvent(prepostEvent);
        if (!prepostEvent.isPerformPost()) {
            return new ChatMessageResponse(-1, 0, "CANCELLED");
        }
        String text = prepostEvent.getMessage();
        text = text.replaceAll("access_token=([0-9a-f]+)", "access_token=xxxxxxxxxxxxxx");
        parameters.put("text", text);
		parameters.put("fkey", this.chatFKey);
		LOGGER.info("Okay, here we go!");
        Resource response;
        try {
            response = agent.post("https://chat.stackexchange.com/chats/" +
                    message.getRoom() + "/messages/new", parameters);
        } catch (UnsupportedEncodingException e) {
            return new ChatMessageResponse("UnsupportedEncodingException", e);
        }

        LOGGER.info("Response title: " + response.getTitle());
        if (response instanceof JsonDocument) {
			LOGGER.info(response.toString());
            JsonDocument json = (JsonDocument) response;
			LOGGER.info("Success: " + json.getRoot());
            message.onSuccess(json);
            return new ChatMessageResponse(Long.parseLong(json.getRoot().getChild("id").getValue()),
                    Long.parseLong(json.getRoot().getChild("time").getValue()), json.getRoot().toString());
        }

        if (response instanceof HtmlDocument) {
            HtmlDocument htmlDocument = (HtmlDocument) response;
			LOGGER.error("Failure: " + htmlDocument);
            HtmlElement body = htmlDocument.find("body");
			if (body == null) {
				LOGGER.error("Null body: {}", htmlDocument);
				return new ChatMessageResponse(htmlDocument.asString(), new NullPointerException("Null Body"));
			}
            if (body.getInnerHtml().contains("You can perform this action again in")) {
                LOGGER.info("Throttling: " + body.getInnerHtml());
                int timing =
                        Integer.parseInt(body.getInnerHtml().replaceAll("You can perform this action again in", "")
                            .replaceAll("seconds?\\.?", "").trim());
                return new ChatMessageResponse(body.getInnerHtml(), new ChatThrottleException(timing));
            }

            LOGGER.error("Failure body: {}", body.getInnerHtml());
            return new ChatMessageResponse(body.getInnerHtml(), new ProbablyNotLoggedInException());
        }

		LOGGER.error("Unknown response: " + response);
        //even harder failure
        throw new IllegalStateException("unexpected response, response.getClass() = " + response.getClass());
	}

	public void stop() {
        this.executeEvent(new DugaStopEvent(this));
		this.executorService.shutdownNow();
	}

    @Override
    public <E extends DugaEvent> void registerListener(Class<E> eventClass, Consumer<E> handler) {
        handlers.putIfAbsent(eventClass, new ArrayList<>());
        handlers.get(eventClass).add(e -> handler.accept((E) e));
    }

    private void executeEvent(DugaEvent event) {
        List<Consumer<Object>> list = handlers.get(event.getClass());
        if (list != null) {
            list.forEach(e -> e.accept(event));
        }
    }

    private void drainMessagesQueue() {
		try {
			while (true) {
                LOGGER.info("Posting drained messages...");
				try {
                    List<ChatMessage> mess = messagesQueue.take();
                    LOGGER.info("Retrieved: " + mess);
					postDrainedMessages(mess);
                    LOGGER.info("Posted: " + mess);
				}
				catch (RuntimeException ex) {
                    LOGGER.warn("Error in drainMessagesQueue", ex);
					try {
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
		LOGGER.info("Attempting to post " + messages);
		if (currentBurst + messages.size() >= configuration.getChatMaxBurst()
			|| System.currentTimeMillis() < lastPostedTime + configuration.getChatThrottle()) {
			long sleepTime = lastPostedTime + configuration.getChatThrottle() - System.currentTimeMillis();
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

    public ChatMessageRetriever listener() {
        return new StackExchangeFetch(() -> chatFKey);
    }
}
