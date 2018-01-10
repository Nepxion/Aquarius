@echo on
@echo =============================================================
@echo $                                                           $
@echo $                      Nepxion Aquarius                     $
@echo $                                                           $
@echo $                                                           $
@echo $                                                           $
@echo $  Nepxion Technologies All Right Reserved                  $
@echo $  Copyright(C) 2017                                        $
@echo $                                                           $
@echo =============================================================
@echo.
@echo off

@title Nepxion Aquarius
@color 0a

call mvn clean deploy -DskipTests -e -P release -pl aquarius-common,aquarius-common-redis,aquarius-common-zookeeper,aquarius-cache-aop,aquarius-cache-redis,aquarius-id-generator-local,aquarius-id-generator-redis,aquarius-id-generator-zookeeper,aquarius-limit-aop,aquarius-limit-local,aquarius-limit-redis,aquarius-lock-aop,aquarius-lock-local,aquarius-lock-redis,aquarius-lock-zookeeper,aquarius-assembly-cache,aquarius-assembly-id-generator,aquarius-assembly-limit,aquarius-assembly-lock,aquarius-assembly-all -am

pause