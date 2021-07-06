FROM openjdk:11-jre

EXPOSE 3842

WORKDIR /duga-bot/
ADD build/libs/*-all.jar /duga-bot/

VOLUME /data/logs/

CMD java -jar /duga-bot/ktor-demo-0.0.1-SNAPSHOT-all.jar \
    unanswered \
    answer-invalidation comment-scan \
    vba-star-race \
    weekly-update-reminder \
    hello-world \
    stats-dynamodb \
    duga-poster \
    daily-stats
