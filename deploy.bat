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

call mvn clean deploy -DskipTests -e -P release -pl aquarius-lock-starter,aquarius-cache-starter,aquarius-limit-starter,aquarius-id-generator-starter -am

pause