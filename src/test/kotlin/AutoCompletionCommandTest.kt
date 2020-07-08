import com.github.johnnyjayjay.spiglin.command.AutoCompletingCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.mockito.Mockito.mock
import kotlin.properties.Delegates
import kotlin.test.Test

class AutoCompletionCommandTest {

    @Test
    fun `test autocomplete without any sub commands`() {
        val emptyCommand = AutoCompletingCommand(children = emptyMap())
        Assertions.assertEquals(
            emptyList<String>(),
            emptyCommand.onTabComplete(sender, command, "", emptyArray())
        ) { "Tab-suggestions must be empty if no commands are present" }
        Assertions.assertEquals(
            emptyList<String>(),
            emptyCommand.onTabComplete(sender, command, "", arrayOf("a", "b", "c"))
        ) { "Tab-suggestions must be empty if no commands are present" }
    }

    @Test
    fun `test suggestions at root level`() {
        Assertions.assertEquals(
            listOf("sub1", "sub2sub"),
            (defaultCommand as TabCompleter).onTabComplete(sender, command, "", emptyArray())
        ) { """Tab suggestions must be "sub1", "sub2sub"""" }

        Assertions.assertEquals(
            listOf("sub1", "sub2sub"),
            (defaultCommand as TabCompleter).onTabComplete(sender, command, "", arrayOf("sub"))
        ) { """Tab suggestions must be "sub1", "sub2sub"""" }

        Assertions.assertEquals(
            listOf("sub2sub"),
            (defaultCommand as TabCompleter).onTabComplete(sender, command, "", arrayOf("sub2"))
        ) { """Tab suggestions must be "sub2sub"""" }
    }

    @Test
    fun `test suggestions at deep level`() {
        Assertions.assertEquals(
            listOf("sub3", "sub4sub"),
            (defaultCommand as TabCompleter).onTabComplete(sender, command, "", arrayOf("sub2sub", ""))
        ) { """Tab suggestions must be "sub3", "sub4sub"""" }

        Assertions.assertEquals(
            listOf("sub3", "sub4sub"),
            (defaultCommand as TabCompleter).onTabComplete(sender, command, "", arrayOf("sub2sub", "sub"))
        ) { """Tab suggestions must be "sub3", "sub4sub"""" }

        Assertions.assertEquals(
            listOf("sub4sub"),
            (defaultCommand as TabCompleter).onTabComplete(sender, command, "", arrayOf("sub2sub", "sub4"))
        ) { """Tab suggestions must be "sub3"""" }
    }

    companion object {
        private var sender: CommandSender by Delegates.notNull()
        private var command: Command by Delegates.notNull()
        private var defaultCommand: CommandExecutor by Delegates.notNull()

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            sender = mock(CommandSender::class.java)
            command = mock(Command::class.java)

            defaultCommand = makeCommand()
        }
    }
}