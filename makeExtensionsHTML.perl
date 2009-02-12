#!/usr/bin/perl
# Copyright 2009 Egon Willighagen <egonw@users.sf.net>
#
# License: Eclipse Public License
#
# Needs the command line utilities xpath and xsltproc. (Ubuntu: xsltproc, libxml-xpath-perl)
#
use diagnostics;
use strict;

my %extensionPoints = (
  'content-type', 'org.eclipse.core.runtime.contentTypes'
);

open(INDEX, ">ep.index.html");
print INDEX "<html>\n";
print INDEX "<head>\n";
print INDEX "  <title>Extension Points used in Bioclipse</title>\n";
print INDEX "</head>\n";
print INDEX "<body>\n";
print INDEX "  <h1>Extension Points used in Bioclipse</h1>\n";
print INDEX "<ul>\n";
while ( my ($ep, $id) = each(%extensionPoints) ) {
  print INDEX "<li>\n";
  print INDEX "<a href=\"ep.$ep.html\">$ep</a>\n";
  `echo "<list>" > ep.$ep.xml`;
  `xpath -q -e "//extension[\@point='$id']" \`find . -name plugin.xml\` >> ep.$ep.xml`;
  `echo "</list>" >> ep.$ep.xml`;
  `xsltproc ep2html.xslt ep.$ep.xml > ep.$ep.html`;
  print INDEX "</li>\n";
}
print INDEX "</ul>\n";
print INDEX "</body>\n";
print INDEX "</html>\n";

