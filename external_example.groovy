tasks {
    cron('0 * * * * *') {
        message('20298', 'Once per minute')

        comments('stackoverflow') {
            if (it.toLowerCase().body_markdown.contains('rubberduck')) {
                it.post(14929)
            }
        }

        listen('20298', 20) {
            if (it.userId == 98071) {
                it.reply('Yes master!')
            }
            if (it.message == 'lol') {
                it.star()
            }
        }
    }

    cron('0 0 * * * *') {
        message('20298', 'Once per hour')

        github {
            user('Zomis') {
                events 'IssuesEvent'
            }
            repo('Cardshifter/Cardshifter') {
                events '*'
            }
        }

        repdiff('codereview', 23788, 31562)
    }
}

users {
    user 'Zomis' password 'topsecret123' {
        // stuff...
    }
}