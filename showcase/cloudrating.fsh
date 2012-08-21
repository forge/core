@/*  Why: What is the most expensive and painful part of software development? */;
@/*  How: Google for answers, blogs, experiment. */;
@/*  What: Forge streamlines technology integrations to provide a repeatable, testable, productive programming model tool.*/;


new-project;

set ACCEPT_DEFAULTS true;
persistence setup --provider HIBERNATE --container JBOSS_AS7;

entity --named Conference;
field string --named name;
field temporal --type DATE --named beginDate;
field temporal --type DATE --named endDate;

@/* Explain round tripping / full parsing */;

entity --named Session;
field manyToOne --named conference --fieldType ~.model.Conference.java --inverseFieldName sessions;
field string --named title;
field string --named description;

entity --named SessionReview;
field manyToOne --named session --fieldType ~.model.Session.java --inverseFieldName reviews;
field string --named reviewerName;
field int --named rating;
field string --named comments;

entity --named Speaker;
field string --named name;
field string --named company;
field string --named website;
field string --named twitter;
field oneToMany --named sessions --fieldType ~.model.Session.java --inverseFieldName speaker;

cd ~~;
build;

scaffold setup;
scaffold from-entity ~.model.*;

build;

set ACCEPT_DEFAULTS false;