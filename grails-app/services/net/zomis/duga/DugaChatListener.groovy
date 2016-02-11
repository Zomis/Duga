package net.zomis.duga

import net.zomis.duga.tasks.ListenTask
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.scheduling.TaskScheduler

/**
 * Keeps track of what rooms to listen in and regularly tell those rooms to do their listening
 */
class DugaChatListener implements InitializingBean {

    @Autowired TaskScheduler scheduler
    @Autowired DugaBotService chatBot
    @Autowired DugaTasks tasks
    @Autowired Environment environment

    private ChatCommands commands

    private final Map<String, ListenTask> listenRooms = new HashMap<>()

    @Override
    void afterPropertiesSet() throws Exception {
        assert !commands
        commands = new ChatCommands(this)
        listenStart('20298')
    }

    ListenTask listenStart(String roomId) {
        if (listenRooms.containsKey(roomId)) {
            throw new IllegalStateException('Already listening in room ' + roomId)
        }
        ListenTask listenTask = new ListenTask(chatBot, roomId, commands, this)
        def future = scheduler.scheduleWithFixedDelay(listenTask, 3000)
        listenTask.future = future
        listenRooms.put(roomId, listenTask)
        return listenTask
    }

    ListenTask listenStop(long roomId) {
        String roomKey = String.valueOf(roomId)
        ListenTask task = listenRooms.get(roomKey)
        if (task) {
            task.future.cancel(true)
            listenRooms.remove(roomKey)
        }
        return task
    }
}
