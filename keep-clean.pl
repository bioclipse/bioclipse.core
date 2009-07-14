#!perl -w
use strict;
use Fatal qw(open close);

my @files = split "\n", `find plugins -name \*.java`;

for my $file (@files) {

    next if $file !~ /net\.bioclipse\./;   # not our codebase

    open my $FH, '<', $file;

    my @output;

    my $tabs_at_beginning = 0;
    my $empty_line_ws     = 0;
    for my $line (<$FH>) {
        if ($line =~ /^\t/) {
            ++$tabs_at_beginning;
            $line =~ s/^(\s*?)\t/$1        / while $line =~ /^(\s*?)\t/;
        }
        if ($line =~ /^\s+$/) {
            ++$empty_line_ws;
            $line = "\n";
        }

        push @output, $line;
    }
    close $FH;

    if ($tabs_at_beginning || $empty_line_ws) {

        open my $OFH, '>', $file;
        print {$OFH} join "", @output;
    }
}
