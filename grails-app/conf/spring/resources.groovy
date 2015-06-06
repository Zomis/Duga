import net.zomis.duga.DugaBot
import net.zomis.duga.DugaData
import net.zomis.duga.DugaInit
import net.zomis.duga.GormUserDetailsService
import net.zomis.duga.HookStringification
import net.zomis.duga.SecurityConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

beans = {
    webSecurityConfiguration(SecurityConfiguration)
    passwordEncoder(BCryptPasswordEncoder)
    userDetailsService(GormUserDetailsService)
    dugaData(DugaData)
    dugaTest(DugaInit)
    dugaBot(DugaBot)
    stringification(HookStringification)
}
