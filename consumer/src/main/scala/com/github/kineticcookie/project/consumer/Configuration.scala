package com.github.kineticcookie.project.consumer

final case class Configuration(
    kafkaServer: String,
    consumerGroup: String,
    topic: String,
    dbUrl: String,
    dbUser: String,
    dbPassword: String
)
