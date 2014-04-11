----CS1555 - DATABASE MANAGEMENT SYSTEMS (SPRING 2011)
----DEPT. OF COMPUTER SCIENCE, UNIVERSITY OF PITTSBURGH
----ASSIGMENT #2: SQL - SAMPLE SOLUTION - PART 2
----Release: February 18, 2011

-----------------------------------------------------------
--Q4.a: List the names of all forests that have acid_level over 0.5.

prompt Q4.a 

SELECT Name 
FROM FOREST 
WHERE Acid_Level>0.5;


--------------------------

--Q4.b: Find the names of all roads in the forest whose name is “Allegheny National Forest”.
 
prompt Q4.b 
SELECT R.Name 
FROM	ROAD R, INTERSECTION I, FOREST F 
WHERE R.Road_No=I.Road_No and F.Forest_No=I.Forest_No and  
F.Name='Allegheny National Forest';

-------------------------
--Q4.c: List all the sensors along with the name of the workers who maintain them.
 
prompt Q4.c

SELECT s.Sensor_ID, w.Ssn, w.Name
from SENSOR s left outer join WORKER w on s.Maintainer = w.ssn; 

-------------------------------
--Q4.d: List all the sensors which have not been assigned a maintainer. 
prompt Q4.d

SELECT *
FROM SENSOR
WHERE Maintainer is null;

------------------------------
--Q4.e: Find the names of all forests such that no sensors in those forests reported anything between Jan 10, 2007 and Jan 11, 2007.
prompt Q4.e

SELECT Name 
FROM   FOREST
WHERE NOT EXISTS (SELECT Sensor_ID 
                  FROM SENSOR natural join REPORT 
                  WHERE (X between MBR_XMin and MBR_XMax) and (Y between MBR_YMin and MBR_YMax) 
                        and  (Report_Time between '10-JAN-07' and '11-JAN-07')
                 );

 


------------------------------
--Q4.f: List the pairs of states that share at least one forest (i.e., cover parts of the same forests).

prompt Q4.f

SELECT distinct c1.State, c2.State
FROM COVERAGE c1, COVERAGE c2
where c1.Forest_No = c2.Forest_No
     and c1.State < c2.State;

------------------------------
--Q4.g: For each forest, find its number of sensors and average temperature reported in January 2007. List them in descending order of the average temperatures.

prompt Q4.g
SELECT f.Forest_No, COUNT(distinct s.Sensor_ID) as Num_Sensors, AVG(r.Temperature) as Avg_Temp
FROM (FOREST f left outer join SENSOR s on (s.X between f.MBR_XMin and f.MBR_XMax) and (s.Y between f.MBR_YMin and f.MBR_YMax))
      left outer join (select * from Report where Report_Time between '1-JAN-07' and '31-JAN-07') r on s.Sensor_Id = r.Sensor_Id
GROUP BY f.Forest_No
ORDER BY AVG(r.Temperature) desc;

--Note that the left outer join is used instead of normal equi-join or theta-join in order to:
--+ include the forest that does not have any sensor in it
--+ include the forest whose sensors did not report anything in January 2007

-----------------------------

--Q4.h Find the states that have higher area of forest than Pennsylvania
prompt Q4.h

SELECT State
FROM coverage
GROUP BY State
HAVING sum(area) > (select sum(c.area) from State s join Coverage c on s.Abbreviation = c.State
                    where s.Name = 'Pennsylvania') ;


------------------------------------
--Q4.i: Find the states whose forests cover more than 30% of its area.

SELECT c.State 
FROM coverage c
GROUP BY c.State
HAVING sum(c.area) > 0.03* (select s.area from State s where s.Abbreviation = c.State) ;

---------------------------------
--Q4.j: Find the forest with the highest number of sensors

