package br.com.zup.bank

import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebAppConfiguration
@SpringBootTest
@RunWith(SpringRunner::class)
abstract class AbstractTestConfig {
    @Autowired
    protected lateinit var context: WebApplicationContext
    protected lateinit var mvc: MockMvc

    @Before
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context).build()
    }
}