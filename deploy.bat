@echo on
@echo =============================================================
@echo $                                                           $
@echo $                      Nepxion Aquarius                     $
@echo $                                                           $
@echo $                                                           $
@echo $                                                           $
@echo $  Nepxion Studio All Right Reserved                        $
@echo $  Copyright (C) 2017-2050                                  $
@echo $                                                           $
@echo =============================================================
@echo.
@echo off

@title Nepxion Aquarius
@color 0a

call mvn clean deploy -DskipTests -e -P release -pl aquarius-assembly-lock,aquarius-assembly-cache,aquarius-assembly-limit,aquarius-assembly-id-generator,aquarius-assembly-all -am

pause