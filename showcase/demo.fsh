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
@step("new-project --named conftrack --topLevelPackage com.conftrack");
@step("scaffold setup"); 
@step("persistence setup --provider HIBERNATE --container JBOSS_AS6");
@step("entity --named Conference");
@step("ls");

@step("ls id::long");
@step("field string --named name");
@step("ls");
@step("scaffold from-entity");
@step("build");

@step("mvn jboss:hard-deploy");
@step("field temporal --type DATE --named begins");
@step("field temporal --type DATE --named ends");
@step("ls");
@step("build");

@step("mvn jboss:hard-deploy");