SELECT Forest_No
FROM Forest join Sensor  on (X between MBR_XMin and MBR_XMax) and (Y between MBR_YMin and MBR_YMax)
GROUP BY Forest_No
HAVING count(sensor_ID)  = (SELECT max(num_sensors) 
                              FROM (SELECT count(sensor_id) as num_sensors
                                    FROM Forest join Sensor on (X between MBR_XMin and MBR_XMax) and (Y between MBR_YMin and MBR_YMax)
                                    GROUP BY Forest_No)
			   );	
 



----CS1555 - DATABASE MANAGEMENT SYSTEMS (SPRING 2011)
----DEPT. OF COMPUTER SCIENCE, UNIVERSITY OF PITTSBURGH
----ASSIGMENT #2: SQL - SAMPLE SOLUTION  - PART 1
----Release: February 18, 2011

--Question 1: 


-- Clean up
drop table report cascade constraints;
drop table coverage cascade constraints;
drop table intersection cascade constraints;
drop table road cascade constraints;
drop table sensor cascade constraints;
drop table worker cascade constraints;
drop table forest cascade constraints;
drop table state cascade constraints;

-- Create tables
create table FOREST (
    Forest_No   varchar2(10),
    Name	varchar2(30),
    Area	float,
    Acid_Level	float,
    MBR_XMin	float,
    MBR_XMax	float,
    MBR_YMin	float,
    MBR_YMax	float,
    Constraint forest_PK primary key (Forest_No) deferrable	
);

create table STATE (
	Name		varchar2(30),
	Abbreviation	varchar2(2),
	Area		float,
	Population	int,
    Constraint State_PK primary key (Abbreviation) deferrable
);

create table COVERAGE (
    Forest_No	varchar2(10),
    State	varchar2(2),
    Percentage	float,
    Area	float,
    Constraint coverage_PK primary key (Forest_No, State) deferrable,
    Constraint coverage_FK1 foreign key (Forest_No) references FOREST( Forest_No ) initially deferred deferrable,
    Constraint coverage_FK2 foreign key ( State ) references State( Abbreviation ) initially deferred deferrable
);

create table ROAD (
    Road_No		varchar2(10),
    Name		varchar2(30),
    Length		float,
    Constraint road_PK primary key (Road_No) deferrable
);

create table INTERSECTION (
    Forest_No	varchar2(10),
    Road_No	varchar2(10),
    Constraint intersection_PK  primary key (Forest_No, Road_No) deferrable,
    Constraint intersection_FK1 foreign key (Forest_No) references FOREST(Forest_No) initially deferred deferrable,
    Constraint intersection_FK2 foreign key (Road_No) references ROAD(Road_No) initially deferred deferrable
);

create table SENSOR (
    Sensor_Id	int,
    X		float,
    Y		float,
    Last_Charged date,
    Constraint sensor_PK primary key (Sensor_Id) deferrable
);

create table REPORT (
    Sensor_Id	int,
    Temperature	float,
    Report_Time	date,
    Constraint report_PK primary key (Sensor_Id, Report_Time) deferrable,
    Constraint report_FK foreign key (Sensor_Id) references SENSOR(Sensor_Id) initially deferred deferrable
);

create table WORKER (
    ssn			varchar2(9),
    Name		varchar2(30),
    Age			int,
    Rank		int,
    Constraint worker_PK primary key (ssn) deferrable
);


------------------------------------------------------------------------------

--Question 2

--a
--note that initially immediate not deferrable is the default setting so you don't really need to specify it

alter table FOREST add Constraint forest_UQ_name UNIQUE(name) initially immediate not deferrable, 

alter table FOREST add constraint forest_UQ_MBR UNIQUE(MBR_XMin, MBR_XMax, MBR_YMin, MBR_YMax) initially immediate not deferrable;

alter table STATE add  Constraint state_UQ_Name UNIQUE (Name) initially immediate not deferrable;

alter table SENSOR add constraint sensor_UQ_coordinate UNIQUE(X,Y) initially immediate not deferrable;


