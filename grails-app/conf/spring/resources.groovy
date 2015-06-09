import net.zomis.duga.DugaBot
import net.zomis.duga.DugaChatListener
import net.zomis.duga.DugaStats
import net.zomis.duga.DugaTasks
import net.zomis.duga.GithubBean
import net.zomis.duga.GormUserDetailsService
import net.zomis.duga.HookStringification
import net.zomis.duga.SecurityConfiguration
import net.zomis.duga.StackExchangeAPI
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

beans = {
    webSecurityConfiguration(SecurityConfiguration)
    passwordEncoder(BCryptPasswordEncoder)
    userDetailsService(GormUserDetailsService)

    // Ordered by dependencies, thing A may be dependent on B if it is listed below B.
    // This ordering does not matter to Spring or Grails, only for personal convenience
    stackAPI(StackExchangeAPI)
    dugaBot(DugaBot)
    stats(DugaStats)
    stringification(HookStringification)
    githubAPI(GithubBean)
    tasks(DugaTasks)
    chatListener(DugaChatListener)
}
