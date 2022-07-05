package com.poisonedyouth.realdatabasedemo

import javax.sql.DataSource
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.jdbc.DataSourceBuilder
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

private class KMySQLContainer(image: DockerImageName) : MySQLContainer<KMySQLContainer>(image)

class CleanDatabaseDataSourceExtension : BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        dataSource.connection.use {
            //it.createStatement().execute("DELETE FROM customer")
            //it.createStatement().execute("ALTER TABLE customer AUTO_INCREMENT = 1")
        }
    }


    companion object {
        val dataSource = DatabaseContainer.datasource
    }
}


private object DatabaseContainer {

    private var mySQLContainer: KMySQLContainer = KMySQLContainer(
        DockerImageName.parse("mysql:5.7")

    ).apply {
        withUsername("root")
        withPassword("password")
    }
    val datasource: DataSource
        get() = DataSourceBuilder
            .create()
            .url(mySQLContainer.jdbcUrl)
            .password(mySQLContainer.password)
            .username(mySQLContainer.username)
            .build()

    init {
        mySQLContainer.start()
    }
}
