#!/usr/bin/perl
# Copyright 2009 Egon Willighagen <egonw@users.sf.net>
#
# License: Eclipse Public License
#
# Needs the command line utilities xpath and xsltproc.
# (Ubuntu: xsltproc, libxml-xpath-perl)
#
use diagnostics;
use strict;

my %extensionPoints = (
  'editor', 'org.eclipse.ui.editors',
  'menuContribution', 'org.eclipse.ui.menus',
  'definition', 'org.eclipse.core.expressions.definitions',
  'wizard', 'org.eclipse.ui.newWizards',
  'content-type', 'org.eclipse.core.runtime.contentTypes'
);
my %bioclipseExtensionPoints = (
  'scriptContribution', 'net.bioclipse.scripting.contribution',
  'folder', 'net.bioclipse.data.install'
);

my @plugins = `find .. -name plugin.xml`;

open(my $INDEX, '>', 'doc/ep.index.html') or die $!;
print $INDEX "<html>\n";
print $INDEX "<head>\n";
print $INDEX "  <title>Extension Points used in Bioclipse</title>\n";
print $INDEX "</head>\n";
print $INDEX "<body>\n";
print $INDEX "<h1>Extension Points used in Bioclipse</h1>\n";

print $INDEX "<h2>Eclipse Extension Points</h2>\n";
print $INDEX "<ul>\n";
while ( my ($ep, $id) = each(%extensionPoints) ) {
  print $INDEX "<li>\n";
  print $INDEX "<a href=\"ep.$ep.html\">$id</a>\n";
  system('echo "<list>" > doc/ep.'.$ep.'.xml');
  foreach my $plugin (@plugins) {
    $plugin =~ s/[\n|\r]//g;
    $plugin =~ m/.*\/([\w|\.]+)\/plugin\.xml/;
    my $pluginID = $1;
    `echo "<plugin id=\\"$pluginID\\">" >> doc/ep.$ep.xml`;
    `echo "<!-- $plugin -->" >> doc/ep.$ep.xml`;
    `xpath -q -e "\/\/extension[\@point=\'$id\']" $plugin >> doc/ep.$ep.xml`;
    if ($? == -1) {
      print "Execution of xpath failed; please check your xpath " +
            "installation.\n";
      exit -1;
    }
    `echo "<\/plugin>" >> doc/ep.$ep.xml`;
  }
  system('echo "</list>" >> doc/ep.'.$ep.'.xml');
  system('xsltproc --stringparam epElement '.$ep.' ep2html.xslt doc/ep.'.$ep.'.xml > doc/ep.'.$ep.'.html');
  print $INDEX "</li>\n";
}
print $INDEX "</ul>\n";

print $INDEX "<h2>Bioclipse Extension Points</h2>\n";
print $INDEX "<ul>\n";
while ( my ($ep, $id) = each(%bioclipseExtensionPoints) ) {
  print $INDEX "<li>\n";
  print $INDEX "<a href=\"ep.$ep.html\">$id</a>\n";
  system('echo "<list>" > doc/ep.'.$ep.'.xml');
  foreach my $plugin (@plugins) {
    $plugin =~ s/[\n|\r]//g;
    $plugin =~ m/.*\/([\w|\.]+)\/plugin\.xml/;
    my $pluginID = $1;
    `echo "<plugin id=\\"$pluginID\\">" >> doc/ep.$ep.xml`;
    `echo "<!-- $plugin -->" >> doc/ep.$ep.xml`;
    `xpath -q -e "\/\/extension[\@point=\'$id\']" $plugin >> doc/ep.$ep.xml`;
    `echo "<\/plugin>" >> doc/ep.$ep.xml`;
  }
  system('echo "</list>" >> doc/ep.'.$ep.'.xml');
  system('xsltproc --stringparam epElement '.$ep.' ep2html.xslt doc/ep.'.$ep.'.xml > doc/ep.'.$ep.'.html');
  print $INDEX "</li>\n";
}
print $INDEX "</ul>\n";

print $INDEX "</body>\n";
print $INDEX "</html>\n";

