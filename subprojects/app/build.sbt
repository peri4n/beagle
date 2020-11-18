
lazy val runDev = inputKey[Unit]("run task used for development")

fullRunInputTask(runDev, Compile, "io.beagle.app.App")

(javaOptions in runDev) += "-DrunMode=dev"
(javaOptions in run) += "-DrunMode=prod"

(fork in runDev) := true
(fork in run) := true

(connectInput in runDev) := true
(connectInput in run) := true




