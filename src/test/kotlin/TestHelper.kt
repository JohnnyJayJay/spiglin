import com.github.johnnyjayjay.spiglin.command.command
import org.bukkit.command.CommandExecutor

fun makeCommand(): CommandExecutor = command {
    rootCommand {
        println("HI")
        true // return
    }

    subCommandExecutor("sub1") {
        println("sub1")
        true // return
    }

    subCommand("sub2sub") {
        rootCommand {
            println("sub3")
            true // return
        }

        subCommandExecutor("sub3") {
            println("sub3")
            true // return
        }

        subCommandExecutor("sub4sub") {
            println("sub3")
            true // return
        }
    }
}
