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
                }
        }
        currentStep ++;

};

clear;

@step("new-project --named mm --type pom --topLevelPackage de");
@step("new-project --named mm-jar --topLevelPackage de");
@step("cd ..");
@step("new-project --named mm-web --topLevelPackage de");
@step("project install-facet forge.spec.servlet");
@step("cd ..");
@step("mvn clean install");
