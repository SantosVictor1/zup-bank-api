package br.com.zup.bank.workflow

import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.IWaitListService
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.scenario.ProcessScenario
import org.camunda.bpm.scenario.Scenario
import org.camunda.bpm.scenario.act.UserTaskAction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

@WebAppConfiguration
@SpringBootTest
@RunWith(SpringRunner::class)
@Deployment(resources = ["userRegister.bpmn"])
class UserRegisterWorkflowTest {
    @Autowired
    private lateinit var waitListService: IWaitListService
    @Autowired
    private lateinit var blacklistService: IBlacklistService
    @Autowired
    private lateinit var userService: IUserService
    @Mock
    private lateinit var registerProcess: ProcessScenario

    @Test
    @Transactional
    fun testRegisterWithCpfOutOfBlacklist() {
        val variables = setVariables()

        Scenario.run(registerProcess).startByKey("userRegister", variables).execute()
        verify(registerProcess, (times(1))).hasFinished("EndEventSuccess")

        val user = waitListService.getStatus("42511229846")
        Assert.assertEquals(user.status, Status.COMPLETED)
    }

    @Test
    @Transactional
    fun testRegisterWithCpfInBlacklist() {
        val variables = setVariables()
        val blacklistRequestDTO = buildBlacklistRequest()

        Mockito.`when`(registerProcess.waitsAtUserTask("MANUAL_CALLBACK")).thenReturn(
            UserTaskAction {
                variables["callbackResponse"] = true
            }
        )

        blacklistService.saveNewCpf(blacklistRequestDTO)
        Scenario.run(registerProcess).startByKey("userRegister", variables).execute()
//        verify(registerProcess, (times(1))).hasFinished("EndEventSuccess")

        val user = waitListService.getStatus("42511229846")
        Assert.assertEquals(user.status, Status.COMPLETED)
    }

    private fun buildBlacklistRequest() = BlacklistRequestDTO("42511229846")

    private fun setVariables(): MutableMap<String, Any> {
        val variables = mutableMapOf<String, Any>()

        variables["cpf"] = "42511229846"
        variables["email"] = "pedro@gmail.com"
        variables["name"] = "Pedro"

        return variables
    }
}