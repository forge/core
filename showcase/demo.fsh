@currentStep = 1;
@v = SHELL.prompt("Start at which step?");
@startAt = Integer.parseInt( "".equals(v.trim()) ? 1 : v );

@doGit = SHELL.promptBoolean("Track changes with Git?");

def step( cmd ) { 

	if ( startAt <= currentStep )
	{
		@SHELL.println();
		@SHELL.prompt("" + currentStep + ": " + $cmd + " ");
		@SHELL.println();
		$cmd;
		if ( doGit && currentStep > 1 )
		{
			git add -A;
		};
		@SHELL.println();
		wait;
		if ( doGit && currentStep > 1)
		{
			git commit -m "step";
		};
		@SHELL.println();
	}
	currentStep ++;

};

@step("new-project --named conftrack --topLevelPackage com.conftrack");

if ( doGit )
{

	git init;
	git add -A;
	git commit -m 'Initial revision';

};

@step("scaffold setup"); 
@step("persistence setup");
@step("prettyfaces setup");
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

@step("richfaces setup");
@step("build");
@step("mvn jboss:hard-deploy");
@step("beans new-bean --type com.conftrack.model.Scheduler --scoped REQUEST");
@step("arquillian create-test --class com.conftrack.model.Scheduler");

@step("build test -Pjbossas-remote-6");