--b
alter table SENSOR
add Energy int not null; 

alter table SENSOR
add constraint energy_check CHECK (Energy >=0 and Energy <=10) initially immediate not deferrable;
--c
alter table FOREST
add Constraint acidCheck CHECK (Acid_Level>=0 and Acid_Level<=1) initially immediate not deferrable;

--d
alter table SENSOR add Maintainer varchar2(9) default null; 


--e
alter table SENSOR
add Constraint sensor_FK  foreign key (Maintainer) references WORKER(ssn) initially immediate not deferrable; 


--------------------------------------------------------
--Question 3:

INSERT INTO FOREST VALUES( '1', 'Allegheny National Forest', 400.0, 0.3, 134.0, 550.0, 233.0, 598.0);
INSERT INTO FOREST VALUES( '2', 'Pennsylvania Forest', 100.0, 0.55, 21.0, 100.0, 35.0, 78.0);
INSERT INTO FOREST VALUES( '3', 'Stone Valley', 150.0, 0.4, 22.0, 78.0, 12.0, 20.0);

INSERT INTO STATE VALUES( 'Pennsylvania', 'PA', 50000.0, 1400000 );
INSERT INTO STATE VALUES( 'Ohio', 'OH', 45000.0, 1200000 );
INSERT INTO STATE VALUES( 'Virginia', 'VA', 35000.0, 1000000 );

INSERT INTO COVERAGE VALUES( 1, 'PA', 0.4, 160.0 );
INSERT INTO COVERAGE VALUES( 1, 'OH', 0.6, 240.0 );
INSERT INTO COVERAGE VALUES( 2, 'PA', 1, 100.0 );
INSERT INTO COVERAGE VALUES( 3, 'PA', 0.3, 45.0 );
INSERT INTO COVERAGE VALUES( 3, 'VA', 0.6, 90.0 );
INSERT INTO COVERAGE VALUES( 3, 'OH', 0.1, 15.0 );

INSERT INTO ROAD VALUES( 1, 'FORBES', 500.0 );
INSERT INTO ROAD VALUES( 2, 'BIGELOW', 300.0 );
INSERT INTO ROAD VALUES( 3, 'BAYARD', 100.0 );

INSERT INTO INTERSECTION VALUES ( '1', '1' );
INSERT INTO INTERSECTION VALUES ( '1', '2' );
INSERT INTO INTERSECTION VALUES ( '2', '1' );
INSERT INTO INTERSECTION VALUES ( '2', '2' );
INSERT INTO INTERSECTION VALUES ( '3', '3' );

INSERT INTO WORKER VALUES( '123456789', 'John', 22, 3 );
INSERT INTO WORKER VALUES( '121212121', 'Jason', 30, 5 );
INSERT INTO WORKER VALUES( '222222222', 'Mike', 25, 4 );


