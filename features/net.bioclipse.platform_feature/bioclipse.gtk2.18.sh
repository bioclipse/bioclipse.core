#!/bin/sh

BIOCLIPSE=`readlink -f $0 | xargs dirname`

export GDK_NATIVE_WINDOWS=true
$BIOCLIPSE/bioclipse