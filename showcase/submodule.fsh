@currentStep = 1;
@v = SHELL.prompt("Start at which step?");
@startAt = Integer.parseInt( "".equals(v.trim()) ? 1 : v );

def step( cmd ) { 

        if ( startAt <= currentStep )
        {
                @SHELL.println();
                if ( SHELL.promptBoolean("Execute " + currentStep + ": " + cmd + " ?") )
                {
                        @SHELL.println();
                        $cmd;
                        @SHELL.println();
                        wait;
                        clear;
                }
        }
        currentStep ++;

};

clear;

@step("new-project --named mm --type pom --topLevelPackage de");
@step("new-project --named mm-jar2 --topLevelPackage de");
@step("cd ..");
@step("mvn clean install");