INSERT INTO SENSOR VALUES( 1, 150.0, 300.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 2, '123456789' );
INSERT INTO SENSOR VALUES( 2, 200.0, 400.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, '123456789' );
INSERT INTO SENSOR VALUES( 3, 50.0, 50.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, '121212121' );
INSERT INTO SENSOR VALUES( 4, 50.0, 15.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, null);
INSERT INTO SENSOR VALUES( 5, 60.0, 60.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 4, '121212121' );
INSERT INTO SENSOR VALUES( 6, 50.0, 60.0, to_date('01-JAN-2007 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, null);
INSERT INTO SENSOR VALUES( 7, 150.0, 310.0, to_date('01-MAR-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, '222222222' );
INSERT INTO SENSOR VALUES( 8, 60.0, 50.0, to_date('01-SEP-2007 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 4, '121212121' );   

INSERT INTO REPORT VALUES( 1, 55, to_date('10-JAN-2006 09:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 1, 57, to_date('10-JAN-2006 14:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 1, 40, to_date('10-JAN-2006 20:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 2, 58, to_date('10-JAN-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 2, 59, to_date('10-JAN-2007 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 3, 50, to_date('10-JAN-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 4, 30, to_date('01-JAN-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 5, 33, to_date('02-JAN-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 5, 38, to_date('02-JAN-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 6, 39, to_date('10-MAR-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 7, 45, to_date('20-SEP-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 7, 50, to_date('20-SEP-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 8, 57, to_date('02-JAN-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );

select * from forest;
select * from state;
select * from coverage;
select * from road;
select * from intersection;
select Sensor_Id, X, Y, to_char(Last_Charged, 'DD-MON-YYYY HH24:MI:SS') as Last_Charged, Energy, Maintainer from sensor;
select Sensor_Id Temperature, to_char(Report_time, 'DD-MON-YYYY HH24:MI:SS') as Report_Time from report;
select * from worker;

commit;    

--exit;


--
-- hw3 database
--
--

-- Clean up
drop table report cascade constraints;
drop table coverage cascade constraints;
drop table intersection cascade constraints;
drop table road cascade constraints;
drop table sensor cascade constraints;
drop table worker cascade constraints;
drop table forest cascade constraints;
drop table state cascade constraints;
drop table emergency cascade constraints;

-- Create tables
create table FOREST (
    Forest_No   varchar2(10),
    Name	varchar2(30),
    Area	float,
    Acid_Level	float,
    MBR_XMin	float,
    MBR_XMax	float,
    MBR_YMin	float,
    MBR_YMax	float,
    Constraint forest_PK primary key (Forest_No) deferrable,
    constraint acidCheck CHECK (Acid_Level>=0 and Acid_Level<=1) deferrable,
    Constraint forest_UQ_name UNIQUE(name) deferrable, 
    constraint forest_UQ_MBR UNIQUE(MBR_XMin, MBR_XMax, MBR_YMin, MBR_YMax) deferrable	
);

create table STATE (
	Name		varchar2(30),
	abbreviation	varchar2(2),
	Area		float,
	Population	int,
    Constraint State_PK primary key (abbreviation) deferrable,
    Constraint state_UQ_Name UNIQUE (Name) deferrable
);

create table COVERAGE (
    Forest_No	varchar2(10),
    State	varchar2(2),
    Percentage	float,
    Area	float,
    Constraint coverage_PK primary key (Forest_No, State) deferrable,
    Constraint coverage_FK1 foreign key (Forest_No) references FOREST( Forest_No ) initially deferred deferrable,
    Constraint coverage_FK2 foreign key ( State ) references State( abbreviation ) initially deferred deferrable
);

create table ROAD (
    Road_No		varchar2(10),
    Name		varchar2(30),
    Length		float,
    Constraint road_PK primary key (Road_No) deferrable
);

create table INTERSECTION (
    Forest_No	varchar2(10),
    Road_No	varchar2(10),
    Constraint intersection_PK  primary key (Forest_No, Road_No) deferrable,
    Constraint intersection_FK1 foreign key (Forest_No) references FOREST(Forest_No) initially deferred deferrable,
    Constraint intersection_FK2 foreign key (Road_No) references ROAD(Road_No) initially deferred deferrable
);

create table WORKER (
    ssn			varchar2(9),
    Name		varchar2(30),
    Age			int,
    Rank		int,
    Constraint worker_PK primary key (ssn) deferrable
);

create table SENSOR (
    Sensor_Id	int,
    X		float,
    Y		float,
    Last_Charged date,
    Energy	int,
    Maintainer  varchar2(9), 
    LastRead	date,
    Constraint sensor_PK primary key (Sensor_Id) deferrable,
    constraint energy_check CHECK (Energy >=0 and Energy <=10) deferrable,
    constraint sensor_FK  foreign key (Maintainer) references WORKER(ssn) initially deferred deferrable	
);

create table REPORT (
    Sensor_Id	int,
    Temperature	float,
    Report_Time	date,
    Constraint report_PK primary key (Sensor_Id, Report_Time) deferrable,
    Constraint report_FK foreign key (Sensor_Id) references SENSOR(Sensor_Id) initially deferred deferrable
);


create table Emergency(
   Sensor_id int,
   Report_Time date,
   constraint emergency_PK primary key(sensor_id, Report_Time) deferrable,
   constraint emergency_FK foreign key(Sensor_id, report_time) references REPORT (sensor_id, report_time) initially deferred deferrable	
);


INSERT INTO FOREST VALUES( '1', 'Allegheny National Forest', 400.0, 0.3, 134.0, 550.0, 233.0, 598.0); 
INSERT INTO FOREST VALUES( '2', 'Pennsylvania Forest', 100.0, 0.55, 21.0, 100.0, 35.0, 78.0); 
INSERT INTO FOREST VALUES( '3', 'Stone Valley', 150.0, 0.4, 22.0, 78.0, 12.0, 20.0); 

INSERT INTO STATE VALUES( 'Pennsylvania', 'PA', 50000.0, 1400000 );
INSERT INTO STATE VALUES( 'Ohio', 'OH', 45000.0, 1200000 );
INSERT INTO STATE VALUES( 'Virginia', 'VA', 35000.0, 1000000 );

INSERT INTO COVERAGE VALUES( 1, 'PA', 0.4, 160.0 );
INSERT INTO COVERAGE VALUES( 1, 'OH', 0.6, 240.0 );
INSERT INTO COVERAGE VALUES( 2, 'PA', 1, 100.0 );
INSERT INTO COVERAGE VALUES( 3, 'PA', 0.3, 45.0 );
INSERT INTO COVERAGE VALUES( 3, 'VA', 0.6, 90.0 );
INSERT INTO COVERAGE VALUES( 3, 'OH', 0.1, 15.0 );

INSERT INTO ROAD VALUES( 1, 'FORBES', 500.0 );
INSERT INTO ROAD VALUES( 2, 'BIGELOW', 300.0 );
INSERT INTO ROAD VALUES( 3, 'BAYARD', 100.0 );

INSERT INTO INTERSECTION VALUES ( '1', '1' );
INSERT INTO INTERSECTION VALUES ( '1', '2' );
INSERT INTO INTERSECTION VALUES ( '2', '1' );
INSERT INTO INTERSECTION VALUES ( '2', '2' );
INSERT INTO INTERSECTION VALUES ( '3', '3' );

INSERT INTO WORKER VALUES( '123456789', 'John', 22, 3 );
INSERT INTO WORKER VALUES( '121212121', 'Jason', 30, 5 );
INSERT INTO WORKER VALUES( '222222222', 'Mike', 25, 4 );


INSERT INTO SENSOR VALUES( 1, 150.0, 300.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 2, '123456789', to_date('10-JAN-2006 20:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO SENSOR VALUES( 2, 200.0, 400.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, '123456789',to_date('10-JAN-2007 12:30:00', 'DD-MON-YYYY HH24:MI:SS'));
INSERT INTO SENSOR VALUES( 3, 50.0, 50.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, '121212121', to_date('10-JAN-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO SENSOR VALUES( 4, 50.0, 15.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, null, to_date('01-JAN-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS'));
INSERT INTO SENSOR VALUES( 5, 60.0, 60.0, to_date('01-JAN-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 4, '121212121', to_date('02-JAN-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS'));
INSERT INTO SENSOR VALUES( 6, 50.0, 60.0, to_date('01-JAN-2007 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, null, to_date('10-MAR-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS')); 
INSERT INTO SENSOR VALUES( 7, 150.0, 310.0, to_date('01-MAR-2006 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 3, '222222222', to_date('20-SEP-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') ); 
INSERT INTO SENSOR VALUES( 8, 60.0, 50.0, to_date('01-SEP-2007 10:00:00', 'DD-MON-YYYY HH24:MI:SS'), 4, '121212121', to_date('02-JAN-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );   

INSERT INTO REPORT VALUES( 1, 55, to_date('10-JAN-2006 09:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 1, 57, to_date('10-JAN-2006 14:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 1, 40, to_date('10-JAN-2006 20:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 2, 58, to_date('10-JAN-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 2, 59, to_date('10-JAN-2007 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 3, 50, to_date('10-JAN-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 4, 30, to_date('01-JAN-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 5, 33, to_date('02-JAN-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );
INSERT INTO REPORT VALUES( 5, 38, to_date('02-JAN-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') ); 
INSERT INTO REPORT VALUES( 6, 39, to_date('10-MAR-2006 12:30:00', 'DD-MON-YYYY HH24:MI:SS') ); 
INSERT INTO REPORT VALUES( 7, 45, to_date('20-SEP-2006 22:00:00', 'DD-MON-YYYY HH24:MI:SS') ); 
INSERT INTO REPORT VALUES( 7, 50, to_date('20-SEP-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') ); 
INSERT INTO REPORT VALUES( 8, 57, to_date('02-JAN-2007 22:00:00', 'DD-MON-YYYY HH24:MI:SS') );

commit;    

--exit;


-------------------------------------------------
--Question 1
------------------------------------------------

-- a
set transaction read write;
set constraint all deferred;
INSERT INTO ROAD VALUES( 4, 'century road', 200);
INSERT INTO INTERSECTION
   (SELECT Forest_No, 4 from FOREST where Name = 'Allegheny National Forest');
-- for this hw, this statement is also accepted: INSERT INTO INTERSECTION VALUES( '1', 4);
commit;

-------------------------
--b

set transaction read write;
set constraint all deferred;
UPDATE SENSOR SET Maintainer='121212121' WHERE Sensor_ID=7;
UPDATE SENSOR SET Maintainer='222222222' WHERE Sensor_ID=3 or Sensor_ID=5 or Sensor_ID = 8;
commit;

--** the following solution gets 2 bonus points:

set transaction read write;
set constraint all deferred;
UPDATE SENSOR SET Maintainer = 'temp' WHERE Maintainer = '222222222';
UPDATE SENSOR SET Maintainer = '222222222' WHERE Maintainer = '121212121';
UPDATE SENSOR SET Maintainer = '121212121' WHERE Maintainer = 'temp';
commit;

--**NOTE that if we had the assumption/constraint the worker's name is unique, the following should have been be the complete solution:

set transaction read write;
set constraint all deferred;

UPDATE SENSOR 
SET Maintainer = 'temp' 
WHERE Maintainer = (select SSN from Worker where Name = 'Jason');

UPDATE SENSOR SET Maintainer = (select ssn from Worker where Name = 'Jason') 
WHERE Maintainer = (select ssn from Worker where Name ='Mike');

UPDATE SENSOR SET Maintainer = (select ssn from Worker where Name = 'Mike') 
WHERE Maintainer = 'temp';

-------------------------
--c

set transaction read write;
set constraint all deferred;
INSERT INTO WORKER VALUES( '555555555', 'Peter', 25, 1);
UPDATE SENSOR SET Maintainer='555555555' WHERE Sensor_ID=1;
commit;    


-----------------------------------------------------------------------------
--Question 2
-----------------------------------------------------------------------------

--a
CREATE VIEW PA_FOREST AS 
SELECT f.Forest_No, f.Name, f.Area 
FROM FOREST f, COVERAGE c, STATE s 
WHERE f.Forest_No = c.Forest_No 
      AND c.State = s.Abbrevation 
      AND s.Name = 'Pennsylvania';

--or

CREATE VIEW PA_FOREST AS 
SELECT f.Forest_No , f.Name, f.Area 
FROM (FOREST f JOIN COVERAGE c ON f.Forest_No = c.Forest_No) JOIN STATE s ON c.State = s.Abbrevation  
WHERE AND s.Name = 'Pennsylvania';


---------------------
--b
CREATE VIEW FOREST_AREA AS 
SELECT State, SUM(area) as Total_Area 
FROM COVERAGE 
GROUP BY State;


---------------------
--c
CREATE VIEW DUTIES AS SELECT Maintainer, COUNT(*) as Total 
FROM SENSOR 
GROUP BY Maintainer;



----------------------------------------------------------
--Question 3
----------------------------------------------------------

--a
SELECT distinct Maintainer 
FROM SENSOR s, FOREST f, PA_FOREST pf
WHERE (X between Mbr_Xmin and Mbr_Xmax)
	AND (Y between Mbr_Ymin and Mbr_Ymax)
	AND f.Forest_No = pf.forest_no
	AND Maintainer is not null;

--------------------------
--b

SELECT F1.State 
FROM FOREST_AREA F1 
WHERE F1.Total_AREA = (SELECT MAX(F2.Total_AREA) 
                        FROM FOREST_AREA F2);


------------------------
--c
SELECT Name
FROM WORKER W JOIN DUTIES D1 ON W.SSN=D1.Maintainer
WHERE D1.Total = (SELECT MAX(D2.Total)
                FROM DUTIES D2);


-------------------------
--d
SELECT Sensor_ID, Energy 
FROM SENSOR S JOIN DUTIES D1 ON S.Maintainer = D1.Maintainer 
WHERE D1.Total = (SELECT MAX(D2.Total) 
                 FROM DUTIES D2);



------------------------

--e: I am listing several of the possible solutions:
--e1
SELECT State
FROM FOREST_AREA f
WHERE f.Total_Area >= ANY (SELECT Total_Area 
                           FROM (SELECT * FROM FOREST_AREA ORDER BY ToTal_Area DESC)
                           WHERE RowNum <=3);

--e2:
SELECT State
FROM FOREST_AREA f1
WHERE 3 > (SELECT count(*) 
           FROM Forest_Area f2 
           WHERE f2.ToTal_Area > f1.ToTal_Area);


--e3: use Oracle's rank() function 
SELECT State
FROM (SELECT State, rank() OVER (ORDER BY TOTAL_AREA DESC) as rank 
      FROM FOREST_AREA)
WHERE rank <=3;



-------------------------------------------------------------
--QUESTION 4
-------------------------------------------------------------

--a

CREATE OR REPLACE PROCEDURE proc_update_Last_Read(sensorID in int, read_date in date)
AS
BEGIN
update Sensor
set LastRead = read_date
where Sensor.sensor_id = sensorID;
END; 


------------------------------
--b
CREATE OR REPLACE FUNCTION fun_compute_percentage (forestNo in varchar2, area_covered in float) RETURN float 
AS
percentage float;
BEGIN
select area_covered/f.area into percentage
from forest f
where f.forest_no = forestNo;
return (percentage);
END;

--Again, note that the variable delaration has to come before the body of the function/procedure
------------------------------------------------------------------
--QUESTION 5
------------------------------------------------------------------

--a

CREATE OR REPLACE TRIGGER tri_LastRead
AFTER INSERT ON Report
FOR EACH ROW
BEGIN
proc_update_Last_Read(:new.sensor_id, :new.report_time);
END;


------------------------------
--b
CREATE OR REPLACE TRIGGER tri_Percentage
BEFORE UPDATE OF area ON coverage
FOR EACH ROW
BEGIN
:new.percentage := fun_compute_percentage(:new.forest_no, :new.area);
END;


----------------------------
--c
CREATE OR REPLACE TRIGGER tri_Emergency
AFTER INSERT ON REPORT
FOR EACH ROW
WHEN (new.Temperature > 100)
BEGIN
insert into Emergency values (:new.Sensor_ID, :new.Report_Time);
END;

