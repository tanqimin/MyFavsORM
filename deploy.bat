@echo off
cd framework.orm
call mvn clean
call mvn deploy -Dmaven.test.skip=true

cd ../framework.orm.generator
call mvn clean
call mvn deploy -Dmaven.test.skip=true

cd ../framework.orm.spring-boot-starter
call mvn clean
call mvn deploy -Dmaven.test.skip=true

cd ../
@echo on