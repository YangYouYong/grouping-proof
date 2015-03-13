# $Id: Makefile.am 760 2011-02-13 14:26:18Z rtartiere@il4p.fr $

INCLUDES = $(CUTTER_CFLAGS) -I$(top_srcdir)/libfreefare
AM_CFLAGS = @LIBNFC_CFLAGS@

if WITH_CUTTER

check_LTLIBRARIES = \
		     libtestcommon.la

libtestcommon_la_SOURCES = mifare_desfire_auto_authenticate.c \
			   mifare_desfire_auto_authenticate.h

endif
