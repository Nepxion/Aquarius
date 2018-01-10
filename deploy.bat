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

@title Nepxion Skeleton
@color 0a

call mvn clean deploy -DskipTests -e -P release -pl aquarius-cache,aquarius-id-generator,aquarius-limit,aquarius-lock,aquarius-all -am

@rem call mvn clean deploy -DskipTests -e -P release -pl aquarius-common,aquarius-common-redis,aquarius-common-zookeeper,aquarius-cache-aop,aquarius-cache-redis,aquarius-id-generator-local,aquarius-id-generator-redis,aquarius-id-generator-zookeeper,aquarius-limit-aop,aquarius-limit-local,aquarius-limit-redis,aquarius-lock-aop,aquarius-lock-local,aquarius-lock-redis,aquarius-lock-zookeeper -am

pause