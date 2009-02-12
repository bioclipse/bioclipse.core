#!/usr/bin/perl
#
# Copyright 2007 Egon Willighagen <egonw@users.sf.net>
#
# License: EPL
#
use diagnostics;
use strict;

my $prefixFeatures = ".";
if (-d "./features") { 
  $prefixFeatures = "features";
}
my $prefixPlugins = ".";
if (-d "./plugins") {
  $prefixPlugins = "plugins";
}

print "Features subdir found: $prefixFeatures\n";
print "Plugins subdir found: $prefixPlugins\n";

# make a clean output
`rm -Rf output`;
`mkdir -p output`;

# First make sure we copy the stylesheets
`mkdir -p output/PRODUCT_PLUGIN`;
`cp $prefixPlugins/net.bioclipse/*.css output/PRODUCT_PLUGIN/.`;

# OK, now start copying in things and create the main index.html
print "Finding features...";
my @features = `find $prefixFeatures -name "feature.xml"`;
print " found: " . scalar(@features) . "\n";

my %tocs;

my @plugins = {};
my @featureIDs = {};
foreach my $feature (@features) {
  my $id = "";
  if ($feature =~ m/\/([^\/]*)\/feature.xml/) {
    $id = $1;
  }
  push @featureIDs, $id;
  print "  feature: $id\n";

  # extract the plugins from the features
  open(FEATUREXML, "<$prefixFeatures/$id/feature.xml") || die "Could not open $prefixFeatures/$id/feature.xml!\n";
  my $pluginlines = 0;
  while (my $line = <FEATUREXML>) {
    if ($pluginlines == 1) {
      if ($line =~ m/id="([^"]*)"/) {
        my $plugin = $1;
        print "    plugin: $plugin\n";
        my $dir = "$plugin";

        # step I: process the plugin.xml
        if (!-e "$prefixPlugins/$dir/plugin.xml") {
          # skip it
        } else {
          open(PLUGINXML, "<$prefixPlugins/$dir/plugin.xml") || die "Could not open $prefixPlugins/$dir/plugin.xml!";
          my $extensionElement = 0;
          my $extensionPoint = "";
          my $tocElement = 0;
          my %tocInfo;
          my $suffix = "";
          while (my $line = <PLUGINXML>) {
            $tocs{$dir.$suffix}{"dir"} = $dir;
  
            if ($line =~ "<extension") {
              $extensionElement = 1;
            } elsif ($line =~ "</extension") {
              $extensionElement = 0;
              $extensionPoint = "";
              $tocElement = 0;
            }
            if ($extensionElement == 1) {
              if ($line =~ m/point=[\"|\'](.*?)[\"|\']/) {
                $extensionPoint = $1;
                # print "EP: $extensionPoint\n";
              }
              if ($extensionPoint eq "org.eclipse.help.toc") {
                if ($line =~ m/<toc/) {
                  $tocElement = 1;
                }
                if ($tocElement == 1) {
                  if ($line =~ m/file=[\"|\'](.*?)[\"|\']/) {
                    my $file = $1;
                    if ($tocs{$dir}{"tocfile"}) {
                      $suffix = $suffix . "#"; # deal with more EPs per plugin
                    }
                    $tocs{$dir.$suffix}{"tocfile"} = $file;
                  }
                }
              }
              # print "EXT line: $line";
            } # else skip content
          }
  
          # step II: process the META-INF/MANIFEST.MF
          open(MANIFEST, "<$prefixPlugins/$dir/META-INF/MANIFEST.MF") || die "Could not open the MANIFEST for $prefixPlugins/$dir!";
          while (my $line = <MANIFEST>) {
            if ($line =~ m/^Bundle-Name:\s*(.*)/) {
              my $name = $1;
              if ($name eq "\%pluginName") {
                # need to to something clever
                if (open(PLUGINPROPS, "<$prefixPlugins/$dir/plugin.properties")) {
                  while (my $line = <PLUGINPROPS>) {
                    if ($line =~ /\s*pluginName\s*=\s*(.*)/) {
                      $name = $1;
                      $name =~ s/^\s*//g;
                      $name =~ s/\s*$//g;
                    }
                  }
                } 
              }
              $tocs{$dir}{"name"} = $name;
            } elsif ($line =~ m/^Bundle-Version:\s*(.*)/) {
              $tocs{$dir}{"version"} = $1;
            } elsif ($line =~ m/^Bundle-Vendor:\s*(.*)/) {
              my $name = $1;
              if ($name eq "\%providerName") {
                # need to to something clever
                if (open(PLUGINPROPS, "<$prefixPlugins/$dir/plugin.properties")) {
                  while (my $line = <PLUGINPROPS>) {
                    if ($line =~ /\s*providerName\s*=\s*(.*)/) {
                      $name = $1;
                      $name =~ s/^\s*//g;
                      $name =~ s/\s*$//g;
                    }
                  }
                } 
              }
              $tocs{$dir}{"vendor"} = $name;
            } elsif ($line =~ m/^Bundle-SymbolicName:\s*(.*?);/) {
              $tocs{$dir}{"symbolicname"} = $1;
            }
          }
  
        }
      }
    } else {
      if ($line =~ m/<plugin/) {
        $pluginlines = 1;
      }
    }
  }
}

