package net.zomis.duga.chat;

import com.gistlabs.mechanize.impl.MechanizeAgent;

public interface LoginFunction {

    MechanizeAgent constructAgent(BotConfiguration configuration);
    String retrieveFKey(MechanizeAgent agent, BotConfiguration configuration);

}
