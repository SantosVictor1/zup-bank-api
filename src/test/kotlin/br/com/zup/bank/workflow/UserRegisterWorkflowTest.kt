package br.com.zup.bank.workflow

import br.com.zup.bank.AbstractTestConfig
import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.service.IBlacklistService
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.IWaitListService
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests
import org.camunda.bpm.scenario.ProcessScenario
import org.camunda.bpm.scenario.Scenario
import org.camunda.bpm.scenario.act.UserTaskAction
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


@Transactional
@Deployment(resources = ["userRegister.bpmn"])
class UserRegisterWorkflowTest : AbstractTestConfig() {
    @Autowired
    private lateinit var waitListService: IWaitListService
    @Autowired
    private lateinit var blacklistService: IBlacklistService
    @Autowired
    private lateinit var userService: IUserService
    @Mock
    private lateinit var registerProcess: ProcessScenario

    @Test
    fun testRegisterWithCpfOutOfBlacklist() {
        val variables = setVariables()

        Scenario.run(registerProcess).startByKey("userRegister", variables).execute()
        verify(registerProcess, (times(1))).hasFinished("EndEventSuccess")

        val userStatus = waitListService.getStatus("42511229846")
        val user = userService.getByCpf("42511229846", true)

        Assert.assertEquals(userStatus.status, Status.COMPLETED)
        Assert.assertThat(user.email, CoreMatchers.`is`(variables["email"]))
        Assert.assertThat(user.name, CoreMatchers.`is`(variables["name"]))
    }

    @Test
    fun testRegisterWithCpfInBlacklistAndApproved() {
        val variables = setVariables()
        val blacklistRequestDTO = buildBlacklistRequest()

        Mockito.`when`(registerProcess.waitsAtUserTask("MANUAL_CALLBACK")).thenReturn(
            UserTaskAction {
                BpmnAwareTests.runtimeService().setVariable(it.executionId, "callbackResponse", true)
                it.complete()
            }
        )

        blacklistService.saveNewCpf(blacklistRequestDTO)
        Scenario.run(registerProcess).startByKey("userRegister", variables).execute()
        verify(registerProcess, (times(1))).hasFinished("EndEventSuccess")

        val userStatus = waitListService.getStatus("42511229846")
        val user = userService.getByCpf("42511229846", true)

        Assert.assertEquals(userStatus.status, Status.COMPLETED)
        Assert.assertThat(user.email, CoreMatchers.`is`(variables["email"]))
        Assert.assertThat(user.name, CoreMatchers.`is`(variables["name"]))
    }

    @Test
    fun testRegisterWithCpfInBlacklistAndReproved() {
        val variables = setVariables()
        val blacklistRequestDTO = buildBlacklistRequest()

        Mockito.`when`(registerProcess.waitsAtUserTask("MANUAL_CALLBACK")).thenReturn(
            UserTaskAction {
                BpmnAwareTests.runtimeService().setVariable(it.executionId, "callbackResponse", false)
                it.complete()
            }
        )

        blacklistService.saveNewCpf(blacklistRequestDTO)
        Scenario.run(registerProcess).startByKey("userRegister", variables).execute()
        verify(registerProcess, (times(1))).hasFinished("EndEventError")

        val userStatus = waitListService.getStatus("42511229846")
        Assert.assertEquals(userStatus.status, Status.FAILED)
        Assert.assertEquals(userStatus.message, "Cadastro reprovado")
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