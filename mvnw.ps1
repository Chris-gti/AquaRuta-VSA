$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    if (Test-Path "C:\Program Files\Java\jdk-26.0.1") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.1"
    } elseif (Test-Path "C:\Program Files\Java\latest") {
        $env:JAVA_HOME = "C:\Program Files\Java\latest"
    }
}
$MvnCmd = Join-Path $ScriptDir ".mvn\apache-maven-3.9.9\bin\mvn.cmd"
& $MvnCmd @args