# Iterate over all plugin.xml files, and extract which TOC files are mentioned.
# We cannot assume all of them to be called toc.xml, thought that currently often
# is the case.
foreach my $plugin (@plugins) {
  my $dir = $plugin;
}

print "Creating main toc...\n";

open(MAINTOC, ">output/index.html");

print MAINTOC "<html>\n";
print MAINTOC "<head>\n";
print MAINTOC "  <title>Bioclipse Documentation</title>\n";
print MAINTOC "  <link rel=\"stylesheet\" href=\"PRODUCT_PLUGIN/narrow_book.css\" type=\"text/css\">\n";
print MAINTOC "</head>\n";
print MAINTOC "<body>\n";
print MAINTOC "  <h1>Plugins</h1>\n";
print MAINTOC "  <ul>\n";
foreach my $tocDir (sort keys %tocs) {
  next if ($tocDir =~ /#/); # skip EPs for which the $tocDir is already given

  my $tocOutputDir = "output/$tocDir";
  `mkdir -p $tocOutputDir`;
  my $filename = "$tocOutputDir/index.html";
  print MAINTOC "    <li><a href=\"$tocDir/index.html\">" . 
                $tocs{$tocDir}{"name"} . "</a> (" . $tocDir . ")</li>\n";
  open(PLUGINTOC, ">$filename") || die "Could not open $filename!";
  print PLUGINTOC "<html>\n";
  print PLUGINTOC "<head>\n";
  print PLUGINTOC "  <title>" . $tocs{$tocDir}{"name"} . "</title>\n";
  print PLUGINTOC "  <link rel=\"stylesheet\" href=\"../PRODUCT_PLUGIN/narrow_book.css\" type=\"text/css\">\n";
  print PLUGINTOC "</head>\n";
  print PLUGINTOC "<body>\n";
  print PLUGINTOC "  <h1>".$tocs{$tocDir}{"name"}."</h1>\n";
  print PLUGINTOC "  <table>\n";
  print PLUGINTOC "  <tr><td><b>Version</b></td><td>".$tocs{$tocDir}{"version"}."</td></tr>\n" if ($tocs{$tocDir}{"version"});
  print PLUGINTOC "  <tr><td><b>Vendor</b></td><td>".$tocs{$tocDir}{"vendor"}."</td></tr>\n" if ($tocs{$tocDir}{"vendor"});
  print PLUGINTOC "  <tr><td><b>Symbolic Name</b></td><td>".$tocs{$tocDir}{"symbolicname"}."</td></tr>\n" if ($tocs{$tocDir}{"symbolicname"});
  print PLUGINTOC "  </table>\n";

  # process the 'toc.xml'
  my $suffix = "";
  while ($tocs{"$tocDir$suffix"}{"tocfile"}) { # OK, a toc.xml is defined
    my $tocFileName = "$tocDir/" . $tocs{"$tocDir$suffix"}{"tocfile"};
    open(TOCXML, "<$prefixPlugins/$tocFileName") || die "Could not open $tocFileName!\n";
    my $tocElement = 0;
    my $tocStartTag = 0;
    my $topicElement = 0;
    my $topicLabel = "";
    my $topicHREF = "";
    while (my $line = <TOCXML>) {
      if ($line =~ "<toc") {
        $tocElement++;
        $tocStartTag = 1;
      } elsif ($line =~ "</toc") {
        print PLUGINTOC "</ul>\n";
        $tocElement--;
      } elsif ($line =~ "<topic") {
        $topicElement = 1;
        $topicLabel = "";
        $topicHREF = "";
      }
      if ($tocStartTag == 1) {
        if ($line =~ m/label=[\"|\'](.*?)[\"|\']/) {
          my $label = $1;
          print PLUGINTOC "<h2>$label</h2>\n";
          print PLUGINTOC "<ul>\n";
        }
        if ($line =~ m/>/) {
          $tocStartTag = 0;
        }
      }
      if ($topicElement == 1) {
        if ($line =~ m/label=[\"|\'](.*?)[\"|\']/) {
          $topicLabel = $1;
        }
        if ($line =~ m/href=[\"|\'](.*?)[\"|\']/) {
          $topicHREF = $1;
        }
        if ($line =~ m/\/>/) {
          $topicElement = 0;
          print PLUGINTOC "  <li><a href=\"$topicHREF\">$topicLabel</a></li>\n";
          # make sure to copy things too
          if ($topicHREF =~ m/(.*)\//) {
            my $topicSrcDir = $tocDir."/".$1;
            my $topicTargetDir = "output/".$topicSrcDir;
            `mkdir -p $topicTargetDir`;
            # ok, the next might copy things too many time, but at least the
            # images get copied too. Now it just complaints about .svn permission
            # stuff :(
            `cp -R $prefixPlugins/$topicSrcDir/* $topicTargetDir/. > /dev/null`;
            `find $topicTargetDir/. -name ".svn" | xargs rm -Rf`;
          }
        }
      }
    }
    $suffix = $suffix . "#";
  }

  print PLUGINTOC "</body>\n";
  print PLUGINTOC "</html>\n";
}
print MAINTOC "  </ul>\n";
print MAINTOC "</body>\n";
print MAINTOC "</html>\n";

close(MAINTOC);
