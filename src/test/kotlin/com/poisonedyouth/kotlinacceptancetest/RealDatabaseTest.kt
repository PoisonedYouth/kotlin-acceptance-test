package com.poisonedyouth.kotlinacceptancetest

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Target(ANNOTATION_CLASS, CLASS)
@Retention(RUNTIME)
@ExtendWith(CleanDatabaseDataSourceExtension::class)
@SpringBootTest
@ContextConfiguration(classes = [TestContextDataSourceConfiguration::class])
annotation class RealDatabaseTest
