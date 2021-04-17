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

call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=2.0.13

pause