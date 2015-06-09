import net.zomis.duga.DugaBot
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
    dugaBot(DugaBot)
    stringification(HookStringification)
    stats(DugaStats)
    tasks(DugaTasks)
    stackAPI(StackExchangeAPI)
    githubAPI(GithubBean)
}
